package com.wps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TestService extends Service {

  private NotificationManager notificationManager;
  private String notificationId = "channelId";
  private String notificationName = "channelName";


  @Override
  public void onCreate() {
    super.onCreate();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    //创建NotificationChannel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationChannel channel = new NotificationChannel(notificationId, notificationName, NotificationManager.IMPORTANCE_HIGH);
      notificationManager.createNotificationChannel(channel);
    }

    boolean canOpenNotification = NotificationManagerCompat.from(this).areNotificationsEnabled();

    Notification notification = getNotification();

    startForeground(1, notification);
  }

  private Notification getNotification() {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notificationId)
      //.setSmallIcon(R.drawable.logo_small)
      .setContentTitle("")
      .setContentText("我正在运行");

    Notification notification = builder.build();
    return notification;
  }


  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
