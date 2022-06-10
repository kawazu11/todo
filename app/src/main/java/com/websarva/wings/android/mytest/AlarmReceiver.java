package com.websarva.wings.android.mytest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.websarva.wings.android.mytest.db.TaskContract;
import com.websarva.wings.android.mytest.db.TaskDbHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    private static final Object Tag = "AlarmReceiver";


    //通知設定
    @Override
    public void onReceive(Context context, Intent intent) {
        int requestCode = intent.getIntExtra("RequestCode", 0);
        String task = intent.getStringExtra("task");


        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        String channelId = "default";
        String title = context.getString(R.string.app_name);

        long currentTime = System.currentTimeMillis();
        SimpleDateFormat dataFormat =
                new SimpleDateFormat("HH:mm:ss", Locale.JAPAN);

        String message = task;

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannel channel = new NotificationChannel(
                channelId, title, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(defaultSoundUri, null);

        if(notificationManager != null) {
            notificationManager.createNotificationChannel(channel);

            //通知の詳細設定
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();

            notificationManager.notify(requestCode, notification);
        }
    }
}
