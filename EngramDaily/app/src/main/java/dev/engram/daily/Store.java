package dev.engram.daily;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.*;
import java.text.SimpleDateFormat;
import java.util.*;

public final class Store {
    final SharedPreferences p;
    public Store(Context c) {
        p=c.getSharedPreferences("engram", Context.MODE_PRIVATE);
        if(!p.contains("bio")) {
            try(java.io.InputStream in=c.getAssets().open("jade.engram.json")) {
                JSONObject jade=new JSONObject(Engine.readBounded(in,32000));
                p.edit().putString("bio",jade.getString("bio")).putString("engramName",jade.getString("name")).apply();
            } catch(Exception e) { p.edit().putString("bio","You are Jade, a fictional companion who supports the user's autonomy with practical, caring guidance.").putString("engramName","Jade").apply(); }
        }
    }
    public String get(String k) { return p.getString(k, ""); }
    public void set(String k,String v) { p.edit().putString(k,v).apply(); }
    public String mode() { String m=get("mode"); return m.isEmpty()?"Planner":m; }
    public String bio() { String b=get("bio");return b.isEmpty()?"You are Jade, a grounded, caring simulated career companion. Support user choice.":b; }
    public JSONArray array(String k) { try{return new JSONArray(get(k));}catch(Exception e){return new JSONArray();} }
    public synchronized void add(String k,JSONObject value) {
        JSONArray old=array(k), next=new JSONArray();
        for(int i=Math.max(0,old.length()-99);i<old.length();i++) next.put(old.opt(i));
        next.put(value); set(k,next.toString());
    }
    public String context() {
        return "Career: "+get("career")+"\nGoals: "+get("goals")+"\nConstraints and preferences: "+get("constraints")
            +"\nUser-controlled memory: "+get("memory")+"\nUser-supplied reference notes (may be stale):\n"+get("notes");
    }
    public String prompt(String task) { return context()+"\n\nTask: "+task; }
    public String plannerDigest() {
        String career=get("career").trim(); if(career.isEmpty()) career="your chosen career";
        return get("engramName")+" • Let’s choose the next stretch of your road.\n\nDAILY PRACTICE • "+new SimpleDateFormat("EEE, d MMM",Locale.getDefault()).format(new Date())
            +"\n\nDirection\n"+career+"\n"+get("goals")
            +"\n\nChoose one next step\n• Spend 20 minutes practicing one relevant skill.\n• Create one small piece of evidence of your work.\n• Review a prior lesson from memory, then check it."
            +"\n\nReflect\nWhat felt useful? What would you change tomorrow? You choose the pace."
            +"\n\nYour reference notes\n"+(get("notes").isEmpty()?"No source notes imported.":get("notes"))
            +"\n\nSource: built-in planning template and your saved inputs. No live news or AI inference was used.";
    }
    public JSONObject export() throws JSONException {
        JSONObject o=new JSONObject();o.put("schemaVersion",1);
        for(String k:new String[]{"career","goals","constraints","memory","notes","bio","engramName","digest","digestDate"})o.put(k,get(k));
        o.put("sessions",array("sessions"));o.put("history",array("history"));return o;
    }
}
