package com.example.tiasakeun.utils.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.tiasakeun.utils.receiver.AlarmReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmHelper {
    private static final String TAG = "AlarmHelper";

    public static void setAlarmForSubActivity(Context context, long subActivityId, String title, String dateStr, String timeStr) {
        if (dateStr == null || timeStr == null) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "Mohon izinkan akses untuk alarm", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        try {
            String dateTimeString = dateStr + " " + timeStr;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = simpleDateFormat.parse(dateTimeString);

            if (date == null) return;

            long triggerTimeInMillis = date.getTime();
            if (triggerTimeInMillis <= System.currentTimeMillis()) {
                return;
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("subActivityTitle", title);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (int) subActivityId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTimeInMillis, pendingIntent);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, "setAlarmForSubActivity: " + ex.getMessage());
        }
    }

    public static void cancelAlarmForSubActivity(Context context, long subActivityId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                (int) subActivityId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
