const test=require('node:test'),assert=require('node:assert/strict'),fs=require('node:fs'),os=require('node:os'),path=require('node:path');
const {validateModel}=require('../gguf.cjs');
const u32=n=>{let b=Buffer.alloc(4);b.writeUInt32LE(n);return b;};const u64=n=>{let b=Buffer.alloc(8);b.writeBigUInt64LE(BigInt(n));return b;};const str=s=>Buffer.concat([u64(Buffer.byteLength(s)),Buffer.from(s)]);
function fixture(arch){return Buffer.concat([Buffer.from('GGUF'),u32(3),u64(1),u64(1),str('general.architecture'),u32(8),str(arch)]);}
function withFile(data,fn){const dir=fs.mkdtempSync(path.join(os.tmpdir(),'engram-gguf-'));try{const p=path.join(dir,'renamed-model.gguf');fs.writeFileSync(p,data);return fn(p);}finally{fs.rmSync(dir,{recursive:true,force:true});}}
test('rejects renamed projector by metadata, not just filename',()=>withFile(fixture('clip'),p=>assert.throws(()=>validateModel(p),/projector/)));
test('rejects unsupported Gemma and Qwen architectures',()=>{for(const a of ['gemma4','qwen35moe'])withFile(fixture(a),p=>assert.throws(()=>validateModel(p),/runtime upgrade/));});
test('supported metadata explicitly does not claim inference',()=>withFile(fixture('llama'),p=>assert.match(validateModel(p).status,/inference not tested/)));
test('truncated and invalid files do not register',()=>{for(const b of [Buffer.from('GGUF'),Buffer.alloc(30),fixture('llama').subarray(0,40)])withFile(b,p=>assert.throws(()=>validateModel(p)));});
test('malicious lengths are rejected without large allocation',()=>withFile(Buffer.concat([Buffer.from('GGUF'),u32(3),u64(1),u64(1),u64(2**40)]),p=>assert.throws(()=>validateModel(p),/limit/)));
