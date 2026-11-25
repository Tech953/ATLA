package dev.engram.daily;
import android.content.Context;
import org.json.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

final class Engine {
    static final AtomicBoolean BUSY=new AtomicBoolean(false);
    static final String DIGEST="Prepare a concise daily career digest: priorities, a 20-minute practice, spaced-review prompt, and reflection. Summarize supplied notes with their source labels. If no current sources exist, say so. Do not invent opportunities or statistics.";
    static String run(Context c,String task) throws Exception {
        if(!BUSY.compareAndSet(false,true))throw new IOException("Another generation is running. Try again shortly.");
        try {
            Store s=new Store(c);String system=Planner.system(s.bio()), user=s.prompt(task);
            if(s.mode().equals("Planner"))return s.plannerDigest();
            if(s.mode().equals("GGUF")) {
                File f=new File(c.getFilesDir(),"model.gguf");
                if(!f.exists())throw new IOException("Import a compatible GGUF model in Settings first.");
                Gguf.validate(f);
                return LocalModel.generate(f.getAbsolutePath(),system,user);
            }
            if(!s.get("cloudConsent").equals("yes"))throw new IOException("Enable API data sharing in Settings first.");
            URL url=new URL(s.get("endpoint"));
            if(!url.getProtocol().equals("https") || url.getHost().isEmpty() || url.getUserInfo()!=null)
                throw new IOException("Use an HTTPS chat-completions endpoint without embedded credentials.");
            if(s.get("model").trim().isEmpty())throw new IOException("Enter your API model identifier.");
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            try {
                conn.setInstanceFollowRedirects(false);conn.setConnectTimeout(15000);conn.setReadTimeout(90000);
                conn.setRequestMethod("POST");conn.setDoOutput(true);conn.setRequestProperty("Content-Type","application/json");
                String secret=Vault.read(c);if(!secret.isEmpty())conn.setRequestProperty("Authorization","Bearer "+secret);
                JSONArray messages=new JSONArray().put(new JSONObject().put("role","system").put("content",system))
                    .put(new JSONObject().put("role","user").put("content",user));
                byte[] body=new JSONObject().put("model",s.get("model")).put("messages",messages).put("max_tokens",700).put("stream",false).toString().getBytes(StandardCharsets.UTF_8);
                conn.setFixedLengthStreamingMode(body.length);
                try(OutputStream out=conn.getOutputStream()){out.write(body);}
                int status=conn.getResponseCode();if(status<200||status>=300)throw new IOException("API returned HTTP "+status+". Check endpoint, model, credentials, and provider quota.");
                String response;
                try(InputStream in=conn.getInputStream()){response=readBounded(in,2_000_000);}
                String result=new JSONObject(response).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content");
                if(result.trim().isEmpty())throw new IOException("The provider returned an empty answer.");
                return result;
            } finally {conn.disconnect();}
        } finally {BUSY.set(false);}
    }
    static String readBounded(InputStream in,int max) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;
        while((n=in.read(b))!=-1){if(out.size()+n>max)throw new IOException("File or response is too large (limit "+max+" bytes).");out.write(b,0,n);}
        return out.toString("UTF-8");
    }
}
