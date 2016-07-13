package io.vov.vitamio.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoViewDemo extends Activity {


  private String path = "http://video19.ifeng.com/video06/2012/04/11/629da9ec-60d4-4814-a940-997e6487804a.mp4";
  private VideoView mVideoView;
  private EditText mEditText;

  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    if (!LibsChecker.checkVitamioLibs(this)) return;
    setContentView(R.layout.videoview);
    mEditText = (EditText) findViewById(R.id.url);
    mVideoView = (VideoView) findViewById(R.id.surface_view);
    if (path == "") {
      // Tell the user to provide a media file URL/path.
      Toast.makeText(VideoViewDemo.this, "Please edit VideoViewDemo Activity, and set path" + " variable to your media file URL/path",
          Toast.LENGTH_LONG).show();
      return;
    } else {
      /*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
      mVideoView.setVideoPath(path);
      mVideoView.setMediaController(new MediaController(this));
      mVideoView.requestFocus();

      mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override public void onPrepared(MediaPlayer mediaPlayer) {
          // optional need Vitamio 4.0
          mediaPlayer.setPlaybackSpeed(1.0f);
        }
      });
    }
  }

  public void startPlay(View view) {
    String url = mEditText.getText().toString();
    path = url;
    if (!TextUtils.isEmpty(url)) {
      mVideoView.setVideoPath(url);
    }
  }

  public void openVideo(View View) {
    mVideoView.setVideoPath(path);
  }
}
