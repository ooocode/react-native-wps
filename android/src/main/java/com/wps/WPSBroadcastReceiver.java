package com.wps;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WPSBroadcastReceiver extends BroadcastReceiver {
  OkHttpClient okHttpClient = new OkHttpClient();


  private static final MediaType FROM_DATA = MediaType.parse("multipart/form-data");

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction() == Define.Reciver.ACTION_SAVE) {
      Set<String> keys = intent.getExtras().keySet();
      String SavePath = intent.getExtras().getString("SavePath");
     /* String OpenFile = intent.getExtras().getString("OpenFile");
      String OpenURI = intent.getExtras().getString("OpenURI");
      String ThirdPackage = intent.getExtras().getString("ThirdPackage");*/


      try {
        File file = new File(SavePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        MultipartBody body = new MultipartBody.Builder()
          .setType(MultipartBody.FORM)
          .addFormDataPart("filename", file.getName(), fileBody)
          .build();

        String uri = WpsModule.WorkFlowBaseUrl + "/api/AttachmentsNew/wps/" + file.getName();
        Log.d("file", "onReceive: " + uri);
        Request request = new Request.Builder()
          .post(body)
          .addHeader("Authorization", WpsModule.Token)
          .url(uri)
          .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            e.printStackTrace();
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            Log.d("file", "onResponse: 文件上传成功");
          }
        });
      } catch (Exception e) {
        e.printStackTrace();
      }

            /*ReactContext ctx = reactApplication.getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
            ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("WpsSave", SavePath);
            Log.d("1", "onReceive: " + SavePath);*/

            /*var item = ActivityWebview.Files.FirstOrDefault(e => e.Value == SavePath);
            if (item.Value.EndsWith(".pdf"))
            {
                return;
            }*/

      //var fileName = System.IO.Path.GetFileName(item.Key);
      //fileName = Android.Net.Uri.Decode(fileName);
      //await API.Instance.UpdateFileToServerForOld(item.Key, item.Value, fileName);
    } else if (intent.getAction() == Define.Reciver.ACTION_CLOSE) {
      // String SavePath = intent.getExtras().getString("CloseFile");
      Set<String> keys = intent.getExtras().keySet();
      String SavePath = intent.getExtras().getString("CloseFile");

           /* File src= new File(SavePath);
            File dest = new File(SavePath+"0");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    Files.copy(src.toPath(),dest.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/

           /* ReactContext ctx = reactApplication.getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
            ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("WpsSave", SavePath+"0");
            Log.d("1", "onReceive: " + SavePath+"0");*/


            /*try {
                File file = new File(SavePath);
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
                MultipartBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("filename",file.getName(),fileBody)
                        .build();

                String uri = "http://192.168.1.2:8001/api/AttachmentsNew/wps/"+file.getName();
                Request request = new Request.Builder()
                        .post(body)
                        .addHeader("Authorization",ExJavaModule.Token)
                        .url(uri)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }*/
    } else if (intent.getAction() == "cn.wps.moffice.broadcast.AfterSaved") {
      String a = "123";
    }
  }
}
