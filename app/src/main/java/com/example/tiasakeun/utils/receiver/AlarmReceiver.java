package com.example.tiasakeun.utils.receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.tiasakeun.R;

public class AlarmReceiver extends BroadcastReceiver {
    //ID untuk channel notifikasi
    private static final String CHANNEL_ID = "sub_activity_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String subActivityTitle = intent.getStringExtra("subActivityTitle");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Pengingat Sub Aktivitas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Waktunya Beraktivitas!")
                .setContentText(subActivityTitle != null ? subActivityTitle : "Jadwal kegiatan dimulai.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }
}
