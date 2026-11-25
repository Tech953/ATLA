const fs=require('node:fs'),path=require('node:path'),{spawnSync}=require('node:child_process');
const root=path.join(__dirname,'..');process.chdir(root);
const builder=require.resolve('electron-builder/cli.js');
const args=[builder,'--win','--x64','--dir','--publish','never'];if(process.env.ENGRAM_ELECTRON_DIST)args.push('--config.electronDist='+process.env.ENGRAM_ELECTRON_DIST);
let r=spawnSync(process.execPath,args,{stdio:'inherit',env:process.env});if(r.status!==0)process.exit(r.status||1);
const appdir=path.join(root,'dist','win-unpacked'),lines=[],dirs=[];
function walk(dir){for(const f of fs.readdirSync(dir,{withFileTypes:true})){const full=path.join(dir,f.name),relative=path.relative(appdir,full).replaceAll(path.sep,'\\');if(f.isDirectory()){walk(full);dirs.push(relative);}else lines.push('Delete "$INSTDIR\\'+relative.replaceAll('$','$$')+'"');}}
walk(appdir);lines.push(...dirs.map(d=>'RMDir "$INSTDIR\\'+d.replaceAll('$','$$')+'"'));
const inc=path.join(root,'dist','uninstall-files.nsh');fs.writeFileSync(inc,lines.join('\n'));
const flag=process.platform==='win32'?'/D':'-D';const defs={APPDIR:appdir,OUTPUT:path.join(root,'dist','Engram-Daily-Jade-Setup-0.2.1-x64.exe'),ICON:path.join(root,'assets','icon.ico'),UNINSTALL_INCLUDE:inc};
const nsisEnv={...process.env};if(process.env.MAKENSIS&&!nsisEnv.NSISDIR){const candidate=path.resolve(path.dirname(process.env.MAKENSIS),'..');if(fs.existsSync(path.join(candidate,'Stubs')))nsisEnv.NSISDIR=candidate;}
r=spawnSync(process.env.MAKENSIS||'makensis',[...Object.entries(defs).map(([k,v])=>flag+k+'='+v),path.join(root,'installer.nsi')],{stdio:'inherit',env:nsisEnv});if(r.error)console.error('Install NSIS 3 and put makensis on PATH, or set MAKENSIS to its full path.');process.exit(r.status|| (r.error?1:0));
