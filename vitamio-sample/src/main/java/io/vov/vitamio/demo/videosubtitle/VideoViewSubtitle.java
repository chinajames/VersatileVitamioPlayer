package io.vov.vitamio.demo.videosubtitle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.app.AppActivity;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnTimedTextListener;
import io.vov.vitamio.demo.R;
import io.vov.vitamio.demo.mediaplayers.MediaPlayerDemoList;
import io.vov.vitamio.widget.VideoView;

public class VideoViewSubtitle extends AppActivity {

  private String path = "";
  private String subtitle_path = "";
  private VideoView mVideoView;
  private TextView mSubtitleView;
  private long mPosition = 0;
  private int mVideoLayout = 0;
  private static final String MEDIA = "media";
  public static final int LOCAL_AUDIO = MediaPlayerDemoList.LOCAL_AUDIO;
  public static final int STREAM_AUDIO = MediaPlayerDemoList.STREAM_AUDIO;
  public static final int RESOURCES_AUDIO = MediaPlayerDemoList.RESOURCES_AUDIO;
  public static final int LOCAL_VIDEO = MediaPlayerDemoList.LOCAL_VIDEO;
  public static final int STREAM_VIDEO = MediaPlayerDemoList.STREAM_VIDEO;
  public static final int STREAM_RTMP = MediaPlayerDemoList.STREAM_RTMP;

  public static Intent newIntent(Context context, String videoPath, String videoTitle, int mediaType) {
    Intent intent = new Intent(context, VideoViewSubtitle.class);
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
    setContentView(R.layout.subtitle_videoview);
    mVideoView = (VideoView) findViewById(R.id.surface_view);
    mSubtitleView = (TextView) findViewById(R.id.subtitle_view);

    path = getIntent().getStringExtra("videoPath");
    if (TextUtils.isEmpty(path)) {
      // Tell the user to provide a media file URL/path.
      Toast.makeText(VideoViewSubtitle.this,
          "Please edit VideoViewSubtitle Activity, and set path" + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
      return;
    } else {
      /*
			 * Alternatively,for streaming media you can use
			 * mVideoView.setVideoURI(Uri.parse(URLstring));
			 */
      mVideoView.setVideoPath(path);

      // mVideoView.setMediaController(new MediaController(this));
      mVideoView.requestFocus();

      mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override public void onPrepared(MediaPlayer mediaPlayer) {
          // optional need Vitamio 4.0
          mediaPlayer.setPlaybackSpeed(1.0f);
          mVideoView.addTimedTextSource(subtitle_path);
          mVideoView.setTimedTextShown(true);
        }
      });
      mVideoView.setOnTimedTextListener(new OnTimedTextListener() {

        @Override public void onTimedText(String text) {
          mSubtitleView.setText(text);
        }

        @Override public void onTimedTextUpdate(byte[] pixels, int width, int height) {

        }
      });
    }
  }

  @Override protected void onPause() {
    mPosition = mVideoView.getCurrentPosition();
    mVideoView.stopPlayback();
    super.onPause();
  }

  @Override protected void onResume() {
    if (mPosition > 0) {
      mVideoView.seekTo(mPosition);
      mPosition = 0;
    }
    super.onResume();
    mVideoView.start();
  }

  public void changeLayout(View view) {
    mVideoLayout++;
    if (mVideoLayout == 4) {
      mVideoLayout = 0;
    }
    switch (mVideoLayout) {
      case 0:
        mVideoLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
        view.setBackgroundResource(R.drawable.mediacontroller_sreen_size_100);
        break;
      case 1:
        mVideoLayout = VideoView.VIDEO_LAYOUT_SCALE;
        view.setBackgroundResource(R.drawable.mediacontroller_screen_fit);
        break;
      case 2:
        mVideoLayout = VideoView.VIDEO_LAYOUT_STRETCH;
        view.setBackgroundResource(R.drawable.mediacontroller_screen_size);
        break;
      case 3:
        mVideoLayout = VideoView.VIDEO_LAYOUT_ZOOM;
        view.setBackgroundResource(R.drawable.mediacontroller_sreen_size_crop);

        break;
    }
    mVideoView.setVideoLayout(mVideoLayout, 0);
  }
}
