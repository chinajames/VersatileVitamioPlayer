package io.vov.vitamio.demo.floating;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.app.StringUtils;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.demo.R;
import java.lang.ref.WeakReference;

public class FloatPlayerController extends FrameLayout {
  private static final String TAG = "FloatPlayerController";
  private Context mContext;
  private LayoutInflater mInflater;
  private IMediaPlayer mPlayer;
  private MediaPlayer mMediaPlayer;

  private RelativeLayout mControllerRoot;
  private RelativeLayout mLoadingLayout;
  private RelativeLayout mFloatWindowBtnControllLayout;
  private ImageView mCloseFloatWindowBtn;
  private ImageView mPlayPauseBtn;
  private ImageView mFloatToFullScreenBtn;

  public static final int UPDATE_VIEW = 0x20003;
  private ShowSecond showSecond;
  private SeekBar play_progress;
  private TextView currentTime;
  private TextView durationTime;
  public RelativeLayout playControlMainLayout;
  private int intervalTime = 100;

  private boolean mIsScaling = false;
  private boolean mIsTouchUp = true;
  private boolean mIsMultiTouchMode = false;

  private float mDownY = 0;
  private float mDownX = 0;
  private float mTouchX = 0;
  private float mTouchY = 0;

  public FloatPlayerController(Context context, IMediaPlayer iPlayer, MediaPlayer iMediaPlayer) {
    super(context);
    mContext = context;
    mPlayer = iPlayer;
    mMediaPlayer = iMediaPlayer;
    mInflater = LayoutInflater.from(context);
    showSecond = new ShowSecond(this);

    setFocusable(true);
    setFocusableInTouchMode(true);
    setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
    requestFocus();
    setBackgroundColor(getResources().getColor(R.color.green));

    initFloatView();
  }

  public void updateSecond() {
    Log.w("hanjh", "updateSecond");
    if (showSecond != null) {
      showSecond.removeMessages(UPDATE_VIEW);
      showSecond.sendMessageDelayed(showSecond.obtainMessage(UPDATE_VIEW), 100);
    }
  }

  public void setmPlayer(MediaPlayer iMediaPlayer) {
    mMediaPlayer = iMediaPlayer;
  }

  private static class ShowSecond extends Handler {
    WeakReference<FloatPlayerController> playerUiControllerWeakReference;

    public ShowSecond(FloatPlayerController playerUiController) {
      playerUiControllerWeakReference = new WeakReference<>(playerUiController);
    }

    @Override public void handleMessage(Message msg) {
      FloatPlayerController playerUiController = this.playerUiControllerWeakReference.get();
      if (playerUiController == null || playerUiController.mContext == null || playerUiController.mMediaPlayer == null || !playerUiController.mMediaPlayer.isPlaying()) {
        return;
      }
      switch (msg.what) {
        case UPDATE_VIEW:
          playerUiController.currentTime.setText(StringUtils.stringForTime((int) playerUiController.mMediaPlayer.getCurrentPosition()));
          playerUiController.durationTime.setText(StringUtils.stringForTime((int) playerUiController.mMediaPlayer.getDuration()));
          if (playerUiController.mMediaPlayer.getDuration() / 200 > 100) {
            playerUiController.intervalTime = (int) playerUiController.mMediaPlayer.getDuration() / 200;
            if (playerUiController.intervalTime > 1000) {
              playerUiController.intervalTime = 1000;
            }
          }

          playerUiController.play_progress.setMax((int) playerUiController.mMediaPlayer.getDuration());
          playerUiController.play_progress.setProgress((int) playerUiController.mMediaPlayer.getCurrentPosition());
          playerUiController.showSecond.removeMessages(UPDATE_VIEW);
          if (null != playerUiController.playControlMainLayout
              && playerUiController.playControlMainLayout.getVisibility() == View.VISIBLE) {
            playerUiController.showSecond.sendMessageDelayed(playerUiController.showSecond.obtainMessage(UPDATE_VIEW),
                playerUiController.intervalTime);
          }
          break;
        default:
          break;
      }
    }
  }

