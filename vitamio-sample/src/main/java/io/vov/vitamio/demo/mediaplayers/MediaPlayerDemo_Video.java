package io.vov.vitamio.demo.mediaplayers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
import com.app.AppActivity;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import io.vov.vitamio.demo.R;
import io.vov.vitamio.widget.MediaController;
import media.explore.content.RecentMediaStorage;

public class MediaPlayerDemo_Video extends AppActivity
    implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

  private int mVideoWidth;
  private int mVideoHeight;
  private MediaPlayer mMediaPlayer;
  private SurfaceView mPreview;
  private SurfaceHolder holder;
  private static final String MEDIA = "media";
  public static final int LOCAL_AUDIO = MediaPlayerDemoList.LOCAL_AUDIO;
  public static final int STREAM_AUDIO = MediaPlayerDemoList.STREAM_AUDIO;
  public static final int RESOURCES_AUDIO = MediaPlayerDemoList.RESOURCES_AUDIO;
  public static final int LOCAL_VIDEO = MediaPlayerDemoList.LOCAL_VIDEO;
  public static final int STREAM_VIDEO = MediaPlayerDemoList.STREAM_VIDEO;
  public static final int STREAM_RTMP = MediaPlayerDemoList.STREAM_RTMP;
  private boolean mIsVideoSizeKnown = false;
  private boolean mIsVideoReadyToBePlayed = false;

  private String mVideoPath;
  private Uri mVideoUri;

  /**
   * Called when the activity is first created.
   */
  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    if (!LibsChecker.checkVitamioLibs(this)) return;
    setContentView(R.layout.mediaplayer_video);
    mPreview = (SurfaceView) findViewById(R.id.surface);
    holder = mPreview.getHolder();
    holder.addCallback(this);
    holder.setFormat(PixelFormat.RGBA_8888);
  }

  public static Intent newIntent(Context context, String videoPath, String videoTitle, int mediaType) {
    Intent intent = new Intent(context, MediaPlayerDemo_Video.class);
    intent.putExtra("videoPath", videoPath);
    intent.putExtra("videoTitle", videoTitle);
    intent.putExtra(MEDIA, mediaType);
    return intent;
  }

  public static void intentTo(Context context, String videoPath, String videoTitle, int mediaType) {
    context.startActivity(newIntent(context, videoPath, videoTitle, mediaType));
  }

  private void playVideo(Integer Media) {
    doCleanUp();
    try {

      switch (Media) {
        case LOCAL_VIDEO:
          mVideoPath = getIntent().getStringExtra("videoPath");
          if (TextUtils.isEmpty(mVideoPath)) {
            Toast.makeText(MediaPlayerDemo_Video.this,
                "Please edit MediaPlayerDemo_Video Activity, and set the path variable to your media file path. Your media file must be stored on sdcard.",
                Toast.LENGTH_LONG).show();
            return;
          }

          Intent intent = getIntent();
          String intentAction = intent.getAction();
          if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
              mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
              mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
              if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                String scheme = mVideoUri.getScheme();
                if (TextUtils.isEmpty(scheme)) {
                  Log.e(TAG, "Null unknown ccheme\n");
                  finish();
                  return;
                }
                if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                  mVideoPath = mVideoUri.getPath();
                } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
                  Log.e(TAG, "Can not resolve content below Android-ICS\n");
                  finish();
                  return;
                } else {
                  Log.e(TAG, "Unknown scheme " + scheme + "\n");
                  finish();
                  return;
                }
              }
            }
          }

          if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
          }

          break;
        case STREAM_RTMP:
        case STREAM_VIDEO:
          mVideoPath = getIntent().getStringExtra("videoPath");
          if (TextUtils.isEmpty(mVideoPath)) {
            Toast.makeText(MediaPlayerDemo_Video.this,
                "Please edit MediaPlayerDemo_Video Activity, and set the path variable to your media file URL.", Toast.LENGTH_LONG).show();
            return;
          }
          break;
      }

      mMediaPlayer = new MediaPlayer(this);
      Log.w("hanjh", "mVideoPath: " + mVideoPath);//"/storage/emulated/0/JQuery实战视频教程[王兴魁]/02.[jQuery]第1章 jQuery入门[下].avi"
      mMediaPlayer.setDataSource(mVideoPath);
      mMediaPlayer.setDisplay(holder);
      mMediaPlayer.prepareAsync();
      mMediaPlayer.setOnBufferingUpdateListener(this);
      mMediaPlayer.setOnCompletionListener(this);
      mMediaPlayer.setOnPreparedListener(this);
      mMediaPlayer.setOnVideoSizeChangedListener(this);
      setVolumeControlStream(AudioManager.STREAM_MUSIC);
    } catch (Exception e) {
      Log.e(TAG, "error: " + e.getMessage(), e);
    }
  }

  public void onBufferingUpdate(MediaPlayer arg0, int percent) {
    Log.d(TAG, "onBufferingUpdate percent:" + percent);
    if (null != pb && null != downloadRateView && null != loadRateView) {
      if (percent < 100) {
        loadRateView.setText(percent + "%");
        pb.setVisibility(View.VISIBLE);
        downloadRateView.setVisibility(View.VISIBLE);
        loadRateView.setVisibility(View.VISIBLE);
      } else {
        pb.setVisibility(View.GONE);
        downloadRateView.setVisibility(View.GONE);
        loadRateView.setVisibility(View.GONE);
      }
    }
  }

  public void onCompletion(MediaPlayer arg0) {
    Log.d(TAG, "onCompletion called");
  }

  public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
    Log.v(TAG, "onVideoSizeChanged called");
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
    if (null != pb && null != downloadRateView && null != loadRateView) {
      pb.setVisibility(View.GONE);
      downloadRateView.setVisibility(View.GONE);
      loadRateView.setVisibility(View.GONE);
      pb = null;
      downloadRateView = null;
      loadRateView = null;
    }
  }

  public void onPrepared(MediaPlayer mediaplayer) {
    Log.d(TAG, "onPrepared called");
    mIsVideoReadyToBePlayed = true;
    if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
      startVideoPlayback();
    }
  }

  public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
    Log.d(TAG, "surfaceChanged called");
  }

  public void surfaceDestroyed(SurfaceHolder surfaceholder) {
    Log.d(TAG, "surfaceDestroyed called");
  }

  public void surfaceCreated(SurfaceHolder holder) {
    Log.d(TAG, "surfaceCreated called");
    playVideo(getIntent().getIntExtra(MEDIA, STREAM_VIDEO));
  }

  @Override protected void onPause() {
    super.onPause();
    releaseMediaPlayer();
    doCleanUp();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    releaseMediaPlayer();
    doCleanUp();
  }

  private void releaseMediaPlayer() {
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }

  private void doCleanUp() {
    mVideoWidth = 0;
    mVideoHeight = 0;
    mIsVideoReadyToBePlayed = false;
    mIsVideoSizeKnown = false;
  }

  private void startVideoPlayback() {
    Log.v(TAG, "startVideoPlayback");
    holder.setFixedSize(mVideoWidth, mVideoHeight);
    mMediaPlayer.start();
  }
}
