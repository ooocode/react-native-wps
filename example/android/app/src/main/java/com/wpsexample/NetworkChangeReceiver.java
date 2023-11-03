package com.wpsexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.HeadlessJsTaskService;
import com.wps.services.MyTaskService;

import java.util.Iterator;
import java.util.Map;

public class NetworkChangeReceiver extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
      Log.d("TAG", "onReceive: " + "11111");
      Intent intentService = new Intent(context, MyTaskService.class);
      Bundle bundle = new Bundle();
      bundle.putString("qq", "xx");

      intentService.putExtras(bundle);
      context.startService(intentService);
      HeadlessJsTaskService.acquireWakeLockNow(context);


      return;
     /*
      Intent newIntent = new Intent(context, MainActivity.class);  // 要启动的Activity
      //1.如果自启动APP，参数为需要自动启动的应用包名
      //Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
      //这句话必须加上才能开机自动运行app的界面
      newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //2.如果自启动Activity
      context.startActivity(newIntent);
      //3.如果自启动服务
      //context.startService(newIntent);*/
    }
  }
}