  private void initFloatView() {
    mControllerRoot = (RelativeLayout) mInflater.inflate(R.layout.floatwindow_controller, null);
    mFloatWindowBtnControllLayout = (RelativeLayout) mControllerRoot.findViewById(R.id.float_window_btn_controll_layout);
    play_progress = (SeekBar) mControllerRoot.findViewById(R.id.play_progress);
    currentTime = (TextView) mControllerRoot.findViewById(R.id.currentTime);
    durationTime = (TextView) mControllerRoot.findViewById(R.id.durationTime);
    playControlMainLayout = (RelativeLayout) mControllerRoot.findViewById(R.id.playControlMainLayout);
    mLoadingLayout = (RelativeLayout) mControllerRoot.findViewById(R.id.float_loading_layout);
    mCloseFloatWindowBtn = (ImageView) mControllerRoot.findViewById(R.id.close_float_window);
    mCloseFloatWindowBtn.setOnClickListener(mOnClickListener);
    mPlayPauseBtn = (ImageView) mControllerRoot.findViewById(R.id.float_play_pause);
    mPlayPauseBtn.setOnClickListener(mOnClickListener);
    mFloatToFullScreenBtn = (ImageView) mControllerRoot.findViewById(R.id.float_to_fullscreen);
    mFloatToFullScreenBtn.setOnClickListener(mOnClickListener);
    FrameLayout.LayoutParams mFloatLayoutLP =
        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    mFloatLayoutLP.gravity = Gravity.BOTTOM;
    addView(mControllerRoot, mFloatLayoutLP);
    play_progress.setOnSeekBarChangeListener(new MySeekBarListener());
  }

  class MySeekBarListener implements SeekBar.OnSeekBarChangeListener {

