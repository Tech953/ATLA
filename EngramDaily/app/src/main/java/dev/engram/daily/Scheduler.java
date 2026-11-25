package dev.engram.daily;
import android.app.*;
import android.app.job.*;
import android.content.*;
import java.time.*;

final class Scheduler {
    static final int JOB=401;
    static void schedule(Context c) {
        Store s=new Store(c);JobScheduler js=c.getSystemService(JobScheduler.class);js.cancel(JOB);
        if(!s.get("scheduled").equals("yes"))return;
        int hour=9,minute=0;try{String[] t=s.get("time").split(":");hour=Integer.parseInt(t[0]);minute=Integer.parseInt(t[1]);}catch(Exception ignored){}
        ZonedDateTime now=ZonedDateTime.now(),next=now.withHour(hour).withMinute(minute).withSecond(0).withNano(0);
        if(!next.isAfter(now))next=next.plusDays(1);
        long delay=Duration.between(now,next).toMillis();
        JobInfo.Builder job=new JobInfo.Builder(JOB,new ComponentName(c,DigestJob.class)).setMinimumLatency(delay).setPersisted(true);
        if(s.mode().equals("API"))job.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        js.schedule(job.build());
    }
    static void notifyReady(Context c) {
        NotificationManager nm=c.getSystemService(NotificationManager.class);
        nm.createNotificationChannel(new NotificationChannel("digest","Daily digest",NotificationManager.IMPORTANCE_DEFAULT));
        PendingIntent open=PendingIntent.getActivity(c,0,new Intent(c,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n=new Notification.Builder(c,"digest").setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Your daily digest is ready").setContentText("Open Engram Daily to review your next steps.")
            .setContentIntent(open).setAutoCancel(true).setVisibility(Notification.VISIBILITY_PRIVATE).build();
        try{nm.notify(1,n);}catch(SecurityException ignored){}
    }
}
