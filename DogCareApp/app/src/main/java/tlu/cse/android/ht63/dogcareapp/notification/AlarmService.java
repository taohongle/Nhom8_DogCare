package tlu.cse.android.ht63.dogcareapp.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class AlarmService {
    private final Context context;
    public AlarmService(Context context) {
        this.context = context;
    }
    public void setExactAlarm(long timeInMillis, int id, String title, String content) {
        cancelAlarm(context, id);
        setAlarm(timeInMillis, false, id, title, content,0);
    }
    public void setRepetitiveAlarm(long timeInMillis, int id, String title, String content, int day) {
        cancelAlarm(context, id);
        setAlarm(timeInMillis, true, id, title, content,day);
    }
    @SuppressLint("ScheduleExactAlarm")
    private void setAlarm(long timeInMillis, boolean isRepeat, int id, String title, String content,int day) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(AppCompatActivity.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("content", content);
        int flag = PendingIntent.FLAG_MUTABLE;

        Log.d("__haha", "setAlarm: "+Integer.hashCode(id));
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, Integer.hashCode(id), intent, flag);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);

        if (isRepeat) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * day,
                    alarmIntent
            );
        } else {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    alarmIntent
            );
        }
    }

    private void cancelAlarm(Context context, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(AppCompatActivity.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        int flag = PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, flag);
        alarmManager.cancel(pendingIntent);
    }
}
