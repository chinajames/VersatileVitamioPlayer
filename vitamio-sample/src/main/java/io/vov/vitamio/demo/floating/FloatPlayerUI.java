package io.vov.vitamio.demo.floating;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.demo.R;
import java.io.IOException;

public class FloatPlayerUI implements IMediaPlayer {
  public final String TAG = this.getClass().getSimpleName();
  private Activity mContext;
  private FloatPlayerController mController;
  private SurfaceView mSurfaceView;
  private MediaPlayer mMediaPlayer;
  private SurfaceHolder mSurfaceHolder;

  public ViewGroup mlayoutView;
  private FrameLayout mPlayerSurfaceFrame;

  private IServiceHelper mServiceHelper;
  private boolean mIsVideoReadyToBePlayed = false;
  private int mVideoWidth;
  private int mVideoHeight;
  private boolean mIsVideoSizeKnown = false;

  private ScaleGestureDetector mScaleGestureDetector = null;
  private static float TOLERATION = 0.1f;

  public static WindowManager mWindowManager = null;
  public static WindowManager.LayoutParams wmParams = null;
  private static String videoPath;

  private int stateBarHeight = 50;
  private int screenWidth = 0;
  private int screenHeight = 0;
  private int minWindowWidth = 0;
  private int mWindowHeight = 0;
  private int mWindowWidth;
  private int mLastHeight;
  private int mLastWidth;
  private float widthHeightRatio = 16 / 9;

  public FloatPlayerUI(Activity context, IServiceHelper helper, String videoPaths) {
    videoPath = videoPaths;
    mContext = context;
    mServiceHelper = helper;

    initSurfaceView();
    initController();
    mController.showLoading();
    addViewToWindow();
  }

  private void initSurfaceView() {
    mlayoutView = (ViewGroup) View.inflate(mContext, R.layout.layout_floatview, null);
    mSurfaceView = (SurfaceView) mlayoutView.findViewById(R.id.player_surfaceview);
    mPlayerSurfaceFrame = (FrameLayout) mlayoutView.findViewById(R.id.player_surface_frame);

    mSurfaceHolder = mSurfaceView.getHolder();
    mSurfaceHolder.addCallback(mCallback);
    mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
    //mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  private SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
    @Override public void surfaceCreated(SurfaceHolder surfaceHolder) {
      Log.d(TAG, "surfaceCreated called");
      initMediaPlayer();
    }

    @Override public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
  };

