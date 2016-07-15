package io.vov.vitamio.demo.videosubtitle;

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
import io.vov.vitamio.demo.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import media.explore.activities.FileExplorerActivity;

public class VideoSubtitleList extends ListActivity {

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

    toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

  }

  protected List<Map<String, Object>> getData() {
    List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

    Intent intent0 = new Intent(this, FileExplorerActivity.class);
    intent0.putExtra(FileExplorerActivity.ActionFileExplore, FileExplorerActivity.MediaPlayerSubtitles);
    startActivity(intent0);
    addItem(myData, "MediaPlayerSubtitleLocal", intent0);

    Intent intent1 = new Intent(this, FileExplorerActivity.class);
    intent1.putExtra(FileExplorerActivity.ActionFileExplore, FileExplorerActivity.VideoViewSubtitles);
    startActivity(intent1);
    addItem(myData, "VideoViewSubtitleLocal",intent1);

    Intent intent2 = new Intent(this, VideoViewSubtitle.class);
    intent2.putExtra("videoPath", "http://video19.ifeng.com/video06/2012/04/11/629da9ec-60d4-4814-a940-997e6487804a.mp4");
    intent2.putExtra("videoTitle", "陈思成");
    addItem(myData, "VideoViewSubtitleOnline", intent2);
    return myData;
  }

  protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
    Map<String, Object> temp = new HashMap<String, Object>();
    temp.put("title", name);
    temp.put("intent", intent);
    data.add(temp);
  }

  @Override protected void onListItemClick(ListView l, View v, int position, long id) {
    @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) l.getItemAtPosition(position);
    Intent intent = (Intent) map.get("intent");
    startActivity(intent);
  }
}
