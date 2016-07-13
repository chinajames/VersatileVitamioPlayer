/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vov.vitamio.demo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnCompletionListener;
import io.vov.vitamio.MediaPlayer.OnPreparedListener;
import io.vov.vitamio.MediaPlayer.OnVideoSizeChangedListener;
import tv.danmaku.ijk.media.example.content.RecentMediaStorage;

public class MediaPlayerDemo_Video extends Activity implements OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {

	private static final String TAG = "MediaPlayerDemo";
	private int mVideoWidth;
	private int mVideoHeight;
	private MediaPlayer mMediaPlayer;
	private SurfaceView mPreview;
	private SurfaceHolder holder;
	private static final String MEDIA = "media";
	public static final int LOCAL_AUDIO = 1;
	public static final int STREAM_AUDIO = 2;
	public static final int RESOURCES_AUDIO = 3;
	public static final int LOCAL_VIDEO = 4;
	public static final int STREAM_VIDEO = 5;
	private boolean mIsVideoSizeKnown = false;
	private boolean mIsVideoReadyToBePlayed = false;

	private String mVideoPath;
	private Uri mVideoUri;

	/**
	 * 
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		if (!LibsChecker.checkVitamioLibs(this))
			return;
		setContentView(R.layout.mediaplayer_2);
		mPreview = (SurfaceView) findViewById(R.id.surface);
		holder = mPreview.getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.RGBA_8888); 

	}

	public static Intent newIntent(Context context, String videoPath, String videoTitle,int mediaType) {
		Intent intent = new Intent(context, MediaPlayerDemo_Video.class);
		intent.putExtra("videoPath", videoPath);
		intent.putExtra("videoTitle", videoTitle);
		intent.putExtra(MEDIA, mediaType);
		return intent;
	}

	public static void intentTo(Context context, String videoPath, String videoTitle,int mediaType) {
		context.startActivity(newIntent(context, videoPath, videoTitle ,mediaType));
	}

	private void playVideo(Integer Media) {
		doCleanUp();
		try {

			switch (Media) {
			case LOCAL_VIDEO:
				/*
				 * TODO: Set the path variable to a local media file path.
				 */
				mVideoPath = getIntent().getStringExtra("videoPath");
				if (TextUtils.isEmpty(mVideoPath)) {
					// Tell the user to provide a media file URL.
					Toast.makeText(MediaPlayerDemo_Video.this, "Please edit MediaPlayerDemo_Video Activity, "
							+ "and set the path variable to your media file path."
							+ " Your media file must be stored on sdcard.", Toast.LENGTH_LONG).show();
					return;
				}

				// handle arguments
				mVideoPath = getIntent().getStringExtra("videoPath");

				Intent intent = getIntent();
				String intentAction = intent.getAction();
				if (!TextUtils.isEmpty(intentAction)) {
					if (intentAction.equals(Intent.ACTION_VIEW)) {
						mVideoPath = intent.getDataString();
					} else if (intentAction.equals(Intent.ACTION_SEND)) {
						mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
						if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							String scheme = mVideoUri.getScheme();
							if (TextUtils.isEmpty(scheme)) {
								Log.e(TAG, "Null unknown ccheme\n");
								finish();
								return;
							}
							if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
								mVideoPath = mVideoUri.getPath();
							} else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
								Log.e(TAG, "Can not resolve content below Android-ICS\n");
								finish();
								return;
							} else {
								Log.e(TAG, "Unknown scheme " + scheme + "\n");
								finish();
								return;
							}
						}
					}
				}

				if (!TextUtils.isEmpty(mVideoPath)) {
					new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
				}

				// Create a new media player and set the listeners
				mMediaPlayer = new MediaPlayer(this);
				Log.w("hanjh","mVideoPath: "+mVideoPath);//"/storage/emulated/0/JQuery实战视频教程[王兴魁]/02.[jQuery]第1章 jQuery入门[下].avi"
				mMediaPlayer.setDataSource(mVideoPath);
				mMediaPlayer.setDisplay(holder);
				mMediaPlayer.prepareAsync();
				mMediaPlayer.setOnBufferingUpdateListener(this);
				mMediaPlayer.setOnCompletionListener(this);
				mMediaPlayer.setOnPreparedListener(this);
				mMediaPlayer.setOnVideoSizeChangedListener(this);
				setVolumeControlStream(AudioManager.STREAM_MUSIC);


				break;
			case STREAM_VIDEO:
				/*
				 * TODO: Set path variable to progressive streamable mp4 or
				 * 3gpp format URL. Http protocol should be used.
				 * Mediaplayer can only play "progressive streamable
				 * contents" which basically means: 1. the movie atom has to
				 * precede all the media data atoms. 2. The clip has to be
				 * reasonably interleaved.
				 * 
				 */
				mVideoPath = "http://video19.ifeng.com/video06/2012/04/11/629da9ec-60d4-4814-a940-997e6487804a.mp4";
				if (mVideoPath == "") {
					// Tell the user to provide a media file URL.
					Toast.makeText(MediaPlayerDemo_Video.this, "Please edit MediaPlayerDemo_Video Activity," + " and set the path variable to your media file URL.", Toast.LENGTH_LONG).show();
					return;
				}

				// Create a new media player and set the listeners
				mMediaPlayer = new MediaPlayer(this);
				mMediaPlayer.setDataSource(mVideoPath);
				mMediaPlayer.setDisplay(holder);
				mMediaPlayer.prepareAsync();
				mMediaPlayer.setOnBufferingUpdateListener(this);
				mMediaPlayer.setOnCompletionListener(this);
				mMediaPlayer.setOnPreparedListener(this);
				mMediaPlayer.setOnVideoSizeChangedListener(this);
				setVolumeControlStream(AudioManager.STREAM_MUSIC);

				break;

			}



		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
		}
	}

	public void onBufferingUpdate(MediaPlayer arg0, int percent) {
		// Log.d(TAG, "onBufferingUpdate percent:" + percent);

	}

	public void onCompletion(MediaPlayer arg0) {
		Log.d(TAG, "onCompletion called");
	}

	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		Log.v(TAG, "onVideoSizeChanged called");
		if (width == 0 || height == 0) {
			Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
			return;
		}
		mIsVideoSizeKnown = true;
		mVideoWidth = width;
		mVideoHeight = height;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	public void onPrepared(MediaPlayer mediaplayer) {
		Log.d(TAG, "onPrepared called");
		mIsVideoReadyToBePlayed = true;
		if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
			startVideoPlayback();
		}
	}

	public void surfaceChanged(SurfaceHolder surfaceholder, int i, int j, int k) {
		Log.d(TAG, "surfaceChanged called");

	}

	public void surfaceDestroyed(SurfaceHolder surfaceholder) {
		Log.d(TAG, "surfaceDestroyed called");
	}

	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated called");
		playVideo(getIntent().getIntExtra(MEDIA,STREAM_VIDEO));

	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaPlayer();
		doCleanUp();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseMediaPlayer();
		doCleanUp();
	}

	private void releaseMediaPlayer() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void doCleanUp() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		mIsVideoReadyToBePlayed = false;
		mIsVideoSizeKnown = false;
	}

	private void startVideoPlayback() {
		Log.v(TAG, "startVideoPlayback");
		holder.setFixedSize(mVideoWidth, mVideoHeight);
		mMediaPlayer.start();
	}
}
