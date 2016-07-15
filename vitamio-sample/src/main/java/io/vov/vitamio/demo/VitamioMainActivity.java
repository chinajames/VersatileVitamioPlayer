package io.vov.vitamio.demo;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.demo.mediaplayers.MediaPlayerDemoList;
import io.vov.vitamio.demo.videosubtitle.VideoSubtitleList;
import io.vov.vitamio.demo.videoview.VideoViewDemoList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import media.explore.activities.FileExplorerActivity;

/**
 * List
 */
public class VitamioMainActivity extends ListActivity {

  public static final String LOCAL_VIDEO = "LOCAL_VIDEO";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (!LibsChecker.checkVitamioLibs(this)) return;
    setListAdapter(new SimpleAdapter(this, getData(), android.R.layout.simple_list_item_1, new String[] { "title" },
        new int[] { android.R.id.text1 }));

    AppCompatCallback callback = new AppCompatCallback() {
      @Override
      public void onSupportActionModeStarted(ActionMode actionMode) {
      }

      @Override
      public void onSupportActionModeFinished(ActionMode actionMode) {
      }

      @Override
      public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
      }
    };

    AppCompatDelegate delegate = AppCompatDelegate.create(this, callback);

    delegate.onCreate(savedInstanceState);
    delegate.setContentView(R.layout.activity_list);

    Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
    delegate.setSupportActionBar(toolbar);
    delegate.getSupportActionBar().setDisplayShowHomeEnabled(true);
  }

  protected List<Map<String, Object>> getData() {
    List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();
    //addItem(myData, getResources().getString(R.string.choose_local_video), new Intent(this, FileExplorerActivity.class));
    addItem(myData, "MediaPlayer", new Intent(this, MediaPlayerDemoList.class));
    addItem(myData, "VideoView", new Intent(this, VideoViewDemoList.class));

    //Intent intent = new Intent(this, FileExplorerActivity.class);
    //intent.putExtra(FileExplorerActivity.ActionFileExplore,FileExplorerActivity.MediaMetadataRetriever);
    //startActivity(intent);
    //addItem(myData, "MediaMetadataRetriever",intent);

    addItem(myData, "VideoSubtitle", new Intent(this, VideoSubtitleList.class));
    return myData;
  }

  protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
    Map<String, Object> temp = new HashMap<String, Object>();
    temp.put("title", name);
    temp.put("intent", intent);
    data.add(temp);
  }

  @SuppressWarnings("unchecked") @Override protected void onListItemClick(ListView l, View v, int position, long id) {
    Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
    Intent intent = (Intent) map.get("intent");
    startActivity(intent);
  }
}
