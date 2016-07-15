package media.explore.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import com.app.AppActivity;
import com.squareup.otto.Subscribe;
import io.vov.vitamio.demo.MediaMetadataRetrieverDemo;
import io.vov.vitamio.demo.R;
import io.vov.vitamio.demo.VitamioMainActivity;
import io.vov.vitamio.demo.floating.VideoPlayerService;
import io.vov.vitamio.demo.mediaplayers.MediaPlayerDemo_Video;
import io.vov.vitamio.demo.videosubtitle.MediaPlayerSubtitle;
import io.vov.vitamio.demo.videosubtitle.VideoViewSubtitle;
import io.vov.vitamio.demo.videoview.VideoViewDemo;
import java.io.File;
import java.io.IOException;
import media.explore.application.Settings;
import media.explore.eventbus.FileExplorerEvents;
import media.explore.fragments.FileListFragment;

public class FileExplorerActivity extends AppActivity {
  private Settings mSettings;
  public static final String ActionFileExplore = "ActionFileExplore";
  public static final String MediaPlayer = "1";
  public static final String VideoView = "2";
  public static final String MediaMetadataRetriever = "3";
  public static final String MediaPlayerSubtitles = "4";
  public static final String VideoViewSubtitles = "5";
  public static final String MediaPlayerFloating = "6";
  private String VideoActivity = MediaPlayer;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (mSettings == null) {
      mSettings = new Settings(this);
    }

    String lastDirectory = mSettings.getLastDirectory();
    if (!TextUtils.isEmpty(lastDirectory) && new File(lastDirectory).isDirectory()) {
      doOpenDirectory(lastDirectory, false);
    } else {
      doOpenDirectory("/", false);
    }
    VideoActivity = getIntent().getStringExtra(ActionFileExplore);
  }

  @Override protected void onResume() {
    super.onResume();

    FileExplorerEvents.getBus().register(this);
  }

  @Override protected void onPause() {
    super.onPause();

    FileExplorerEvents.getBus().unregister(this);
  }

  private void doOpenDirectory(String path, boolean addToBackStack) {
    Fragment newFragment = FileListFragment.newInstance(path);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    transaction.replace(R.id.body, newFragment);

    if (addToBackStack) transaction.addToBackStack(null);
    transaction.commit();
  }

  @Subscribe public void onClickFile(FileExplorerEvents.OnClickFile event) {
    File f = event.mFile;
    try {
      f = f.getAbsoluteFile();
      f = f.getCanonicalFile();
      if (TextUtils.isEmpty(f.toString())) f = new File("/");
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (f.isDirectory()) {
      String path = f.toString();
      mSettings.setLastDirectory(path);
      doOpenDirectory(path, true);
    } else if (f.exists()) {
      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString(VitamioMainActivity.LOCAL_VIDEO, f.getPath()).apply();
      if (VideoActivity.equals(MediaPlayer)) {
        MediaPlayerDemo_Video.intentTo(this, f.getPath(), f.getName(), MediaPlayerDemo_Video.LOCAL_VIDEO);
      } else if (VideoActivity.equals(MediaMetadataRetriever)) {
        MediaMetadataRetrieverDemo.intentTo(this, f.getPath(), f.getName(), MediaPlayerDemo_Video.LOCAL_VIDEO);
      } else if (VideoActivity.equals(MediaPlayerSubtitles)) {
        MediaPlayerSubtitle.intentTo(this, f.getPath(), f.getName(), MediaPlayerDemo_Video.LOCAL_VIDEO);
      } else if (VideoActivity.equals(VideoViewSubtitles)) {
        VideoViewSubtitle.intentTo(this, f.getPath(), f.getName(), MediaPlayerDemo_Video.LOCAL_VIDEO);
      } else if (VideoActivity.equals(MediaPlayerFloating)) {
        VideoPlayerService.startService(this,f.getPath());
      } else {
        VideoViewDemo.intentTo(this, f.getPath(), f.getName(), MediaPlayerDemo_Video.LOCAL_VIDEO);
      }

      finish();
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_recent) {
      RecentMediaActivity.intentTo(this, VideoActivity);
    } else if (id == R.id.action_sample) {
      SampleMediaActivity.intentTo(this, VideoActivity);
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    boolean show = super.onPrepareOptionsMenu(menu);
    if (!show) return show;

    MenuItem item = menu.findItem(R.id.action_recent);
    if (item != null) item.setVisible(true);

    MenuItem item2 = menu.findItem(R.id.action_sample);
    if (item2 != null) item2.setVisible(true);

    return true;
  }
}
