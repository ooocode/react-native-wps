package com.wps.services;


import android.content.Intent;
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
      Log.d("TAG", "getTaskConfig");

      return new HeadlessJsTaskConfig("MyTaskServiceName",
        Arguments.fromBundle(extras),
        Long.MAX_VALUE, // 后台任务的超时时间 5000
        true
        //retryPolicy
        //false // 可选参数：是否允许任务在前台运行，默认为false
      );
    }
    return null;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d("MyService", "MyTaskService-onCreate");
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d("MyService", "MyTaskService-onDestroy");
  }
}
