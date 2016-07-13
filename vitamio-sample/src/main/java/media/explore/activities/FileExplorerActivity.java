package media.explore.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import com.squareup.otto.Subscribe;
import io.vov.vitamio.demo.MediaPlayerDemo_Video;
import io.vov.vitamio.demo.R;
import java.io.File;
import java.io.IOException;
import media.explore.application.AppActivity;
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
      MediaPlayerDemo_Video.intentTo(this, f.getPath(), f.getName(), MediaPlayerDemo_Video.LOCAL_VIDEO);
    }
  }
}
