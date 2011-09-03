package com.mshuffle;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.TextView;

/**
 * @author Sapan
 */
public class MusicActivity extends Activity implements MediaPlayerControl {

	MediaController ctrl;
	ImageView songPicture;
	String mUrl;
	Resources res;
	TextView songNameView;

	String mSongPicUrl = null;
	private String mSongTitle = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music);

		final ImageButton button = (ImageButton) findViewById(R.id.shufflebutton);

		res = getResources(); // Resource object to get Drawables

		songPicture = (ImageView) findViewById(R.id.thumbnail); // new ImageView(this);
		songNameView = (TextView) findViewById(R.id.songName);

		ctrl = new MediaController(this);
		ctrl.setMediaPlayer(this);
		ctrl.setAnchorView(songPicture);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shuffleNext();
			}
		});

		if (MusicService.getInstance() != null) {
			// Service already running
			try {
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(MusicService.getInstance().getSongPicUrl()).getContent());
				songPicture.setImageBitmap(bitmap);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			songNameView.setText(MusicService.getInstance().getSongTitle());

			return;
		} else {
			songPicture.setImageDrawable(res.getDrawable(R.drawable.test));
			songNameView.setText("Music Shuffle");
		}

		mUrl = "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";

		MusicService.setSong(mUrl, "Temp Song", null);

		new Thread(new Runnable() {

			@Override
			public void run() {
				startService(new Intent("PLAY"));
			}
		}).start();

	}

	private void shuffleNext() {
		String shuffleUrl = R.string.host + "services/music.php?v=2.0&a=shuffle.next";
		String musicJson = res.getString(R.string.json);
		Log.i("MusicActivity", musicJson);
		JSONObject musicDetails = null;
		try {
			musicDetails = new JSONObject(musicJson);
		} catch (JSONException e) {
			Log.e("MusicActivity", "Error parsing JSON");
			// e.printStackTrace();
		}
		try {
			if (MusicService.getInstance() != null && musicDetails != null) {
				mSongTitle = musicDetails.getString("name");
				mSongPicUrl = musicDetails.getString("pic_url");
				MusicService.setSong(musicDetails.getString("link"), mSongTitle, mSongPicUrl);
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(mSongPicUrl).getContent());
				songPicture.setImageBitmap(bitmap);
				MusicService.getInstance().restartMusic();
				songNameView.setText(mSongTitle);
			}
		} catch (JSONException e) {
			Log.e("MusicActivity", "Link not found");
			e.printStackTrace();
		} catch (MalformedURLException e) {
			Log.e("MusicActivity", "Malformed pic url");
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("MusicActivity", "Cant read picture at pic_url");
			e.printStackTrace();
		}
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
