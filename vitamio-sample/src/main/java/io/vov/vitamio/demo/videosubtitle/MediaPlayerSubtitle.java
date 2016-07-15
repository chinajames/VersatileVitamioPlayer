package io.vov.vitamio.demo.videosubtitle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import com.app.AppActivity;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.demo.R;
import io.vov.vitamio.demo.VitamioMainActivity;
import io.vov.vitamio.demo.mediaplayers.MediaPlayerDemoList;
import java.io.IOException;

public class MediaPlayerSubtitle extends AppActivity implements Callback, OnPreparedListener, OnTimedTextListener {

  SurfaceView splayer;
  SurfaceHolder sholder;
  TextView tv;
  private MediaPlayer mediaPlayer;
  private String path = "";
  private static final String MEDIA = "media";
  public static final int LOCAL_AUDIO = MediaPlayerDemoList.LOCAL_AUDIO;
  public static final int STREAM_AUDIO = MediaPlayerDemoList.STREAM_AUDIO;
  public static final int RESOURCES_AUDIO = MediaPlayerDemoList.RESOURCES_AUDIO;
  public static final int LOCAL_VIDEO = MediaPlayerDemoList.LOCAL_VIDEO;
  public static final int STREAM_VIDEO = MediaPlayerDemoList.STREAM_VIDEO;
  public static final int STREAM_RTMP = MediaPlayerDemoList.STREAM_RTMP;

  public static Intent newIntent(Context context, String videoPath, String videoTitle, int mediaType) {
    Intent intent = new Intent(context, MediaPlayerSubtitle.class);
    intent.putExtra("videoPath", videoPath);
    intent.putExtra("videoTitle", videoTitle);
    intent.putExtra(MEDIA, mediaType);
    return intent;
  }

  public static void intentTo(Context context, String videoPath, String videoTitle, int mediaType) {
    context.startActivity(newIntent(context, videoPath, videoTitle, mediaType));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!LibsChecker.checkVitamioLibs(this)) return;
    setContentView(R.layout.subtitle_mediaplayer);
    tv = (TextView) findViewById(R.id.sub1);
    splayer = (SurfaceView) findViewById(R.id.surface);
    sholder = splayer.getHolder();
    sholder.setFormat(PixelFormat.RGBA_8888);
    sholder.addCallback(this);
  }

  private void playVideo() {
    path = getIntent().getStringExtra("videoPath");
    try {
      if (TextUtils.isEmpty(path)) {
        Toast.makeText(MediaPlayerSubtitle.this, "Please edit MediaPlayer Activity, "
            + "and set the path variable to your media file path."
            + " Your media file must be stored on sdcard.", Toast.LENGTH_LONG).show();
        return;
      }
      mediaPlayer = new MediaPlayer(this);
      mediaPlayer.setDataSource(path);
      mediaPlayer.setDisplay(sholder);
      mediaPlayer.prepareAsync();
      mediaPlayer.setOnPreparedListener(this);

      mediaPlayer.setOnTimedTextListener(this);

    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  @Override public void surfaceCreated(SurfaceHolder holder) {
    playVideo();
  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {


  }

  private void startVPback() {

    mediaPlayer.start();
  }

  @Override public void onPrepared(MediaPlayer arg0) {


    startVPback();
    mediaPlayer.addTimedTextSource(Environment.getExternalStorageDirectory() + "/12.srt");
    mediaPlayer.setTimedTextShown(true);
  }

  @Override protected void onPause() {

    super.onPause();
    relaMediaPlay();
  }

  private void relaMediaPlay() {

    if (mediaPlayer != null) {
      mediaPlayer.release();
      mediaPlayer = null;
    }
  }

  @Override protected void onDestroy() {

    super.onDestroy();
    relaMediaPlay();
  }

  @Override public void onTimedText(String text) {

    tv.setText(text);
  }

  @Override public void onTimedTextUpdate(byte[] pixels, int width, int height) {


  }
}
