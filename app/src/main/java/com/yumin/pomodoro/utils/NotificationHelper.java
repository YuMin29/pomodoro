package com.yumin.pomodoro.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.yumin.pomodoro.R;

public class NotificationHelper extends ContextWrapper {
    private static final int NOTIFICATION_ID = 1000;
    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;

    public static final String CHANNEL_ID = "default";
    private static final String CHANNEL_NAME = "Default Channel";
    private static final String CHANNEL_DESCRIPTION = "this is default channel!";

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationChannel.setDescription(CHANNEL_DESCRIPTION);
            getNotificationManager().createNotificationChannel(mNotificationChannel);
        }
    }

    public NotificationCompat.Builder getNotification(String title, String leftTime, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        }
        builder.setSmallIcon(R.drawable.ic_baseline_mood_24);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        builder.setContentText(leftTime);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        return builder;
    }

    public void notify(NotificationCompat.Builder builder) {
        if (getNotificationManager() != null) {
            // use same id to update notification
            getNotificationManager().notify(NOTIFICATION_ID, builder.build());
        }
    }

    public void cancelNotification(){
        if (getNotificationManager() != null)
            getNotificationManager().cancel(NOTIFICATION_ID);
    }

    public void openChannelSetting(String channelId) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null)
            startActivity(intent);
    }

    public void openNotificationSetting() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        if (getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null)
            startActivity(intent);
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

}