  private void initMediaPlayer() {
    try {

      mMediaPlayer = new MediaPlayer(mContext);
      Log.w("hanjh", "mVideoPath: " + videoPath);//"/storage/emulated/0/JQuery实战视频教程[王兴魁]/02.[jQuery]第1章 jQuery入门[下].avi"
      mMediaPlayer.setDataSource(videoPath);
      mMediaPlayer.setDisplay(mSurfaceHolder);
      mMediaPlayer.prepareAsync();
      mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
      mMediaPlayer.setOnErrorListener(mOnErrorListener);
      mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
      mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
      mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
      //mMediaPlayer.setOnInfoListener(mOnInfoListener);
      mContext.setVolumeControlStream(AudioManager.STREAM_MUSIC);
      mController.setmPlayer(mMediaPlayer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initController() {
    mController = new FloatPlayerController(mContext, this, mMediaPlayer);
    RelativeLayout.LayoutParams controllParams =
        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    controllParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    if (mController.getParent() == null) {
      mPlayerSurfaceFrame.addView(mController, controllParams);
    }
    mController.setVisibility(View.VISIBLE);
    mScaleGestureDetector = new ScaleGestureDetector(mContext, new ScaleGestureListener());
    mPlayerSurfaceFrame.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
        Log.v("Hanjh", "onTouch ");
        if (mController.getVisibility() != View.VISIBLE) {
          mController.setVisibility(View.VISIBLE);
        }
        mScaleGestureDetector.onTouchEvent(paramMotionEvent);
        mController.onTouchEvent(paramMotionEvent);
        return false;
      }
    });
    mController.setOnTouchListener(new View.OnTouchListener() {
      @Override public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
        Log.v("Hanjh", "onTouch ");
        if (mController.getVisibility() != View.VISIBLE) {
          mController.setVisibility(View.VISIBLE);
        }
        mScaleGestureDetector.onTouchEvent(paramMotionEvent);
        mController.onTouchEvent(paramMotionEvent);
        return false;
      }
    });
  }

  private void addViewToWindow() {
    wmParams = new WindowManager.LayoutParams();
    mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
    //设置TYPE_PHONE，需要申请android.permission.SYSTEM_ALERT_WINDOW权限
    //TYPE_TOAST同样可以实现悬浮窗效果，不需要申请其他权限
    wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
    wmParams.format = PixelFormat.RGBA_8888;
    wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
    wmParams.gravity = Gravity.LEFT | Gravity.TOP;
    int width = mContext.getResources().getDisplayMetrics().widthPixels;
    int height = mContext.getResources().getDisplayMetrics().heightPixels;
    wmParams.verticalWeight = 0;
    wmParams.horizontalWeight = 0;
    wmParams.width = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
    wmParams.height = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
    mLastWidth = wmParams.width;
    mLastHeight = wmParams.height;
    wmParams.x = (width - wmParams.width) / 2;
    wmParams.y = (height - wmParams.height) / 2;
    wmParams.alpha = 1.0f;
    wmParams.token = mlayoutView.getApplicationWindowToken();
    mWindowManager.addView(mlayoutView, wmParams);

    Display currentDisplay = mWindowManager.getDefaultDisplay();
    screenWidth = currentDisplay.getWidth();
    screenHeight = currentDisplay.getHeight() - stateBarHeight;
    mWindowWidth = screenWidth;
    mWindowHeight = mWindowWidth * 9 / 16;
    minWindowWidth = (screenWidth < screenHeight ? screenWidth : screenHeight) / 2;
  }

  private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
    @Override public void onCompletion(MediaPlayer mediaPlayer) {
      Log.e(TAG, "onCompletion");
      mController.showEnd();
    }
  };

  private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
    @Override public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
      Log.e(TAG, "onError");
      return false;
    }
  };

  private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
    @Override public void onPrepared(MediaPlayer mediaPlayer) {
      Log.e(TAG, "onPrepared");
      mIsVideoReadyToBePlayed = true;
      if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
        startVideoPlayback();
      }
    }
  };

  private void startVideoPlayback() {
    Log.v(TAG, "startVideoPlayback");
    mController.onBeginPlay();
    mSurfaceHolder.setFixedSize(mVideoWidth, mVideoHeight);
    mMediaPlayer.start();
  }

  private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
    @Override public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
      Log.e(TAG, "onVideoSizeChanged width = " + width + ", height=" + height);
      if (width <= 0 || height <= 0) {
        Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
        return;
      }
      mIsVideoSizeKnown = true;
      mVideoWidth = width;
      mVideoHeight = height;
      if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
        startVideoPlayback();
      }
      updateVideoSize(width, height);
    }
  };

  private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
    @Override public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
      Log.e(TAG, "onBufferingUpdate i = " + i);
      if (i >= 99) {
        mController.updateSecond();
      }
    }
  };

  @Override public void playPause() {
    if (mMediaPlayer.isPlaying()) {
      mMediaPlayer.pause();
    } else {
      mMediaPlayer.start();
    }
  }

  @Override public void closePlayer() {
    if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
      mMediaPlayer.stop();
    }
    mServiceHelper.closeFloatWindow();
    if (mlayoutView.isAttachedToWindow()) {
      mWindowManager.removeView(mlayoutView);
    }
  }

  private void releaseMediaPlayer() {
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  private void doCleanUp() {
    mIsVideoReadyToBePlayed = false;
  }

  public void exitFloatWindow() {
    releaseMediaPlayer();
    doCleanUp();
  }

  @Override public boolean isPlaying() {
    return mMediaPlayer.isPlaying();
  }

  public void updateVideoSize(int width, int height) {
    Log.e("Hanjh", "updateVideoSize width = " + width + " height = " + height);
    if (mContext == null || mSurfaceView == null || null == mSurfaceView.getLayoutParams()) return;
    widthHeightRatio = (float) width / (float) height;
    Log.e("Hanjh", "widthHeightRatio = " + widthHeightRatio);
    int videoHeight = height;
    int videoWidth = width;
    if (width > mWindowWidth) {
      videoWidth = mWindowWidth;
      videoHeight = (int) ((float) videoWidth / widthHeightRatio);
    }
    ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
    lp.height = videoHeight;
    lp.width = videoWidth;
    wmParams.width = videoWidth;
    wmParams.height = videoHeight;
    mLastWidth = wmParams.width;
    mLastHeight = wmParams.height;
    mWindowManager.updateViewLayout(mlayoutView, wmParams);
    mSurfaceView.requestLayout();
  }

  private class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
    int lastw;
    float lastScale = 1;

    @Override public boolean onScale(ScaleGestureDetector detector) {
      float scale = detector.getScaleFactor();
      Log.e("Hanjh", "scale = " + scale);
      if (Math.abs(scale - lastScale) > TOLERATION) {
        updateViewSize((int) (lastw * scale), scale);
        lastScale = scale;
      }
      return false;
    }

    @Override public boolean onScaleBegin(ScaleGestureDetector detector) {
      lastScale = 1;
      lastw = wmParams.width;
      return true;
    }

    @Override public void onScaleEnd(ScaleGestureDetector detector) {
    }
  }

  private void updateViewSize(int newWidth, float scale) {
    // 待优化
    Log.e("Hanjh", "updateViewSize = " + newWidth + " scale " + scale);
    if (newWidth >= screenWidth) {
      // 宽度大于屏幕宽，直接置中，设为屏幕宽
      Log.w("Hanjh",
          "宽度大于屏幕宽，直接置中，设为屏幕宽 mLastWidth" + mLastWidth + " mLastHeight " + mLastHeight + " widthHeightRatio " + widthHeightRatio);
      if (wmParams.width != screenWidth) {
        wmParams.width = screenWidth;
        wmParams.height = (int) (screenWidth / widthHeightRatio);
        mLastWidth = wmParams.width;
        mLastHeight = wmParams.height;
        ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
        lp.width = mLastWidth;
        lp.height = mLastHeight;
      }
      wmParams.x = 0;
      mWindowManager.updateViewLayout(mlayoutView, wmParams);
      mSurfaceView.getHolder().setSizeFromLayout();
      return;
    }
    // 宽度小于屏幕宽
    Log.w("Hanjh", "宽度小于屏幕宽 mLastWidth" + mLastWidth + " mLastHeight " + mLastHeight + " widthHeightRatio " + widthHeightRatio);
    if (mLastWidth * scale < 600) return;
    wmParams.width = (int) (mLastWidth * scale);
    wmParams.height = (int) ((float) wmParams.width / widthHeightRatio);
    mLastWidth = wmParams.width;
    mLastHeight = wmParams.height;
    ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
    lp.width = mLastWidth;
    lp.height = mLastHeight;
    Log.w("Hanjhtest", "宽度小于屏幕宽 mLastWidth" + mLastWidth + " mLastHeight " + mLastHeight);
    mWindowManager.updateViewLayout(mlayoutView, wmParams);
    mSurfaceView.getHolder().setSizeFromLayout();
  }
}
