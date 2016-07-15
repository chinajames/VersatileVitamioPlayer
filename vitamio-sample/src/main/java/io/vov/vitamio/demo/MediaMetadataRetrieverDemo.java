package io.vov.vitamio.demo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.app.AppActivity;
import io.vov.vitamio.MediaMetadataRetriever;
import io.vov.vitamio.demo.mediaplayers.MediaPlayerDemoList;
import java.io.IOException;

public class MediaMetadataRetrieverDemo extends AppActivity {

  private String path = "";
  private static final String MEDIA = "media";
  public static final int LOCAL_AUDIO = MediaPlayerDemoList.LOCAL_AUDIO;
  public static final int STREAM_AUDIO = MediaPlayerDemoList.STREAM_AUDIO;
  public static final int RESOURCES_AUDIO = MediaPlayerDemoList.RESOURCES_AUDIO;
  public static final int LOCAL_VIDEO = MediaPlayerDemoList.LOCAL_VIDEO;
  public static final int STREAM_VIDEO = MediaPlayerDemoList.STREAM_VIDEO;
  public static final int STREAM_RTMP = MediaPlayerDemoList.STREAM_RTMP;

  public static Intent newIntent(Context context, String videoPath, String videoTitle, int mediaType) {
    Intent intent = new Intent(context, MediaMetadataRetrieverDemo.class);
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
    io.vov.vitamio.MediaMetadataRetriever retriever = new io.vov.vitamio.MediaMetadataRetriever(this);
    path = getIntent().getStringExtra("videoPath");
    if (TextUtils.isEmpty(path)) {
      Toast.makeText(MediaMetadataRetrieverDemo.this, "Please edit MediaMetadataRetrieverDemo Activity, "
          + "and set the path variable to your audio file path."
          + " Your audio file must be stored on sdcard.", Toast.LENGTH_LONG).show();
      return;
    }
    try {
      retriever.setDataSource(path);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e){
      e.printStackTrace();
    }
    long durationMs = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

    setContentView(R.layout.media_metadata);
    TextView textView = (TextView) findViewById(R.id.textView);
    textView.setText("durationMs:" + durationMs + "\nartist:" + artist + "\ntitle:" + title);
  }
}
