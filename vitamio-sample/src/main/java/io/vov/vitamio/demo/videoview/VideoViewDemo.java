package io.vov.vitamio.demo.videoview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.app.AppActivity;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.demo.R;
import io.vov.vitamio.demo.mediaplayers.MediaPlayerDemoList;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoViewDemo extends AppActivity {

  private String mVideoPath, mVideoTitle;
  private static final String MEDIA = "media";
  public static final int LOCAL_AUDIO = MediaPlayerDemoList.LOCAL_AUDIO;
  public static final int STREAM_AUDIO = MediaPlayerDemoList.STREAM_AUDIO;
  public static final int RESOURCES_AUDIO = MediaPlayerDemoList.RESOURCES_AUDIO;
  public static final int LOCAL_VIDEO = MediaPlayerDemoList.LOCAL_VIDEO;
  public static final int STREAM_VIDEO = MediaPlayerDemoList.STREAM_VIDEO;
  public static final int STREAM_RTMP = MediaPlayerDemoList.STREAM_RTMP;

  private VideoView mVideoView;
  private EditText mEtVideoPath;
  private TextView mTvVideoTitle;

  public static Intent newIntent(Context context, String videoPath, String videoTitle, int mediaType) {
    Intent intent = new Intent(context, VideoViewDemo.class);
    intent.putExtra("videoPath", videoPath);
    intent.putExtra("videoTitle", videoTitle);
    intent.putExtra(MEDIA, mediaType);
    return intent;
  }

  public static void intentTo(Context context, String videoPath, String videoTitle, int mediaType) {
    context.startActivity(newIntent(context, videoPath, videoTitle, mediaType));
  }

  @Override public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    if (!LibsChecker.checkVitamioLibs(this)) return;
    setContentView(R.layout.videoview);
    mTvVideoTitle = (TextView) findViewById(R.id.video_title);
    mEtVideoPath = (EditText) findViewById(R.id.video_path);
    mVideoView = (VideoView) findViewById(R.id.surface_view);
    mVideoPath = getIntent().getStringExtra("videoPath");
    mVideoTitle = getIntent().getStringExtra("videoTitle");
    if (TextUtils.isEmpty(mVideoPath)) {
      Toast.makeText(VideoViewDemo.this, "Please edit VideoViewDemo Activity, and set path variable to your media file URL/path",
          Toast.LENGTH_LONG).show();
      return;
    } else {
      mTvVideoTitle.setText(TextUtils.isEmpty(mVideoTitle) ? "" : mVideoTitle);
      mEtVideoPath.setText(mVideoPath);
      /*
       * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
      mVideoView.setVideoPath(mVideoPath);
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
    String url = mEtVideoPath.getText().toString();
    mVideoPath = url;
    if (!TextUtils.isEmpty(url)) {
      mVideoView.setVideoPath(url);
    }
  }

  public void openVideo(View View) {
    mVideoView.setVideoPath(mVideoPath);
  }
}
