package com.app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import io.vov.vitamio.utils.Log;

@SuppressLint("Registered") public abstract class BaseActivity extends AppCompatActivity {

  public String TAG = this.getClass().getSimpleName();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }


  @Override protected void onResume() {
    super.onResume();
    Log.i(TAG,"onResume");
  }

  @Override protected void onPause() {
    super.onPause();
  }


}
