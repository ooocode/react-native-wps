package com.wps.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.wps.R;

public class NotificationUtils {

  private static final String CHANNEL_ID = "my_channel_id";
  private static final String CHANNEL_NAME = "My Channel";
  private static final int NOTIFICATION_ID = 1;

  public static Notification showNotification(Context context, String title, String message) {
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    // 创建通知渠道 (Android 8.0及以上需要)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH);
      notificationManager.createNotificationChannel(channel);
    }

    // 创建通知
    Notification notification = null;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      notification = new Notification.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.drawable.baseline_call_made_24)
        .build();
    } else {
      notification = new Notification.Builder(context)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(R.drawable.baseline_call_made_24)
        .build();
    }

    // 显示通知
    //notificationManager.notify(NOTIFICATION_ID, notification);
    return notification;
  }
}
