package media.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import com.app.AppActivity;
import io.vov.vitamio.demo.R;
import media.explore.fragments.RecentMediaListFragment;

public class RecentMediaActivity extends AppActivity {

  private static String VideoActivity = FileExplorerActivity.MediaPlayer;


  public static Intent newIntent(Context context) {
    Intent intent = new Intent(context, RecentMediaActivity.class);
    return intent;
  }

  public static void intentTo(Context context,String videoActivity) {
    VideoActivity = videoActivity;
    context.startActivity(newIntent(context));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Fragment newFragment = RecentMediaListFragment.newInstance(VideoActivity);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    transaction.replace(R.id.body, newFragment);
    transaction.commit();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_recent) {
      RecentMediaActivity.intentTo(this, VideoActivity);
    } else if (id == R.id.action_sample) {
      SampleMediaActivity.intentTo(this ,VideoActivity);
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onPrepareOptionsMenu(Menu menu) {
    boolean show = super.onPrepareOptionsMenu(menu);
    if (!show) return show;

    MenuItem item = menu.findItem(R.id.action_sample);
    if (item != null) item.setVisible(true);

    return true;
  }
}
