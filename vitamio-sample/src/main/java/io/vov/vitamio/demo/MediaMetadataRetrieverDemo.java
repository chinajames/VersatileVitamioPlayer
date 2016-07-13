package io.vov.vitamio.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import io.vov.vitamio.MediaMetadataRetriever;
import java.io.IOException;

public class MediaMetadataRetrieverDemo extends Activity {

  private String path = "/storage/emulated/0/JQuery实战视频教程[王兴魁]/02.[jQuery]第1章 jQuery入门[下].avi";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    io.vov.vitamio.MediaMetadataRetriever retriever = new io.vov.vitamio.MediaMetadataRetriever(this);
    try {
      if (TextUtils.isEmpty(path)) {
        Toast.makeText(MediaMetadataRetrieverDemo.this, "Please edit MediaMetadataRetrieverDemo Activity, "
            + "and set the path variable to your audio file path."
            + " Your audio file must be stored on sdcard.", Toast.LENGTH_LONG).show();
        return;
      }
      retriever.setDataSource(path);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    long durationMs = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

    setContentView(R.layout.media_metadata);
    TextView textView = (TextView) findViewById(R.id.textView);
    textView.setText("durationMs:"+durationMs + "\nartist:" + artist + "\ntitle:" + title);
  }
}
