package com.wps.services;


import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import javax.annotation.Nullable;

public class MyTaskService extends HeadlessJsTaskService {
  @Override
  protected @Nullable HeadlessJsTaskConfig getTaskConfig(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras != null) {
      return new HeadlessJsTaskConfig("MyTaskServiceName",
        Arguments.fromBundle(extras),
        0,
        true
      );
    }
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("MyService", "MyTaskService-onCreate");
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      Notification notification = NotificationUtils.showNotification(this, "后台服务", "运行中，请勿关闭......");
      startForeground(1, notification);
    }
  }


  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d("MyService", "MyTaskService-onDestroy");
  }
}
