'use strict';
(()=>{
 let ready=false;
 const box=()=>document.getElementById('startup-message');
 const explain=code=>{
   if(ready)return;
   const node=box();if(node){node.hidden=false;node.replaceChildren();const title=document.createElement('h1');title.textContent='The interface could not start.';const text=document.createElement('p');text.textContent='Use View → Reload interface, or Help → Save startup diagnostics. Your saved data has not been deleted. Startup code: '+code;node.append(title,text);}
   window.engram?.['startup-failed']?.(code).catch(()=>{});
 };
 window.addEventListener('error',()=>explain('SCRIPT_FAILED'));
 window.addEventListener('unhandledrejection',()=>explain('SCRIPT_FAILED'));
 window.jadeStartup={ready(){ready=true;const node=box();if(node)node.hidden=true;window.engram?.['startup-ready']?.().catch(()=>{});},fail:explain};
 window.addEventListener('DOMContentLoaded',()=>{if(!window.engram)explain('BRIDGE_MISSING');});
})();
