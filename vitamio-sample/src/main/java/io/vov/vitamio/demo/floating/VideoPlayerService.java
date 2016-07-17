package io.vov.vitamio.demo.floating;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import io.vov.vitamio.demo.R;

public class VideoPlayerService extends Service implements IServiceHelper {
  private static Activity mActivity;
  public static FloatPlayerUI mFloatPlayerUI;
  private static WindowManager mWindowManager = null;
  private static WindowManager.LayoutParams wmParams = null;
  private static String videoPath;

  @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();

    initWindowFloatView();
  }

  @Override public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);
  }

  @Override public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);
    return START_NOT_STICKY;
  }

  @Override public void onDestroy() {
    super.onDestroy();
  }

  public void initWindowFloatView() {
    RelativeLayout.LayoutParams params =
        new RelativeLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.float_window_root_width),
            getResources().getDimensionPixelSize(R.dimen.float_window_root_height));
    mFloatPlayerUI = new FloatPlayerUI(mActivity, this, videoPath);
    //mFloatPlayerUI.setLayoutParams(params);
    //mFloatPlayerUI.setBackgroundColor(Color.BLACK);
    mFloatPlayerUI.mlayoutView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
  }

  public static void startService(Activity activity, String videoPaths) {
    videoPath = videoPaths;
    mActivity = activity;
    if (mActivity != null) {
      Intent mIntent = new Intent(mActivity, VideoPlayerService.class);
      mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
      mActivity.startService(mIntent);
    }
  }

  public static void stopService(Activity activity) {
    if (activity == null) return;
    Intent mIntent = new Intent(activity, VideoPlayerService.class);
    activity.stopService(mIntent);
  }

  @Override public void closeFloatWindow() {
    if (mFloatPlayerUI != null) {
      mFloatPlayerUI.exitFloatWindow();
      mFloatPlayerUI = null;
    }
    stopSelf();
  }
}
