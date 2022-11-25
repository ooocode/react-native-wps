package com.wps;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.module.annotations.ReactModule;

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
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
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
   * 通过WPS打开只读word文档
   * */
  @ReactMethod
  public void OpenReadonlyOfficeFileByWps(String path, String contentType,
                                          Promise promise) {
    // 打开文档
    File file = new File(path);

    Intent intent = new Intent();
    Bundle bundle = new Bundle();

    bundle.putBoolean(Define.ENTER_REVISE_MODE, false);
    bundle.putBoolean(Define.REVISION_NOMARKUP, true);
    bundle.putString(Define.OPEN_MODE, Define.READ_ONLY);           //只读模式
    bundle.putBoolean(Define.SHOW_REVIEWING_PANE_RIGHT_DEFAULT, false);
    bundle.putBoolean(Define.FAIR_COPY, true);


    //bundle.putBoolean(Define.IS_SHOW_VIEW, false);
    //bundle.putBoolean(Define.BACK_KEY_DOWN,false);

    //bundle.putBoolean("DisplayView", false);
    bundle.putString("ThirdPackage", this.getReactApplicationContext().getPackageName());

    //文档记录
    bundle.putBoolean(Define.CLEAR_BUFFER, true);    //清除临时文件boolean
    bundle.putBoolean(Define.CLEAR_TRACE, true);        //清除使用记录boolean
    bundle.putBoolean(Define.CLEAR_FILE, true);        //删除打开文件boolean

    bundle.putString("WaterMaskText", "水印测试style");


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
        Uri uri = FileProvider.getUriForFile(this.getReactApplicationContext(), "com.wpsexamplenew.fileprovider", file);
        //Uri uri = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName() + ".fileprovider", file);
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
}
