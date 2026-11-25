const {contextBridge,ipcRenderer}=require('electron');
const names=['startup-ready','startup-failed','calendar-help','calendar-config','calendar-login','calendar-sync','calendar-disconnect','cancel-auth','engine-test','get','save','import','export','digest','chat','scenario','log','project','delete','calendar-import','calendar-reminders','calendar-clear','calendar-export','packet','prerequisite'];
const api=Object.fromEntries(names.map(name=>[name,(...args)=>ipcRenderer.invoke(name,...args)]));
api.onState=fn=>{const listener=(_e,s)=>fn(s);ipcRenderer.on('state',listener);return()=>ipcRenderer.removeListener('state',listener);};
api.onAuthProgress=fn=>{const l=(_e,m)=>fn(m);ipcRenderer.on('auth-progress',l);return()=>ipcRenderer.removeListener('auth-progress',l);};
contextBridge.exposeInMainWorld('engram',Object.freeze(api));
