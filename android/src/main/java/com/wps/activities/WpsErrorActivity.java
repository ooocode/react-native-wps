package com.wps.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wps.R;

public class WpsErrorActivity extends AppCompatActivity {

  //private EditText editNum;
  private Button addbtn;


  DisplayMetrics dm = new DisplayMetrics();


  //MainActivity().getMetrics(dm);
  int width = dm.widthPixels;
  int height = dm.heightPixels;
  Button[] btt = new Button[205];

  //RelativeLayout layout = new RelativeLayout(this);
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    boolean isOk = getIntent().getBooleanExtra("isOk", false);

    if (isOk) {
      this.setTitle("WPS上传文件成功");
    } else {
      this.setTitle("WPS上传文件出错");
    }

    this.setFinishOnTouchOutside(false);

    final LinearLayout rlayout = new LinearLayout(this);
    rlayout.setOrientation(LinearLayout.VERTICAL);
    rlayout.setPadding(40, 40, 40, 40);

    if (isOk == false) {
      String errorMessage = getIntent().getStringExtra("error-message");
      String SavePath = getIntent().getStringExtra("SavePath");

      TextView textView = new TextView(WpsErrorActivity.this);
      textView.setSingleLine(false);
      textView.setEllipsize(null);
      textView.setText("错误原因：" + errorMessage);
      rlayout.addView(textView);

      /*TextView textViewFile = new TextView(WpsErrorActivity.this);
      textViewFile.setSingleLine(false);
      textViewFile.setEllipsize(null);
      textViewFile.setText("文件路径：" + SavePath);
      rlayout.addView(textViewFile);*/
    }

    addbtn = new Button(WpsErrorActivity.this);
    addbtn.setWidth(100);
    addbtn.setHeight(40);
    addbtn.setText("关闭");

    rlayout.addView(addbtn);

    addbtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        WpsErrorActivity.this.finish();
      }
    });


    setContentView(rlayout);
  }
}
