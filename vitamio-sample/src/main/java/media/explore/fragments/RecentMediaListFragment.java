package media.explore.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import io.vov.vitamio.demo.R;
import io.vov.vitamio.demo.floating.VideoPlayerService;
import io.vov.vitamio.demo.mediaplayers.MediaPlayerDemo_Video;
import io.vov.vitamio.demo.videosubtitle.MediaPlayerSubtitle;
import io.vov.vitamio.demo.videosubtitle.VideoViewSubtitle;
import io.vov.vitamio.demo.videoview.VideoViewDemo;
import media.explore.activities.FileExplorerActivity;
import media.explore.content.RecentMediaStorage;

public class RecentMediaListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
  private ListView mFileListView;
  private RecentMediaAdapter mAdapter;
  public static final String ActionFileExplore = "ActionFileExplore";
  private String VideoActivity = FileExplorerActivity.MediaPlayer;

  public static RecentMediaListFragment newInstance(String videoActivitys) {
    RecentMediaListFragment f = new RecentMediaListFragment();
    Bundle bundle = new Bundle();
    bundle.putString(ActionFileExplore, videoActivitys);
    //向detailFragment传入参数
    f.setArguments(bundle);
    return f;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_file_list, container, false);
    mFileListView = (ListView) viewGroup.findViewById(R.id.file_list_view);
    if (getArguments().containsKey(ActionFileExplore)) {
      VideoActivity = getArguments().getString(ActionFileExplore);
    }
    return viewGroup;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    final Activity activity = getActivity();

    mAdapter = new RecentMediaAdapter(activity);
    mFileListView.setAdapter(mAdapter);
    mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
        String url = mAdapter.getUrl(position);
        String name = mAdapter.getName(position);
        Log.w("hanjh", "url: " + url + "\nname: " + name);
        if (VideoActivity.equals(FileExplorerActivity.MediaPlayer)) {
          MediaPlayerDemo_Video.intentTo(activity, url, name, MediaPlayerDemo_Video.LOCAL_VIDEO);
        } else if (VideoActivity.equals(FileExplorerActivity.MediaPlayerSubtitles)) {
          MediaPlayerSubtitle.intentTo(activity, url, name, MediaPlayerDemo_Video.LOCAL_VIDEO);
        } else if (VideoActivity.equals(FileExplorerActivity.VideoViewSubtitles)) {
          VideoViewSubtitle.intentTo(activity, url, name, MediaPlayerDemo_Video.LOCAL_VIDEO);
        } else if (VideoActivity.equals(FileExplorerActivity.MediaPlayerFloating)) {
          VideoPlayerService.startService(activity,url);
          getActivity().finish();
        } else {
          VideoViewDemo.intentTo(activity, url, name, MediaPlayerDemo_Video.LOCAL_VIDEO);
        }
      }
    });

    getLoaderManager().initLoader(2, null, this);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    return new RecentMediaStorage.CursorLoader(getActivity());
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
    mAdapter.notifyDataSetChanged();
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {

  }

  final class RecentMediaAdapter extends SimpleCursorAdapter {
    private int mIndex_id = -1;
    private int mIndex_url = -1;
    private int mIndex_name = -1;

    public RecentMediaAdapter(Context context) {
      super(context, android.R.layout.simple_list_item_2, null,
          new String[] { RecentMediaStorage.Entry.COLUMN_NAME_NAME, RecentMediaStorage.Entry.COLUMN_NAME_URL },
          new int[] { android.R.id.text1, android.R.id.text2 }, 0);
    }

    @Override public Cursor swapCursor(Cursor c) {
      Cursor res = super.swapCursor(c);

      mIndex_id = c.getColumnIndex(RecentMediaStorage.Entry.COLUMN_NAME_ID);
      mIndex_url = c.getColumnIndex(RecentMediaStorage.Entry.COLUMN_NAME_URL);
      mIndex_name = c.getColumnIndex(RecentMediaStorage.Entry.COLUMN_NAME_NAME);

      return res;
    }

    @Override public long getItemId(int position) {
      final Cursor cursor = moveToPosition(position);
      if (cursor == null) return 0;

      return cursor.getLong(mIndex_id);
    }

    Cursor moveToPosition(int position) {
      final Cursor cursor = getCursor();
      if (cursor.getCount() == 0 || position >= cursor.getCount()) {
        return null;
      }
      cursor.moveToPosition(position);
      return cursor;
    }

    public String getUrl(int position) {
      final Cursor cursor = moveToPosition(position);
      if (cursor == null) return "";

      return cursor.getString(mIndex_url);
    }

    public String getName(int position) {
      final Cursor cursor = moveToPosition(position);
      if (cursor == null) return "";

      return cursor.getString(mIndex_name);
    }
  }
}
