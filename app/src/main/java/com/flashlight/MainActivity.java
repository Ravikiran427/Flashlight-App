package com.flashlight;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

  Button startButton, stopButton;
  EditText delayTimeInput;
  TextView titleBar;
  boolean start = false;
  int delayTime;
  LinearLayout background;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
      ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 1);

    startButton = findViewById(R.id.startButton);
    stopButton = findViewById(R.id.stopButton);
    delayTimeInput = findViewById(R.id.delayTime);
    background = findViewById(R.id.background);
    titleBar = findViewById(R.id.titleBar);
    background.setBackgroundColor(Color.parseColor("#000316"));
    titleBar.setTextColor(Color.parseColor("#eeebd9"));

    delayTimeInput.setText("1000");


    startButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startButton.setEnabled(false);
        startButton.setAlpha(0.3f);
        stopButton.setEnabled(true);
        stopButton.setAlpha(1.0f);
        delayTimeInput.setCursorVisible(false);
        start = true;
        delayTime = Integer.parseInt(delayTimeInput.getText().toString());
        startTorch();
      }
    });

    stopButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startButton.setEnabled(true);
        startButton.setAlpha(1.0f);
        stopButton.setEnabled(false);
        stopButton.setAlpha(0.3f);
        delayTimeInput.setCursorVisible(true);
        stopTorch();
      }
    });
  }

  public void startTorch(){
    final CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      final String cameraId = cameraManager.getCameraIdList()[0];
      background.setBackgroundColor(Color.parseColor("#eeebd9"));
      titleBar.setTextColor(Color.parseColor("#000316"));
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        new Thread(new Runnable() {
          @Override
          public void run() {
            while (start) {
                try {
                  cameraManager.setTorchMode(cameraId, true);
                  if(Integer.parseInt(delayTimeInput.getText().toString())!=0) {
                    Thread.sleep(Integer.parseInt(delayTimeInput.getText().toString()));
                    cameraManager.setTorchMode(cameraId, false);
                    Thread.sleep(Integer.parseInt(delayTimeInput.getText().toString()));
                  }
                }
                catch (Exception e){
                }
            }
            if(!start)
              Thread.interrupted();
          }
        }).start();

      }
    } catch (Exception e) {
    }

  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  public void stopTorch(){
    start = false;
    background.setBackgroundColor(Color.parseColor("#000316"));
    titleBar.setTextColor(Color.parseColor("#eeebd9"));
  }

  @Override
  protected void onPause() {
    super.onPause();
    CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    try {
      String cameraId = cameraManager.getCameraIdList()[0];
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cameraManager.setTorchMode(cameraId, false);
      }
    } catch (CameraAccessException e) {
    }
    start = false;
    this.finish();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

    try {
      String cameraId = cameraManager.getCameraIdList()[0];
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cameraManager.setTorchMode(cameraId, false);
      }
    } catch (CameraAccessException e) {
    }
    start = false;
    this.finish();
  }
}
