package media.explore.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import io.vov.vitamio.demo.R;

public class Settings {
  private Context mAppContext;
  private SharedPreferences mSharedPreferences;


  public Settings(Context context) {
    mAppContext = context.getApplicationContext();
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
  }

  public String getLastDirectory() {
    String key = mAppContext.getString(R.string.pref_key_last_directory);
    return mSharedPreferences.getString(key, "/");
  }

  public void setLastDirectory(String path) {
    String key = mAppContext.getString(R.string.pref_key_last_directory);
    mSharedPreferences.edit().putString(key, path).apply();
  }
}
