package com.wps;

import android.app.Application;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UpdateContext {
  public static String getJSBundleFile(Application application, String indexZipFileBaseUrl, String appId, boolean isDev) {
    if (isDev) {
      return null;
    }

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);

    String cacheDir = application.getCacheDir().getPath();
    String bundleDir = cacheDir + "/bundle";
    String indexBundleFileName = bundleDir + "/index.android.bundle";
    File indexBundleFile = new File(indexBundleFileName);

    if (indexBundleFile.exists()) {
      //存在bundle
      try {
        String versionTextPath = bundleDir + "/version.txt";
        Scanner sc = new Scanner(new FileReader(versionTextPath));
        String version = sc.nextLine();

        indexZipFileBaseUrl = indexZipFileBaseUrl + "/File/" + appId + "/download?version=" + version;
        startDownloadAndExtra(application, indexZipFileBaseUrl, bundleDir);
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      return indexBundleFileName;
    } else {
      indexZipFileBaseUrl = indexZipFileBaseUrl + "/File/" + appId + "/download";
      bundleDir = startDownloadAndExtra(application, indexZipFileBaseUrl, bundleDir);
      if (bundleDir == null) {
        return null;
      } else {
        return indexBundleFileName;
      }
    }
  }

  private static String startDownloadAndExtra(Application application, String indexZipFileUrl, String bundleDir) {
    String zipFileName = DownloadZip(application, indexZipFileUrl);
    if (zipFileName != null) {
      try {
        new ZipFile(zipFileName).extractAll(bundleDir);
        return bundleDir;
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
    return null;
  }


  private static String DownloadZip(Application application, String zipUrl) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
      .url(zipUrl)
      .build();


    try (Response response = client.newCall(request).execute()) {
      if (response.code() == 204) {
        return null;
      }

      String contentDisposition = response.header("Content-Disposition");
      String fileName = contentDisposition.substring(contentDisposition.length() - 14, contentDisposition.length());
      String path = application.getCacheDir().getPath();

      String dirPath = path + "/rnversions/";
      File dir = new File(dirPath);
      if (!dir.exists()) {
        //创建目录
        dir.mkdirs();
      }

      String saveZipPath = dirPath + fileName;

      FileOutputStream stream = new FileOutputStream(saveZipPath);
      stream.write(response.body().bytes());
      stream.close();

      Log.d("update", "成功下载ZIP: " + saveZipPath);
      return saveZipPath;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return null;
  }
}
