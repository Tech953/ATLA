package dev.engram.daily;
import android.content.*;
public class ScheduleReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context c,Intent i){Scheduler.schedule(c);}
}