    @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
      mMediaPlayer.seekTo(seekBar.getProgress());
      play_progress.setProgress(seekBar.getProgress());
      currentTime.setText(StringUtils.stringForTime(seekBar.getProgress()));
      durationTime.setText(StringUtils.stringForTime((int) mMediaPlayer.getDuration()));
      if (mMediaPlayer.getDuration() / 200 > 100) {
        intervalTime = (int) mMediaPlayer.getDuration() / 200;
        if (intervalTime > 1000) {
          intervalTime = 1000;
        }
      }
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (event.getPointerCount() < 2) {
      mTouchX = event.getRawX();
      mTouchY = event.getRawY();
      Log.i("Hanjh", "onTouch 0=down 2=move 1=up " + event.getAction());
      switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
          mIsMultiTouchMode = false;
          if (!mIsScaling && mIsTouchUp) {
            mDownX = event.getX();
            mDownY = event.getY();
          }
          break;
        case MotionEvent.ACTION_MOVE:
          if (!mIsScaling && mIsTouchUp && !mIsMultiTouchMode) {
            if (Math.abs(mTouchX - mDownX) > 10 || Math.abs(mTouchY - mDownY) > 10) {
              updateFloatWindow();
            }
          }
          break;

        case MotionEvent.ACTION_UP:
          mIsTouchUp = true;
          Log.d(TAG, "video onTouchEvent ACTION_UP: ");
          break;
        case MotionEvent.ACTION_POINTER_2_UP:
          Log.d(TAG, "video onTouchEvent ACTION_POINTER_2_UP: ");
          break;
        case MotionEvent.ACTION_POINTER_UP:
          Log.d(TAG, "video onTouchEvent ACTION_POINTER_UP: ");
      }
    } else {
      mIsMultiTouchMode = true;
    }
    return true;
  }

  private void updateFloatWindow() {
    Log.d(TAG, "video updateFloatWindow mTouchY:" + mTouchY + " mDownY: " + mDownY);
    Log.d(TAG, "video updateFloatWindow  wmParams.y:" + VideoPlayerService.mFloatPlayerUI.wmParams.y);
    if (!mIsScaling) {
      VideoPlayerService.mFloatPlayerUI.wmParams.x = (int) (mTouchX - mDownX);
      VideoPlayerService.mFloatPlayerUI.wmParams.y = (int) (mTouchY - mDownY);
      if (VideoPlayerService.mFloatPlayerUI.wmParams.y < 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_height)) {
        VideoPlayerService.mFloatPlayerUI.wmParams.y = 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_height);
      } else if (VideoPlayerService.mFloatPlayerUI.wmParams.y > getResources().getDisplayMetrics().heightPixels - 150) {
        VideoPlayerService.mFloatPlayerUI.wmParams.y = getResources().getDisplayMetrics().heightPixels - 150;
      }
      if (VideoPlayerService.mFloatPlayerUI.wmParams.x < 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_width)) {
        VideoPlayerService.mFloatPlayerUI.wmParams.x = 150 - getResources().getDimensionPixelSize(R.dimen.float_window_root_width);
      } else if (VideoPlayerService.mFloatPlayerUI.wmParams.x > getResources().getDisplayMetrics().widthPixels - 150) {
        VideoPlayerService.mFloatPlayerUI.wmParams.x = getResources().getDisplayMetrics().widthPixels - 150;
      }
    }
    VideoPlayerService.mFloatPlayerUI.wmParams.gravity = Gravity.LEFT | Gravity.TOP;
    VideoPlayerService.mFloatPlayerUI.mWindowManager.updateViewLayout(VideoPlayerService.mFloatPlayerUI.mlayoutView,
        VideoPlayerService.mFloatPlayerUI.wmParams);
  }

  private View.OnClickListener mOnClickListener = new View.OnClickListener() {

    @Override public void onClick(View view) {
      int id = view.getId();
      switch (id) {
        case R.id.close_float_window:
          closeFloatWindowClick();
          break;
        case R.id.float_play_pause:
          playPauseClick();
          break;
        case R.id.float_to_fullscreen:
          switchToFullScreen();
          break;
        default:

          break;
      }
    }
  };

  private void closeFloatWindowClick() {
    showSecond.removeMessages(UPDATE_VIEW);
    if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
      mMediaPlayer.stop();
    }
    mMediaPlayer = null;
    mPlayer.closePlayer();
  }

  private void playPauseClick() {
    if (mPlayer.isPlaying()) {
      mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_play));
    } else {
      mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_pause));
    }
    mPlayer.playPause();
  }

  private void switchToFullScreen() {

  }

  public void onAllComplete() {
    mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_play));
    mFloatWindowBtnControllLayout.setVisibility(View.VISIBLE);
  }

  public void onBeginPlay() {
    setPlayPauseButtonVisibility(true);
    setBackgroundColor(0x00000000);
    finishLoading();
  }

  public void showPause() {
    mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_play));
    mFloatWindowBtnControllLayout.setVisibility(View.VISIBLE);
  }

  public void showEnd() {
    mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_play));
    mFloatWindowBtnControllLayout.setVisibility(View.VISIBLE);
    play_progress.setProgress(1);
    mMediaPlayer.seekTo(1);
  }

  public void showPlaying() {
    mPlayPauseBtn.setImageDrawable(getResources().getDrawable(R.drawable.video_btn_float_pause));
  }

  public void showLoading() {
    setPlayPauseButtonVisibility(false);
    showPlaying();
    mLoadingLayout.setVisibility(View.VISIBLE);
  }

  public void setPlayPauseButtonVisibility(boolean visible) {
    mPlayPauseBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
  }

  public void updatePlayPauseIcon() {
    if (mPlayer.isPlaying()) {
      showPlaying();
    } else {
      showPause();
    }
  }

  public void finishLoading() {
    mLoadingLayout.setVisibility(View.GONE);
  }

  @Override protected void onDetachedFromWindow() {
    showSecond.removeMessages(UPDATE_VIEW);
    if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
      mMediaPlayer.stop();
    }
    mMediaPlayer = null;
    super.onDetachedFromWindow();
  }
}
