package io.vov.vitamio.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.app.AppActivity;
import io.vov.vitamio.MediaMetadataRetriever;
import java.io.IOException;

public class MediaMetadataRetrieverDemo extends AppActivity {

  private String path = "";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    io.vov.vitamio.MediaMetadataRetriever retriever = new io.vov.vitamio.MediaMetadataRetriever(this);
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    path = settings.getString(VitamioMainActivity.LOCAL_VIDEO, "");
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
    }
    long durationMs = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
    String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

    setContentView(R.layout.media_metadata);
    TextView textView = (TextView) findViewById(R.id.textView);
    textView.setText("durationMs:" + durationMs + "\nartist:" + artist + "\ntitle:" + title);
  }
}
