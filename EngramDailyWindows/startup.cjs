const fs=require('node:fs'),path=require('node:path');
function mergeState(defaults,saved){
 if(!saved||typeof saved!=='object'||Array.isArray(saved)||saved.version!==1)throw Error('Unsupported saved-state format.');
 const result={...defaults,...saved};
 for(const key of ['profile','engram','settings','schedule','integrations']){
   if(saved[key]!=null&&(typeof saved[key]!=='object'||Array.isArray(saved[key])))throw Error('Invalid saved '+key+'.');
   result[key]={...defaults[key],...(saved[key]||{})};
 }
 for(const key of ['history','sessions','calendars','notified']){
   if(saved[key]!=null&&!Array.isArray(saved[key]))throw Error('Invalid saved '+key+'.');
   result[key]=saved[key]||defaults[key];
 }
 for(const key of Object.keys(defaults.profile))if(typeof result.profile[key]!=='string')throw Error('Invalid saved profile field.');
 if(typeof result.engram.name!=='string'||typeof result.engram.bio!=='string')throw Error('Invalid saved engram.');
 return result;
}
function createStartup(electron,{base,load,show}){
 const {app,dialog,Menu}=electron;
 const file=path.join(app.getPath('userData'),'startup-diagnostics.json');
 const report={version:app.getVersion(),platform:process.platform,arch:process.arch,electron:process.versions.electron,softwareRendering:true,events:[]};
 function record(event,details={}){report.events.push({at:new Date().toISOString(),event,...details});report.events=report.events.slice(-40);try{fs.mkdirSync(path.dirname(file),{recursive:true});fs.writeFileSync(file,JSON.stringify(report,null,2),{mode:0o600});}catch{}}
 let ready=false,watchdog,shown=false;
 function failure(code){record(code);clearTimeout(watchdog);if(shown)return;shown=true;show();dialog.showMessageBox({type:'error',title:'Engram Daily could not open its interface',message:'The interface did not finish starting.',detail:'Use Help → Save startup diagnostics to save a report. Your profile and model files have not been deleted.\n\nStartup code: '+code,buttons:['Close message']}).catch(()=>{});}
 function pending(){ready=false;shown=false;clearTimeout(watchdog);watchdog=setTimeout(()=>{if(!ready)failure('UI_START_TIMEOUT');},20000);watchdog.unref?.();}
 function attach(win){
   win.setMenuBarVisibility(true);
   Menu.setApplicationMenu(Menu.buildFromTemplate([{label:'View',submenu:[{label:'Reload interface',accelerator:'CmdOrCtrl+R',click:()=>{pending();load().catch(()=>failure('PAGE_LOAD_FAILED'));}},{role:'resetZoom'},{role:'zoomIn'},{role:'zoomOut'}]},{label:'Help',submenu:[{label:'Save startup diagnostics',click:async()=>{const out=await dialog.showSaveDialog(win,{defaultPath:'Engram-startup-diagnostics.json'});if(!out.canceled&&out.filePath)fs.writeFileSync(out.filePath,JSON.stringify(report,null,2));}},{label:'About Engram Daily',click:()=>dialog.showMessageBox(win,{message:'Engram Daily · Jade '+app.getVersion(),detail:'Software rendering enabled. No GGUF model is needed to open the interface.'})}]}]));
   win.webContents.on('preload-error',()=>failure('PRELOAD_FAILED'));
   win.webContents.on('did-fail-load',(_e,code,_description,_url,main)=>{if(main&&code!==-3)failure('PAGE_LOAD_FAILED_'+code);});
   win.webContents.on('render-process-gone',(_e,details)=>failure('RENDERER_'+String(details.reason).toUpperCase()));
   win.webContents.on('unresponsive',()=>failure('RENDERER_UNRESPONSIVE'));
   win.webContents.on('did-finish-load',()=>record('PAGE_LOADED'));
   win.on('closed',()=>clearTimeout(watchdog));pending();record('WINDOW_CREATED');
 }
 function uiReady(){ready=true;clearTimeout(watchdog);record('UI_READY');return true;}
 function uiFailure(code){failure(['BRIDGE_MISSING','STATE_LOAD_FAILED','RENDER_FAILED','SCRIPT_FAILED'].includes(code)?code:'UI_FAILED');}
 return {attach,record,uiReady,uiFailure,failure};
}
module.exports={mergeState,createStartup};
