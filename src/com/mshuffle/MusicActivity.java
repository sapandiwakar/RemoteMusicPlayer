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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

/**
 * @author Sapan
 */
public class MusicActivity extends Activity implements MediaPlayerControl {

	MediaController ctrl;
	ImageView imageView;
	String mUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music);
		final Resources res = getResources(); // Resource object to get Drawables

		imageView = (ImageView) findViewById(R.id.thumbnail); // new ImageView(this);
		imageView.setImageDrawable(res.getDrawable(R.drawable.test));

		final Button button = (Button) findViewById(R.id.shufflebutton);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String shuffleUrl = R.string.host + "services/music.php?v=2.0&a=shuffle.next";
				// Log.i("MusicActivity", NetworkUtils.getInputStreamFromUrl(shuffleUrl).toString());
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
						MusicService.setUrl(musicDetails.getString("link"));
						Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(musicDetails.getString("pic_url")).getContent());
						imageView.setImageBitmap(bitmap);
						MusicService.getInstance().restartMusic();
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
		});

		ctrl = new MediaController(this);
		ctrl.setMediaPlayer(this);
		ctrl.setAnchorView(imageView);

		mUrl = "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";

		MusicService.setUrl(mUrl);

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
