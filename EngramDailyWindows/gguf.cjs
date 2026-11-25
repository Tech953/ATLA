const fs=require('node:fs');
const supported=new Set(require('./architectures.json'));
function validateModel(file){
 const fd=fs.openSync(file,'r');let pos=0;const size=fs.fstatSync(fd).size;
 function read(n){if(!Number.isSafeInteger(n)||n<0||pos+n>size||pos+n>32*1024*1024)throw Error('Invalid, truncated or oversized GGUF metadata.');const b=Buffer.alloc(n);if(fs.readSync(fd,b,0,n,pos)!==n)throw Error('Truncated GGUF.');pos+=n;return b;}
 const u32=()=>read(4).readUInt32LE();const u64=()=>{const n=read(8).readBigUInt64LE();if(n>BigInt(Number.MAX_SAFE_INTEGER))throw Error('Invalid GGUF length.');return Number(n);};
 const str=()=>{const n=u64();if(n>1024*1024)throw Error('GGUF metadata string exceeds limit.');return read(n).toString('utf8');};
 function skip(t,depth=0){const widths={0:1,1:1,2:2,3:2,4:4,5:4,6:4,7:1,10:8,11:8,12:8};if(widths[t]){read(widths[t]);return;}if(t===8){str();return;}if(t===9&&depth===0){const type=u32(),n=u64();if(n>1000000)throw Error('GGUF array exceeds limit.');for(let i=0;i<n;i++)skip(type,1);return;}throw Error('Invalid GGUF metadata type.');}
 try{if(read(4).toString()!=='GGUF')throw Error('Not a GGUF file.');const version=u32();if(version!==2&&version!==3)throw Error('Unsupported GGUF version.');const tensors=u64(),count=u64();if(!tensors||count>100000)throw Error('Invalid GGUF model header.');for(let i=0;i<count;i++){const key=str(),type=u32();if(key==='general.architecture'){if(type!==8)throw Error('Invalid architecture metadata.');const architecture=str();if(architecture==='clip'||architecture==='mmproj')throw Error('This is a multimedia projector (mmproj), not a chat model. Select the companion language-model GGUF.');if(!supported.has(architecture))throw Error('Architecture '+architecture+' is unsupported by bundled llama.cpp b5046. A runtime upgrade is required; registration does not establish compatibility.');return {architecture,bytes:size,status:'Metadata checked; inference not tested'};}skip(type);}throw Error('GGUF has no architecture metadata.');}finally{fs.closeSync(fd);}
}
module.exports={validateModel};
