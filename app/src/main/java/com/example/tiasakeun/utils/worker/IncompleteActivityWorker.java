package com.example.tiasakeun.utils.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.tiasakeun.R;
import com.example.tiasakeun.data.model.SubActivity;
import com.example.tiasakeun.data.source.DatabaseDataSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class IncompleteActivityWorker extends Worker {
    private static final String CHANNEL_ID = "incomplete_task_channel";
    private static final String TAG = "IncompleteActivityWorker";

    public IncompleteActivityWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        DatabaseDataSource db = new DatabaseDataSource(context);

        try {
            db.open();
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            ArrayList<SubActivity> incompleteTask = db.getIncompleteSubActivitiesByDate(todayDate);

            if (incompleteTask != null && !incompleteTask.isEmpty()) {
                showIncompleteReminderNotification(context, incompleteTask.size());
            }

            return Result.success();
        } catch (Exception ex) {
            Log.e(TAG, "doWork: " + ex.getMessage());
            return Result.failure();
        } finally {
            db.close();
        }
    }

    private void showIncompleteReminderNotification(Context context, int taskCount) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Pengingat Aktivitas Tertunda",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_24)
                .setContentTitle("Ada Tugas Belum Selesai!")
                .setContentText("Anda memiliki " + taskCount + " tugas yang belum selesai.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(2001, builder.build());
    }
}
