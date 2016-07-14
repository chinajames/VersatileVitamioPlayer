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
import io.vov.vitamio.demo.MediaPlayerDemo_Video;
import io.vov.vitamio.demo.R;
import io.vov.vitamio.demo.VitamioMainActivity;
import java.io.File;
import java.io.IOException;
import media.explore.application.Settings;
import media.explore.eventbus.FileExplorerEvents;
import media.explore.fragments.FileListFragment;

public class FileExplorerActivity extends AppActivity {
  private Settings mSettings;

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
      editor.putString(VitamioMainActivity.LOCAL_VIDEO,f.getPath()).apply();
      MediaPlayerDemo_Video.intentTo(this, f.getPath(), f.getName(), MediaPlayerDemo_Video.LOCAL_VIDEO);
    }
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
