package com.mshuffle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

/**
 * @author Sapan
 */
public class MusicActivity extends Activity implements MediaPlayerControl {

	MediaController ctrl;
	ImageView imageView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources res = getResources(); // Resource object to get Drawables

		imageView = new ImageView(this);
		imageView.setImageDrawable(res.getDrawable(R.drawable.test));
		setContentView(imageView);

		ctrl = new MediaController(this);
		ctrl.setMediaPlayer(this);
		ctrl.setAnchorView(imageView);

		new Thread(new Runnable() {

			@Override
			public void run() {
				startService(new Intent("PLAY"));
			}
		}).start();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// the MediaController will hide after 3 seconds - tap the screen to make it appear again
		ctrl.show();
		return false;
	}

	@Override
	public boolean canPause() {
		return true;
	}

	@Override
	public boolean canSeekBackward() {
		return true;
	}

	@Override
	public boolean canSeekForward() {
		return true;
	}

	@Override
	public int getBufferPercentage() {
		if (MusicService.getInstance() != null) {
			return MusicService.getInstance().getBufferPercentage();
		}
		return 0;
	}

	@Override
	public int getCurrentPosition() {
		if (MusicService.getInstance() != null) {
			return MusicService.getInstance().getCurrentPosition();
		}
		return 0;
	}

	@Override
	public int getDuration() {
		if (MusicService.getInstance() != null) {
			return MusicService.getInstance().getMusicDuration();
		}
		return 0;
	}

	@Override
	public boolean isPlaying() {
		if (MusicService.getInstance() != null) {
			return MusicService.getInstance().isPlaying();
		}
		return false;
	}

	@Override
	public void pause() {
		if (MusicService.getInstance() != null) {
			MusicService.getInstance().pauseMusic();
		}

	}

	@Override
	public void seekTo(int pos) {
		if (MusicService.getInstance() != null) {
			MusicService.getInstance().seekMusicTo(pos);
		}

	}

	@Override
	public void start() {
		if (MusicService.getInstance() != null) {
			MusicService.getInstance().startMusic();
		}

	}
}
