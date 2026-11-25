package dev.engram.daily;

import android.Manifest;
import android.app.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.*;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {
    private final ExecutorService executor=Executors.newSingleThreadExecutor();
    private Store store;
    private LinearLayout root,body,nav;
    private String tab="Today",exportText="";
    private TextView status;
    private boolean working;
    private static final int BG=0xff10171e, CARD=0xff1b2732, INK=0xffedf4f7, MUTED=0xffa8bac9, TEAL=0xff74e4cf;
    @Override public void onCreate(Bundle state) {
        super.onCreate(state);store=new Store(this);
        if(state!=null){tab=state.getString("tab","Today");exportText=state.getString("export","");}
        render();
        if(getSystemService(android.app.job.JobScheduler.class).getPendingJob(Scheduler.JOB)==null)Scheduler.schedule(this);
    }
    @Override public void onSaveInstanceState(Bundle b){super.onSaveInstanceState(b);b.putString("tab",tab);b.putString("export",exportText);}
    @Override public void onDestroy(){executor.shutdown();super.onDestroy();}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density);}
    private GradientDrawable background(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(14));return d;}
    private void render() {
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(BG);
        root.setPadding(dp(18),dp(14),dp(18),dp(8));setContentView(root);
        root.setOnApplyWindowInsetsListener((v,insets)->{
            v.setPadding(dp(18)+insets.getSystemWindowInsetLeft(),dp(10)+insets.getSystemWindowInsetTop(),dp(18)+insets.getSystemWindowInsetRight(),dp(8)+insets.getSystemWindowInsetBottom());return insets;
        });root.requestApplyInsets();
        TextView brand=new TextView(this);brand.setText("E N G R A M  /  D A I L Y");brand.setTextSize(12);brand.setTextColor(TEAL);brand.setPadding(0,dp(8),0,dp(12));root.addView(brand);
        ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(0,0,0,dp(18));scroll.addView(body);
        nav=new LinearLayout(this);root.addView(nav);
        for(String name:new String[]{"Today","Profile","Engram","Lab","Settings"}) {
            TextView item=new TextView(this);item.setText(name);item.setGravity(Gravity.CENTER);item.setTextSize(12);item.setTextColor(name.equals(tab)?TEAL:MUTED);item.setPadding(0,dp(18),0,dp(18));
            nav.addView(item,new LinearLayout.LayoutParams(0,-2,1));item.setOnClickListener(v->{if(!working){tab=name;render();}});
        }
        switch(tab){case "Profile":profile();break;case "Engram":engram();break;case "Lab":lab();break;case "Settings":settings();break;default:today();}
        status=text("",13,MUTED);status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
    }
    private TextView text(String value,int size,int color){TextView v=new TextView(this);v.setText(value);v.setTextColor(color);v.setTextSize(size);v.setPadding(0,dp(7),0,dp(7));v.setTextIsSelectable(true);body.addView(v);return v;}
    private void title(String value,String sub){TextView t=text(value,30,INK);t.setTypeface(null,Typeface.BOLD);text(sub,14,MUTED);}
    private void section(String value){TextView t=text(value,18,TEAL);t.setTypeface(null,Typeface.BOLD);}
    private void card(String value){TextView t=text(value,16,INK);t.setPadding(dp(16),dp(16),dp(16),dp(16));t.setBackground(background(CARD));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(8),0,dp(8));t.setLayoutParams(lp);}
    private EditText field(String label,String value,boolean multi){text(label,13,MUTED);EditText e=new EditText(this);e.setTextColor(INK);e.setTextSize(16);e.setHintTextColor(MUTED);e.setText(value);e.setPadding(dp(12),dp(8),dp(12),dp(8));e.setBackground(background(CARD));e.setSingleLine(!multi);e.setGravity(Gravity.TOP);if(multi){e.setMinLines(3);e.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);}e.setFilters(new android.text.InputFilter[]{new android.text.InputFilter.LengthFilter(multi?6000:500)});body.addView(e,new LinearLayout.LayoutParams(-1,-2));return e;}
    private void button(String label,Runnable action){Button b=new Button(this);b.setText(label);b.setAllCaps(false);b.setTextColor(BG);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(TEAL));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(52));lp.setMargins(0,dp(10),0,0);body.addView(b,lp);b.setOnClickListener(v->{if(!working)action.run();});}
    private void note(String s){if(status!=null)status.setText(s);Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    private String val(EditText e){return e.getText().toString().trim();}
    private void task(Callable<String> work,java.util.function.Consumer<String> success){
        working=true;status.setText("Working… You can leave the app while this finishes.");
        executor.submit(()->{try{String answer=work.call();runOnUiThread(()->{if(isDestroyed())return;working=false;success.accept(answer);});}
        catch(Exception|LinkageError e){runOnUiThread(()->{if(isDestroyed())return;working=false;note(e.getMessage()==null?"Unable to finish. Check settings and try again.":e.getMessage());});}});
    }
    private void today(){
        title("Make room for growth.","Your direction. Your pace. Your companion.");
        card((store.get("career").isEmpty()?"Choose a career in Profile":store.get("career"))+"\nEngine: "+store.mode());
        if(!store.get("jobError").isEmpty())text("Last scheduled attempt: "+store.get("jobError"),14,0xffffcc99);
        button("Create today's digest",()->{
            if(store.get("career").isEmpty()){note("Add your career in Profile first.");return;}
            task(()->{String result=Engine.run(this,Engine.DIGEST);store.set("digest",result);store.set("digestDate",Instant.now().toString()+" • "+store.mode());return result;},x->render());
        });
        if(!store.get("digest").isEmpty()){
            text(store.get("digestDate"),12,MUTED);card(store.get("digest"));
            button("Share digest…",()->{Intent i=new Intent(Intent.ACTION_SEND);i.setType("text/plain");i.putExtra(Intent.EXTRA_TEXT,store.get("digest"));startActivity(Intent.createChooser(i,"Share your digest"));});
            button("Export digest",()->export("engram-digest.txt",store.get("digest"),"text/plain"));
        }else card("A daily digest brings together your goals, reference notes, a practice exercise, and one useful question. Start with the built-in planner or connect a model.");
        text("Generated suggestions can be mistaken. Imported notes are reference material, not verified live news.",12,MUTED);
    }
    private void profile(){
        title("Choose your direction.","Any career. Edit your goals as you learn.");
        EditText career=field("Career or role",store.get("career"),false),goals=field("Goals and current skills",store.get("goals"),true),constraints=field("Time, values, accessibility and other preferences",store.get("constraints"),true),memory=field("Memory • exactly what the companion may remember",store.get("memory"),true),notes=field("Reference notes • include source names, dates and URLs",store.get("notes"),true);
        button("Save profile",()->{store.set("career",val(career));store.set("goals",val(goals));store.set("constraints",val(constraints));store.set("memory",val(memory));store.set("notes",val(notes));note("Profile saved on this device.");});
        button("Import reference text (.txt / .md)",()->pick(12,"text/*"));
        text("Save edits before switching tabs or importing. Memory is edited by you; conversations never silently add to it. API mode sends these saved fields and your engram to your chosen provider.",13,MUTED);
    }
    private void engram(){
        title("Meet your engram.","A character biography, carried across models.");
        EditText name=field("Name",store.get("engramName").isEmpty()?"Jade":store.get("engramName"),false),bio=field("Character bio, tone and boundaries",store.bio(),true);
        button("Save engram",()->{store.set("engramName",val(name));store.set("bio",val(bio));note("Engram saved.");});
        button("Import engram JSON",()->pick(10,"*/*"));
        button("Export engram JSON",()->{try{export("engram.json",new JSONObject().put("schemaVersion",1).put("name",val(name)).put("bio",val(bio)).toString(2),"application/json");}catch(Exception e){note("Could not export engram.");}});
        section("Conversation");
        text("This is a simulated companion. You decide what to keep, change, or act on. Planner mode supports digests; conversations need API or GGUF mode.",13,MUTED);
        JSONArray history=store.array("history");for(int i=Math.max(0,history.length()-8);i<history.length();i++){JSONObject item=history.optJSONObject(i);if(item!=null)card(item.optString("role")+"\n"+item.optString("text"));}
        EditText message=field("Ask, reflect, or rehearse a career conversation","",true);
        CheckBox remember=new CheckBox(this);remember.setText("Save this exchange on this device");remember.setTextColor(INK);body.addView(remember);
        button("Send to companion",()->{
            if(store.mode().equals("Planner")){note("Choose API or GGUF in Settings to chat.");return;}
            String q=val(message);if(q.isEmpty()){note("Enter a message first.");return;}
            boolean save=remember.isChecked();
            task(()->{
                StringBuilder recent=new StringBuilder();JSONArray h=store.array("history");
                for(int i=Math.max(0,h.length()-6);i<h.length();i++){JSONObject o=h.optJSONObject(i);if(o!=null)recent.append(o.optString("role")).append(": ").append(o.optString("text")).append("\n");}
                String answer=Engine.run(this,"Conversation context (saved exchanges only):\n"+recent+"\nCurrent user message: "+q);
                if(save){store.add("history",new JSONObject().put("role","You").put("text",q));store.add("history",new JSONObject().put("role",store.get("engramName").isEmpty()?"Jade":store.get("engramName")).put("text",answer));}
                return answer;
            },answer->{render();if(!save){section("Unsaved reply");card(answer);}note("Reply ready.");});
        });
        button("Delete saved conversations",()->new AlertDialog.Builder(this).setMessage("Delete all saved exchanges?").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{store.set("history","");render();}).show());
    }
    private void lab(){
        title("Practice. Observe. Adjust.","Track your effort and explore explicit assumptions.");
        JSONArray sessions=store.array("sessions");double minutes=0;int count=0;long cutoff=System.currentTimeMillis()-7L*24*60*60*1000;
        for(int i=0;i<sessions.length();i++){JSONObject o=sessions.optJSONObject(i);if(o!=null&&o.optLong("at")>=cutoff){minutes+=o.optDouble("minutes");count++;}}
        card(String.format(Locale.getDefault(),"LAST 7 DAYS\n%.1f hours practiced  •  %d sessions",minutes/60,count));
        EditText skill=field("Skill practiced","",false),duration=field("Minutes practiced","20",false);duration.setInputType(InputType.TYPE_CLASS_NUMBER);
        button("Log practice",()->{try{int m=Integer.parseInt(val(duration));if(m<1||m>1440||val(skill).isEmpty())throw new IllegalArgumentException();store.add("sessions",new JSONObject().put("at",System.currentTimeMillis()).put("skill",val(skill)).put("minutes",m));render();}catch(Exception e){note("Enter a skill and 1–1440 minutes.");}});
        section("Scenario calculator");
        EditText hours=field("Available practice hours per week","5",false),weeks=field("Weeks","12",false),adherence=field("Assumed adherence (%)","80",false);
        for(EditText e:new EditText[]{hours,adherence})e.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);weeks.setInputType(InputType.TYPE_CLASS_NUMBER);
        button("Calculate scenario",()->{try{double result=Planner.projectedHours(Double.parseDouble(val(hours)),Integer.parseInt(val(weeks)),Double.parseDouble(val(adherence))/100);note(String.format(Locale.getDefault(),"Scenario: %.1f total practice hours. This does not predict hiring or skill attainment.",result));}catch(Exception e){note("Use 0–168 hours/week, 1–520 weeks, and 0–100% adherence.");}});
        EditText scenario=field("Career situation to rehearse","",true);
        button("Explore with companion",()->{
            if(store.mode().equals("Planner")){note("Choose API or GGUF in Settings for assisted scenarios.");return;}
            if(val(scenario).isEmpty()){note("Describe a situation first.");return;}
            String situation=val(scenario);
            task(()->Engine.run(this,"SIMULATION, not a forecast. Explore this career situation: "+situation+". State assumptions, give two options with tradeoffs, then offer a roleplay exercise. Do not invent numerical likelihoods."),answer->{new AlertDialog.Builder(this).setTitle("Simulated scenario").setMessage(answer).setPositiveButton("Close",null).show();status.setText("Simulation complete.");});
        });
        section("Recent practice");for(int i=sessions.length()-1;i>=Math.max(0,sessions.length()-10);i--){JSONObject o=sessions.optJSONObject(i);if(o!=null)text(o.optString("skill")+" • "+o.optInt("minutes")+" min • "+Instant.ofEpochMilli(o.optLong("at")).atZone(ZoneId.systemDefault()).toLocalDate(),14,INK);}
        button("Export practice CSV",()->{StringBuilder csv=new StringBuilder("date,skill,minutes\r\n");for(int i=0;i<sessions.length();i++){JSONObject o=sessions.optJSONObject(i);if(o!=null)csv.append(Instant.ofEpochMilli(o.optLong("at"))).append(',').append(csvCell(o.optString("skill"))).append(',').append(o.optInt("minutes")).append("\r\n");}export("practice.csv",csv.toString(),"text/csv");});
    }
    static String csvCell(String s){if(s.matches("^[=+@\\-\\t\\r].*"))s="'"+s;return "\""+s.replace("\"","\"\"")+"\"";}
    private void settings(){
        title("Your controls.","Choose where inference happens and what leaves your device.");
        text("Engine",13,MUTED);Spinner mode=new Spinner(this);String[] options={"Planner","GGUF","API"};ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,options);mode.setAdapter(adapter);mode.setSelection(Arrays.asList(options).indexOf(store.mode()));body.addView(mode);
        text("Planner: built-in offline template. GGUF: on-device model. API: sends saved profile, memory, notes, bio and task to your provider; chat also includes recent saved exchanges.",13,MUTED);
        EditText endpoint=field("Full HTTPS chat-completions endpoint",store.get("endpoint"),false),model=field("API model identifier",store.get("model"),false),key=field("API key • blank keeps the saved key","",false);key.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        CheckBox consent=new CheckBox(this);consent.setText("Allow this data to be sent to the configured API");consent.setTextColor(INK);consent.setChecked(store.get("cloudConsent").equals("yes"));body.addView(consent);
        button("Save engine settings",()->{try{
            String selected=mode.getSelectedItem().toString();
            if(selected.equals("API")){java.net.URL u=new java.net.URL(val(endpoint));if(!u.getProtocol().equals("https")||u.getHost().isEmpty()||u.getUserInfo()!=null||val(model).isEmpty()||!consent.isChecked())throw new IllegalArgumentException("Enter a valid HTTPS endpoint and model, and enable data sharing.");}
            if(!val(key).isEmpty())Vault.save(this,val(key));store.set("mode",selected);store.set("endpoint",val(endpoint));store.set("model",val(model));store.set("cloudConsent",consent.isChecked()?"yes":"no");Scheduler.schedule(this);key.setText("");note("Engine settings saved.");
        }catch(Exception e){note(e.getMessage()==null?"Could not save settings.":e.getMessage());}});
        button("Remove saved API key",()->{try{Vault.erase(this);note("API key removed.");}catch(Exception e){note("Could not remove key.");}});
        section("Local model");File modelFile=new File(getFilesDir(),"model.gguf");text(modelFile.exists()?"Imported model: "+store.get("modelName")+" • "+(modelFile.length()/1024/1024)+" MiB":"No GGUF model imported.",13,MUTED);
        text("Use a small text-instruct GGUF supported by the bundled llama.cpp runtime. The file is copied into app storage. Available RAM, architecture and chat template determine compatibility. Newer GGUF models may require a runtime upgrade.",13,MUTED);
        button("Import GGUF model",()->pick(11,"*/*"));
        button("Test saved engine and engram",()->{if(store.mode().equals("Planner")){note("Choose GGUF or API and save first.");return;}task(()->Engine.run(this,"Connection test: give one short greeting in your character voice."),answer->{new AlertDialog.Builder(this).setTitle("Generation succeeded").setMessage(answer).setPositiveButton("OK",null).show();});});
        button("Delete imported model",()->{if(!Engine.BUSY.compareAndSet(false,true)){note("Wait for current generation to finish.");return;}try{if(modelFile.exists()&&!modelFile.delete()){note("Unable to delete model.");return;}store.set("modelName","");render();}finally{Engine.BUSY.set(false);}});
        section("Daily delivery");text("Local notification with a digest generated near the selected device-local time. Android may delay background work. API mode requires connectivity. Notifications contain no profile details.",13,MUTED);
        EditText time=field("Time (24-hour HH:mm)",store.get("time").isEmpty()?"09:00":store.get("time"),false);
        CheckBox schedule=new CheckBox(this);schedule.setText("Generate a daily digest and notify me");schedule.setTextColor(INK);schedule.setChecked(store.get("scheduled").equals("yes"));body.addView(schedule);
        button("Save schedule",()->{try{String t=val(time);if(!t.matches("(?:[01]\\d|2[0-3]):[0-5]\\d"))throw new IllegalArgumentException();store.set("time",t);store.set("scheduled",schedule.isChecked()?"yes":"no");if(schedule.isChecked()&&Build.VERSION.SDK_INT>=33&&checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},20);Scheduler.schedule(this);note("Schedule saved. Background delivery time is approximate.");}catch(Exception e){note("Enter a valid time such as 09:00.");}});
        section("Your data");
        text("Profiles and history stay in this app's private storage; Android backup is disabled. The API key is encrypted with Android Keystore. Exports contain personal data but never API keys or model weights. Each log retains the most recent 100 entries.",13,MUTED);
        button("Export personal data JSON",()->{try{export("engram-data.json",store.export().toString(2),"application/json");}catch(Exception e){note("Export failed.");}});
        button("Erase all app data",()->new AlertDialog.Builder(this).setTitle("Erase this device's app data?").setMessage("Deletes profile, memory, history, practice logs, API key and imported model. Export first if needed.").setNegativeButton("Cancel",null).setPositiveButton("Erase",(d,w)->{if(Engine.BUSY.get()){note("Wait for generation to finish before erasing data.");return;}((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();}).show());
    }
    private void pick(int request,String mime){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT).setType(mime).addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,request);}
    private void export(String name,String text,String mime){exportText=text;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT).setType(mime).addCategory(Intent.CATEGORY_OPENABLE).putExtra(Intent.EXTRA_TITLE,name);startActivityForResult(i,13);}
    @Override public void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(result!=RESULT_OK||data==null||data.getData()==null)return;Uri uri=data.getData();
        if(request==13){task(()->{try(OutputStream out=getContentResolver().openOutputStream(uri,"wt")){if(out==null)throw new IOException("Cannot open export destination.");out.write(exportText.getBytes(StandardCharsets.UTF_8));}return "Export saved.";},this::note);return;}
        task(()->{
            if(request==11){
                if(!Engine.BUSY.compareAndSet(false,true))throw new IOException("Wait for generation to finish before importing a model.");
                File tmp=new File(getFilesDir(),"model.import"),dest=new File(getFilesDir(),"model.gguf");
                try(InputStream in=getContentResolver().openInputStream(uri);OutputStream out=new FileOutputStream(tmp)){
                    if(in==null)throw new IOException("Cannot open model.");byte[] magic=new byte[4];int offset=0,n;while(offset<4&&(n=in.read(magic,offset,4-offset))>0)offset+=n;
                    if(offset!=4||!Arrays.equals(magic,new byte[]{'G','G','U','F'}))throw new IOException("This file is not a GGUF model.");out.write(magic);
                    byte[] buffer=new byte[1024*1024];while((n=in.read(buffer))!=-1){if(getFilesDir().getUsableSpace()<n+100L*1024*1024)throw new IOException("Not enough free storage for this model.");out.write(buffer,0,n);}
                    out.flush();
                }catch(Exception e){tmp.delete();Engine.BUSY.set(false);throw e;}
                try{Gguf.validate(tmp);java.nio.file.Files.move(tmp.toPath(),dest.toPath(),java.nio.file.StandardCopyOption.REPLACE_EXISTING,java.nio.file.StandardCopyOption.ATOMIC_MOVE);store.set("modelName","model.gguf");}
                finally{tmp.delete();Engine.BUSY.set(false);}return "Model metadata checked. Choose GGUF, save, then test generation; inference is not yet verified.";
            }
            String raw;try(InputStream in=getContentResolver().openInputStream(uri)){if(in==null)throw new IOException("Cannot read file.");raw=Engine.readBounded(in,32000);}
            if(request==10){JSONObject o=new JSONObject(raw);if(o.optInt("schemaVersion")!=1||!(o.opt("name") instanceof String)||!(o.opt("bio") instanceof String)||o.optString("bio").trim().isEmpty()||o.getString("bio").length()>6000||o.optString("name").length()>500)throw new IOException("Use engram schemaVersion 1 with name and bio (up to 6000 characters).");store.set("bio",o.getString("bio"));store.set("engramName",o.optString("name","Companion"));return "Engram imported.";}
            if(raw.length()>6000)throw new IOException("Use a reference excerpt of at most 6000 characters.");store.set("notes",raw);return "Reference notes imported.";
        },message->{render();note(message);});
    }
}
