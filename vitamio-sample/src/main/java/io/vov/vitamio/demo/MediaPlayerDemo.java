
package io.vov.vitamio.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.app.AppActivity;
import media.explore.activities.FileExplorerActivity;

public class MediaPlayerDemo extends AppActivity {
  public Button mlocalvideo;
  public Button mlocalvideoSurface;
  public Button mstreamvideo;
  public static final String MEDIA = "media";
  public static final int LOCAL_AUDIO = 1;
  public static final int STREAM_AUDIO = 2;
  public static final int RESOURCES_AUDIO = 3;
  public static final int LOCAL_VIDEO = 4;
  public static final int STREAM_VIDEO = 5;
  public static final int RESOURCES_VIDEO = 6;
  public static final int LOCAL_VIDEO_SURFACE = 7;

  @Override protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.mediaplayer_1);

    mlocalvideo = (Button) findViewById(R.id.localvideo);
    mlocalvideo.setOnClickListener(mLocalVideoListener);
    mlocalvideoSurface = (Button) findViewById(R.id.localvideo_setsurface);
    mlocalvideoSurface.setOnClickListener(mSetSurfaceVideoListener);
    mstreamvideo = (Button) findViewById(R.id.streamvideo);
    mstreamvideo.setOnClickListener(mStreamVideoListener);
  }

  private OnClickListener mLocalVideoListener = new OnClickListener() {
    public void onClick(View v) {
      Intent intent = new Intent(MediaPlayerDemo.this, FileExplorerActivity.class);
      startActivity(intent);
    }
  };

  private OnClickListener mSetSurfaceVideoListener = new OnClickListener() {
    public void onClick(View v) {
      Intent intent = new Intent(MediaPlayerDemo.this, MediaPlayerDemo_setSurface.class);
      intent.putExtra(MEDIA, LOCAL_VIDEO_SURFACE);
      startActivity(intent);
    }
  };

  private OnClickListener mStreamVideoListener = new OnClickListener() {
    public void onClick(View v) {
      MediaPlayerDemo_Video.intentTo(MediaPlayerDemo.this, "rtmp://live.hkstv.hk.lxdns.com/live/hks", "RTMP香港电视台", MediaPlayerDemo_Video.STREAM_VIDEO);
    }
  };

}
