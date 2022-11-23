package com.wps;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableNativeArray;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.module.annotations.ReactModule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

  // Example method
  // See https://reactnative.dev/docs/native-modules-android
  @ReactMethod
  public void multiply(double a, double b, Promise promise) {
    promise.resolve(a * b);
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
