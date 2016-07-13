package io.vov.vitamio.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import java.io.IOException;

public class MediaPlayerDemo_Audio extends Activity {

  private static final String TAG = "MediaPlayerDemo";
  private MediaPlayer mMediaPlayer;
  private static final String MEDIA = "media";
  private static final int LOCAL_AUDIO = 1;
  private static final int STREAM_AUDIO = 2;
  private static final int RESOURCES_AUDIO = 3;
  private static final int LOCAL_VIDEO = 4;
  private static final int STREAM_VIDEO = 5;
  private String path;

  private TextView tx;

  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    if (!LibsChecker.checkVitamioLibs(this)) return;
    tx = new TextView(this);
    setContentView(tx);
    Bundle extras = getIntent().getExtras();
    playAudio(extras.getInt(MEDIA));
  }

  private void playAudio(Integer media) {
    try {
      switch (media) {
        case LOCAL_AUDIO:
          /**
           * TODO: Set the path variable to a local audio file path.
           */
          path = "";
          if (path == "") {
            // Tell the user to provide an audio file URL.
            Toast.makeText(MediaPlayerDemo_Audio.this, "Please edit MediaPlayer_Audio Activity, "
                + "and set the path variable to your audio file path."
                + " Your audio file must be stored on sdcard.", Toast.LENGTH_LONG).show();
            return;
          }
          mMediaPlayer = new MediaPlayer(this);
          mMediaPlayer.setDataSource(path);
          mMediaPlayer.prepare();
          mMediaPlayer.start();
          break;
        case RESOURCES_AUDIO:
          /**
           * TODO: Upload a audio file to res/raw folder and provide its resid in
           * MediaPlayer.create() method.
           */
          //Bug need fixed
          mMediaPlayer = createMediaPlayer(this, R.raw.test_cbr);
          mMediaPlayer.start();
      }
      tx.setText("Playing audio...");
    } catch (Exception e) {
      Log.e(TAG, "error: " + e.getMessage(), e);
    }
  }

  public MediaPlayer createMediaPlayer(Context context, int resid) {
    try {
      AssetFileDescriptor afd = context.getResources().openRawResourceFd(resid);
      MediaPlayer mp = new MediaPlayer(context);
      mp.setDataSource(afd.getFileDescriptor());
      afd.close();
      mp.prepare();
      return mp;
    } catch (IOException ex) {
      Log.d(TAG, "create failed:", ex);
      // fall through
    } catch (IllegalArgumentException ex) {
      Log.d(TAG, "create failed:", ex);
      // fall through
    } catch (SecurityException ex) {
      Log.d(TAG, "create failed:", ex);
      // fall through
    }
    return null;
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    if (mMediaPlayer != null) {
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }
}
