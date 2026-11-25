package dev.engram.daily;
import android.app.job.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
public class DigestJob extends JobService {
    private final ExecutorService executor=Executors.newSingleThreadExecutor();
    private final AtomicInteger generation=new AtomicInteger();
    @Override public boolean onStartJob(JobParameters params) {
        final int token=generation.incrementAndGet();
        executor.submit(()->{
            Store s=new Store(this);
            if(token!=generation.get())return;
            try {
                String digest=Engine.run(this,Engine.DIGEST);
                if(token==generation.get() && s.get("scheduled").equals("yes")) {
                    s.set("digest",digest);s.set("digestDate",java.time.Instant.now().toString()+" • "+s.mode());s.set("jobError","");Scheduler.notifyReady(this);
                }
            }catch(Exception|LinkageError e){if(token==generation.get())s.set("jobError",e.getMessage()==null?"Generation failed":e.getMessage());}
            if(token==generation.get()){jobFinished(params,false);Scheduler.schedule(this);}
        });return true;
    }
    @Override public boolean onStopJob(JobParameters p){generation.incrementAndGet();return true;}
    @Override public void onDestroy(){generation.incrementAndGet();executor.shutdownNow();super.onDestroy();}
}
