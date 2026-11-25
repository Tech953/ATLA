#include <jni.h>
#include "llama.h"
#include <algorithm>
#include <chrono>
#include <memory>
#include <mutex>
#include <stdexcept>
#include <string>
#include <thread>
#include <vector>

namespace {
using Clock=std::chrono::steady_clock;
struct Deadline { Clock::time_point end=Clock::now()+std::chrono::seconds(100); };
bool expired(void* ptr) { return Clock::now()>=static_cast<Deadline*>(ptr)->end; }
bool loading(float,void* ptr) { return !expired(ptr); }
std::string bytes(JNIEnv* env,jbyteArray a) {
    std::string s(env->GetArrayLength(a),'\0');
    env->GetByteArrayRegion(a,0,static_cast<jsize>(s.size()),reinterpret_cast<jbyte*>(s.data()));return s;
}
}
extern "C" JNIEXPORT jbyteArray JNICALL
Java_dev_engram_daily_LocalModel_generateNative(JNIEnv* env,jclass,jstring path,jbyteArray sys,jbyteArray usr) {
    try {
        static std::once_flag initialized;
        std::call_once(initialized,[]{llama_backend_init();});
        const char* chars=env->GetStringUTFChars(path,nullptr);
        if(!chars) return nullptr;
        std::string filename(chars);env->ReleaseStringUTFChars(path,chars);
        Deadline deadline;
        auto mp=llama_model_default_params();mp.n_gpu_layers=0;mp.use_mmap=true;
        mp.progress_callback=loading;mp.progress_callback_user_data=&deadline;
        std::unique_ptr<llama_model,decltype(&llama_model_free)> model(llama_model_load_from_file(filename.c_str(),mp),llama_model_free);
        if(!model)throw std::runtime_error("Cannot load GGUF: incompatible file, insufficient memory, or load timeout. Try a smaller supported text-instruct model.");
        const auto* vocab=llama_model_get_vocab(model.get());
        std::string system=bytes(env,sys),user=bytes(env,usr);
        const char* tmpl=llama_model_chat_template(model.get(),nullptr);
        if(!tmpl)throw std::runtime_error("This GGUF has no chat template. Import an instruction model with a supported template.");
        llama_chat_message messages[]={{"system",system.c_str()},{"user",user.c_str()}};
        int n=llama_chat_apply_template(tmpl,messages,2,true,nullptr,0);
        if(n<=0)throw std::runtime_error("Unsupported model chat template in this runtime.");
        std::vector<char> formatted(n+1);
        int written=llama_chat_apply_template(tmpl,messages,2,true,formatted.data(),static_cast<int32_t>(formatted.size()));
        if(written<=0 || written>n)throw std::runtime_error("Could not format prompt.");
        int count=-llama_tokenize(vocab,formatted.data(),written,nullptr,0,true,true);
        constexpr int context=2048,outputLimit=384;
        if(count<=0||count>context-outputLimit)throw std::runtime_error("Prompt exceeds local context budget. Shorten bio, notes, memory or saved conversation.");
        std::vector<llama_token> tokens(count);
        if(llama_tokenize(vocab,formatted.data(),written,tokens.data(),count,true,true)!=count)throw std::runtime_error("Tokenization failed.");
        auto cp=llama_context_default_params();cp.n_ctx=context;cp.n_batch=256;cp.n_ubatch=256;
        cp.n_threads=static_cast<int32_t>(std::min(4u,std::max(1u,std::thread::hardware_concurrency())));cp.n_threads_batch=cp.n_threads;
        cp.abort_callback=expired;cp.abort_callback_data=&deadline;
        std::unique_ptr<llama_context,decltype(&llama_free)> ctx(llama_init_from_model(model.get(),cp),llama_free);
        if(!ctx)throw std::runtime_error("Not enough memory to create local model context.");
        for(int start=0;start<count;start+=256){
            if(expired(&deadline))throw std::runtime_error("Local generation timed out. Try a smaller model.");
            auto batch=llama_batch_get_one(tokens.data()+start,std::min(256,count-start));
            if(llama_decode(ctx.get(),batch)!=0)throw std::runtime_error("Local prompt evaluation failed or timed out.");
        }
        std::unique_ptr<llama_sampler,decltype(&llama_sampler_free)> sampler(llama_sampler_init_greedy(),llama_sampler_free);
        std::string result;bool ended=false;
        for(int i=0;i<outputLimit;i++){
            if(expired(&deadline))throw std::runtime_error("Local generation timed out. Try a smaller model.");
            llama_token token=llama_sampler_sample(sampler.get(),ctx.get(),-1);
            if(llama_vocab_is_eog(vocab,token)){ended=true;break;}
            char piece[256];int len=llama_token_to_piece(vocab,token,piece,sizeof(piece),0,false);
            if(len>=0)result.append(piece,len);
            else{std::vector<char> bigger(-len);len=llama_token_to_piece(vocab,token,bigger.data(),static_cast<int32_t>(bigger.size()),0,false);if(len<0)throw std::runtime_error("Token decoding failed.");result.append(bigger.data(),len);}
            if(llama_decode(ctx.get(),llama_batch_get_one(&token,1))!=0)throw std::runtime_error("Local generation failed or timed out.");
        }
        if(result.empty())throw std::runtime_error("Model returned no text. Check model compatibility.");
        if(!ended)result+="\n\n[Local output limit reached.]";
        auto out=env->NewByteArray(static_cast<jsize>(result.size()));
        if(out)env->SetByteArrayRegion(out,0,static_cast<jsize>(result.size()),reinterpret_cast<const jbyte*>(result.data()));
        return out;
    } catch(const std::exception& ex){env->ThrowNew(env->FindClass("java/lang/IllegalStateException"),ex.what());return nullptr;}
    catch(...){env->ThrowNew(env->FindClass("java/lang/IllegalStateException"),"Local inference failed.");return nullptr;}
}
