package com.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import io.vov.vitamio.demo.R;
import media.explore.activities.FileExplorerActivity;
import media.explore.activities.RecentMediaActivity;
import media.explore.activities.SampleMediaActivity;

@SuppressLint("Registered") public abstract class AppActivity extends BaseActivity {
  private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
  public ProgressBar pb;
  public TextView downloadRateView, loadRateView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_app);
  }

  @Override protected void onStart() {
    super.onStart();
    pb = (ProgressBar) findViewById(R.id.probar);
    downloadRateView = (TextView) findViewById(R.id.download_rate);
    loadRateView = (TextView) findViewById(R.id.load_rate);
    setToolBar();
  }

  private void setToolBar() {
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    if (null != mToolbar) {
      setSupportActionBar(mToolbar);
      mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
      mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          finish();
        }
      });

      if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        } else {
          ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
              MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
      }
    }

  }

  @Override public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permission was granted, yay! Do the task you need to do.
        } else {
          // permission denied, boo! Disable the functionality that depends on this permission.
        }
      }
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_app, menu);
    return true;
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    boolean show = super.onPrepareOptionsMenu(menu);
    if (!show) return show;

    MenuItem item1 = menu.findItem(R.id.action_recent);
    if (item1 != null) item1.setVisible(false);

    MenuItem item2 = menu.findItem(R.id.action_sample);
    if (item2 != null) item2.setVisible(false);

    return true;
  }
}
