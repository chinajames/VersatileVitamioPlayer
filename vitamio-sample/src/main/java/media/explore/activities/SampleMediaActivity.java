package media.explore.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import com.app.AppActivity;
import io.vov.vitamio.demo.R;
import media.explore.fragments.SampleMediaListFragment;

public class SampleMediaActivity extends AppActivity {

  private static String VideoActivity = FileExplorerActivity.MediaPlayer;

  public static Intent newIntent(Context context) {
    Intent intent = new Intent(context, SampleMediaActivity.class);
    return intent;
  }

  public static void intentTo(Context context,String videoActivity) {
    VideoActivity = videoActivity;
    context.startActivity(newIntent(context));
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Fragment newFragment = SampleMediaListFragment.newInstance(VideoActivity);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

    transaction.replace(R.id.body, newFragment);
    transaction.commit();
  }

}
