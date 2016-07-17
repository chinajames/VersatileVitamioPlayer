package io.vov.vitamio.demo.floating;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.demo.R;
import java.io.IOException;

public class FloatPlayerUI extends FrameLayout implements IMediaPlayer {
  public final String TAG = this.getClass().getSimpleName();
  private Activity mContext;
  private FloatPlayerController mController;
  private SurfaceView mSurfaceView;
  private MediaPlayer mMediaPlayer;
  private SurfaceHolder mSurfaceHolder;
  private IServiceHelper mServiceHelper;
  private static String videoPath;
  private boolean mIsVideoReadyToBePlayed = false;
  private int mVideoWidth;
  private int mVideoHeight;
  private boolean mIsVideoSizeKnown = false;

  public FloatPlayerUI(Activity context, IServiceHelper helper, String videoPaths) {
    super(context);
    videoPath = videoPaths;
    mContext = context;
    mServiceHelper = helper;

    initSurfaceView();
    initController();
    mController.showLoading();
  }

  private void initSurfaceView() {
    mSurfaceView = new SurfaceView(mContext);
    //mSurfaceView.setBackgroundColor(getResources().getColor(R.color.black));
    LayoutParams surfaceViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    surfaceViewParams.gravity = Gravity.CENTER;
    LayoutParams lp = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.float_window_root_width),
        getResources().getDimensionPixelSize(R.dimen.float_window_root_height), Gravity.CENTER);
    mSurfaceView.setLayoutParams(lp);
    if (mSurfaceView.getParent() == null) {
      removeAllViews();
      addView(mSurfaceView, surfaceViewParams);
    }

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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initController() {
    mController = new FloatPlayerController(mContext, this);
    RelativeLayout.LayoutParams controllParams =
        new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    controllParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    if (mController.getParent() == null) {
      addView(mController, controllParams);
    }
    mController.setVisibility(View.VISIBLE);
  }

  private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
    @Override public void onCompletion(MediaPlayer mediaPlayer) {
      Log.e(TAG, "onCompletion");
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
      if (width == 0 || height == 0) {
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
    mMediaPlayer.stop();
    mServiceHelper.closeFloatWindow();
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

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    releaseMediaPlayer();
    doCleanUp();
  }

  @Override public boolean isPlaying() {
    return mMediaPlayer.isPlaying();
  }

  public void updateVideoSize(int width, int height) {
    if (mContext == null || mSurfaceView == null || null == mSurfaceView.getLayoutParams()) return;
    int playerWidth = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
    int playerHeight = mContext.getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
    int videoWidth = ViewGroup.LayoutParams.MATCH_PARENT;
    int videoHeight = ViewGroup.LayoutParams.MATCH_PARENT;
    if (width > 0 && height > 0) {
      float videoAspect = ((float) width) / height;
      float playerAspect = ((float) playerWidth) / playerHeight;
      if (videoAspect > playerAspect) {
        videoWidth = playerWidth;
        videoHeight = (int) (playerWidth * (1 / videoAspect));
      } else {
        videoWidth = (int) (playerHeight * videoAspect);
        videoHeight = playerHeight;
      }
    }
    ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
    lp.height = videoHeight;
    lp.width = videoWidth;
    mSurfaceView.requestLayout();
  }
}
