package com.wps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.module.annotations.ReactModule;
import com.wps.services.MyTaskService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ReactModule(name = WpsModule.NAME)
public class WpsModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Wps";


  public WpsModule(ReactApplicationContext reactContext) {
    super(reactContext);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      WPSBroadcastReceiver wpsBroadcastReceiver = new WPSBroadcastReceiver();
      reactContext.addLifecycleEventListener(new LifecycleEventListener() {
        @Override
        public void onHostResume() {
          //受 Android 8.0（API 级别 26）后台执行限制的影响，
          // 以 API 级别 26 或更高级别为目标的应用无法再在其清单中注册用于隐式广播的广播接收器。
          // 不过，有几种广播目前不受这些限制的约束。无论应用以哪个 API 级别为目标，都可以继续为以下广播注册监听器。
          IntentFilter filter = new IntentFilter();
          filter.addAction("cn.wps.moffice.file.save");
          filter.addAction("cn.wps.moffice.file.close");
          reactContext.registerReceiver(wpsBroadcastReceiver, filter);
        }

        @Override
        public void onHostPause() {

        }

        @Override
        public void onHostDestroy() {
          reactContext.unregisterReceiver(wpsBroadcastReceiver);
        }
      });
    }
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }


  @ReactMethod
  public void getBundleVersion(Promise promise) {
    promise.resolve(UpdateContext.BundleVersion);
  }

  private boolean isAppInstalledInternal(String packageName) {
    try {
      ApplicationInfo result = this.getReactApplicationContext().getPackageManager()
        .getApplicationInfo(packageName, 0);
      if (result.enabled) {
        return true;
      } else {
        return false;
      }
    } catch (PackageManager.NameNotFoundException e) {
      return false;
    }
  }

  /*
   * apo是否安装了
   * */
  @ReactMethod
  public void isAppInstalled(String packageName, Promise promise) {
    promise.resolve(isAppInstalledInternal(packageName));
  }


  /*
   * 安装APK
   * */
  @ReactMethod
  public void installApk(String fileName, Promise promise) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        //先获取是否有安装未知来源应用的权限
        boolean haveInstallPermission = this.getReactApplicationContext().getPackageManager().canRequestPackageInstalls();
        if (!haveInstallPermission) {//没有权限
          promise.reject("installApk", "android.permission.REQUEST_INSTALL_PACKAGES");
          return;
        }
      }

      File file = new File(fileName);
      if (!file.exists()) {
        promise.reject("installApk", fileName + " 不存在");
        return;
      }
      Uri uri;

      if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        uri = FileProvider.getUriForFile(this.getReactApplicationContext(),
          this.getReactApplicationContext().getPackageName() + ".fileprovider", file);
      } else {
        uri = Uri.fromFile(file);
      }

      final Intent intent = new Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, "application/vnd.android.package-archive");
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

      this.getReactApplicationContext().startActivity(intent);
      promise.resolve(true);
    } catch (Exception ex) {
      ex.printStackTrace();
      promise.reject("installApk", ex.getMessage());
    }
  }


  /*
   * 通过默认程序打开本地文件
   * */
  @ReactMethod
  public void OpenLocalFile(String path, String contentType, Promise promise) {
    try {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      File file = new File(path);

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//大于7.0使用此方法
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(this.getReactApplicationContext(),
          this.getReactApplicationContext().getPackageName() + ".fileprovider", file);
        intent.setDataAndType(uri, contentType);
      } else {//小于7.0就简单了
        intent.setDataAndType(Uri.fromFile(file), contentType);
      }

      this.getCurrentActivity().startActivity(intent);
      promise.resolve(true);
    } catch (Exception ex) {
      promise.reject("打开文件出现错误", ex.getMessage());
    }
  }


  /*
   * 通过WPS打开只读word文档 APP内部路径
   * */
  @ReactMethod
  public void OpenReadonlyOfficeFileByWps(String path, String contentType,
                                          Promise promise) {
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());

    // 打开文档
    File file = new File(path);

    Intent intent = new Intent();
    Bundle bundle = new Bundle();

    bundle.putBoolean(Define.SAVE_PATH, false);
    //打开模式
    bundle.putString(Define.OPEN_MODE, Define.READ_ONLY);
    if (!path.toLowerCase().endsWith(".pdf")) {
      //进入修订模式
      bundle.putBoolean(Define.ENTER_REVISE_MODE, false);

      //REVISION_NOMARKUP === false 则不显示修订 关键
      bundle.putBoolean(Define.REVISION_NOMARKUP, false);

      //显示右边
      bundle.putBoolean(Define.SHOW_REVIEWING_PANE_RIGHT_DEFAULT, false);

      //bundle.putBoolean(Define.FAIR_COPY, true); //清稿

      //关闭修订模式
      bundle.putBoolean(Define.AT_QUICK_CLOSE_REVISEMODE, false);
    }


    //bundle.putBoolean("DisplayView", false);
    bundle.putString("ThirdPackage", this.getReactApplicationContext().getPackageName());

    //文档记录
    bundle.putBoolean(Define.CLEAR_BUFFER, true);    //清除临时文件boolean
    bundle.putBoolean(Define.CLEAR_TRACE, true);        //清除使用记录boolean
    bundle.putBoolean(Define.CLEAR_FILE, true);        //删除打开文件boolean

    //bundle.putString("WaterMaskText", "水印测试style");


    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    intent.setAction(Intent.ACTION_VIEW);

    if (isAppInstalledInternal(Define.PACKAGENAME_KING_PRO)) {
      //wps专业版
      intent.setClassName(Define.PACKAGENAME_KING_PRO, "cn.wps.moffice.documentmanager.PreStartActivity2");
    } else if (isAppInstalledInternal(Define.PACKAGENAME_ENG)) {
      //wps普通版
      intent.setClassName(Define.PACKAGENAME_ENG, "cn.wps.moffice.documentmanager.PreStartActivity2");
    } else {
      promise.reject("检测到您没有安装WPS软件", "检测到您没有安装WPS软件");
      return;
    }


    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//大于7.0使用此方法
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(this.getReactApplicationContext(),
          this.getReactApplicationContext().getPackageName() + ".fileprovider", file);
        intent.setDataAndType(uri, contentType);
      } else {//小于7.0就简单了
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setDataAndType(Uri.fromFile(file), contentType);
      }

      intent.putExtras(bundle);
      this.getCurrentActivity().startActivity(intent);
      promise.resolve(true);
    } catch (Exception ex) {
      promise.reject("OpenReadonlyOfficeFileByWps", ex.getMessage());
    }
  }


  public static String Token = "";
  public static String WorkFlowBaseUrl = "";

  /*
   * 通过WPS打开编辑word文档  app外部路径
   * */
  @ReactMethod
  public void OpenEditOfficeFileByWps(String workFlowBaseUrl, String token, String userName,
                                      String path, String contentType,
                                      String savePath,
                                      Promise promise) {
    WorkFlowBaseUrl = workFlowBaseUrl;
    Token = token;

    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    StrictMode.setVmPolicy(builder.build());

    // 打开文档
    File file = new File(path);
    if (!file.exists()) {
      promise.reject("OpenEditOfficeFileByWps", "文件不存在:" + path);
      return;
    }

    Intent intent = new Intent();
    Bundle bundle = new Bundle();

    //打开模式
    bundle.putString(Define.OPEN_MODE, Define.NORMAL);

    //bundle.putBoolean(Define.SAVE_PATH, true);

    //进入修订模式
    //bundle.putBoolean(Define.ENTER_REVISE_MODE, true);

    bundle.putString(Define.USER_NAME, userName);

    //REVISION_NOMARKUP === false 则不显示修订 关键
    bundle.putBoolean(Define.REVISION_NOMARKUP, false);

    //显示右边
    bundle.putBoolean(Define.SHOW_REVIEWING_PANE_RIGHT_DEFAULT, false);

    //bundle.putBoolean(Define.FAIR_COPY, true); //清稿

    //关闭修订模式
    bundle.putBoolean(Define.AT_QUICK_CLOSE_REVISEMODE, false);


    //bundle.putBoolean(Define.IS_SHOW_VIEW, false);
    //bundle.putBoolean(Define.BACK_KEY_DOWN,false);

    //bundle.putBoolean("DisplayView", false);
    bundle.putString("ThirdPackage", this.getReactApplicationContext().getPackageName());

    //文档记录
    //bundle.putBoolean(Define.CLEAR_BUFFER, false);    //清除临时文件boolean
    //bundle.putBoolean(Define.CLEAR_TRACE, false);        //清除使用记录boolean
    //bundle.putBoolean(Define.CLEAR_FILE, false);        //删除打开文件boolean

    //bundle.putString("WaterMaskText", "水印测试style");

    //广播
    bundle.putBoolean(Define.SEND_SAVE_BROAD, true);
    bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);


    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    intent.setAction(Intent.ACTION_VIEW);


    if (isAppInstalledInternal(Define.PACKAGENAME_KING_PRO)) {
      //wps专业版
      intent.setClassName(Define.PACKAGENAME_KING_PRO, "cn.wps.moffice.documentmanager.PreStartActivity2");
    } else if (isAppInstalledInternal(Define.PACKAGENAME_ENG)) {
      //wps普通版
      intent.setClassName(Define.PACKAGENAME_ENG, "cn.wps.moffice.documentmanager.PreStartActivity2");
    } else {
      promise.reject("检测到您没有安装WPS软件", "检测到您没有安装WPS软件");
      return;
    }


    try {
      /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//大于7.0使用此方法
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(this.getReactApplicationContext(),
          this.getReactApplicationContext().getPackageName() + ".fileprovider", file);
        intent.setDataAndType(uri, contentType);
      } else {//小于7.0就简单了*/
      // 由于没有在Activity环境下启动Activity,设置下面的标签
      intent.setDataAndType(Uri.fromFile(file), contentType);
      //}

      intent.putExtras(bundle);
      this.getCurrentActivity().startActivity(intent);
      promise.resolve(true);
    } catch (Exception ex) {
      ex.printStackTrace();
      promise.reject("OpenReadonlyOfficeFileByWps", ex.getMessage());
    }
  }


  /*
   * 通过WPS打开只读word文档
   * */
  @ReactMethod
  public void SendIntent(
    String action,

    ReadableMap map,
    Promise promise) {
    Intent intent = new Intent();


    //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    intent.setAction(Intent.ACTION_VIEW);
    intent.setClassName(Define.PACKAGENAME_KING_PRO, "cn.wps.moffice.documentmanager.PreStartActivity2");
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


    Bundle bundle = new Bundle();

    Iterator<Map.Entry<String, Object>> iterator = map.getEntryIterator();
    while (iterator.hasNext()) {
      Map.Entry<String, Object> entry = iterator.next();
      System.out.println("key:" + entry.getKey() + ",vaule:" + entry.getValue());

      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof String) {
        bundle.putString(key, (String) value);
      } else if (value instanceof Boolean) {
        bundle.putBoolean(key, (Boolean) value);
      } else if (value instanceof Double) {
        bundle.putDouble(key, (Double) value);
      }
    }

    intent.putExtras(bundle);
    try {
      this.getCurrentActivity().startActivity(intent);
      promise.resolve(true);
    } catch (Exception ex) {
      promise.resolve(false);
    }
  }


  @ReactMethod
  public void PdfFiles(String pdfFileName, Promise promise) {
    ParcelFileDescriptor input = null;
    PdfRenderer renderer = null;

    try {
      String tmpDir = this.getReactApplicationContext().getCacheDir().getPath() + "/tmppdf";
      File tmpDirFile = new File(tmpDir);
      if (!tmpDirFile.exists()) {
        tmpDirFile.mkdir();
      }

      input = ParcelFileDescriptor.open(new File(pdfFileName), ParcelFileDescriptor.MODE_READ_ONLY);
      renderer = new PdfRenderer(input);

      WritableNativeArray array = new WritableNativeArray();

      int pageCount = renderer.getPageCount();
      for (int i = 0; i < pageCount; i++) {
        PdfRenderer.Page page = renderer.openPage(i);
        Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);


        String savePath = tmpDirFile + "/pdf" + i + ".png";
        FileOutputStream saveImgOut = new FileOutputStream(new File(savePath));

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, saveImgOut);
        page.close();

        array.pushString(savePath);
      }

      promise.resolve(array);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      promise.reject("", e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
      promise.reject("", e.getMessage());
    } finally {
      if (renderer != null) {
        renderer.close();
      }
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


  @ReactMethod
  public void restartApp() {
    PackageManager packageManager = this.getReactApplicationContext().getPackageManager();
    Intent intent = packageManager.getLaunchIntentForPackage(this.getReactApplicationContext().getPackageName());

    ComponentName componentName = intent.getComponent();
    Intent mainIntent = Intent.makeRestartActivityTask(componentName);
    this.getReactApplicationContext().startActivity(mainIntent);
    Runtime.getRuntime().exit(0);
  }


  @ReactMethod
  public void startMyTaskService(ReadableMap readableMap) {
    Context context = this.getReactApplicationContext().getApplicationContext();

    Intent intent = new Intent(context, MyTaskService.class);
    Bundle bundle = new Bundle();

    for (Iterator<Map.Entry<String, Object>> it = readableMap.getEntryIterator(); it.hasNext(); ) {
      Map.Entry<String, Object> i = it.next();
      bundle.putString(i.getKey(), i.getValue().toString());
    }

    intent.putExtras(bundle);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(intent);
    } else {
      context.startService(intent);
    }
    HeadlessJsTaskService.acquireWakeLockNow(context);
  }


  @ReactMethod
  public void stopMyTaskService() {
    ReactApplicationContext context = this.getReactApplicationContext();
    Intent service = new Intent(context, MyTaskService.class);
    context.stopService(service);
  }
}
