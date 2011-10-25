package com.remotemplayer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.SeekBar;

/**
 * @author Sapan
 */
public class MusicActivity extends Activity implements MediaPlayerControl, SeekBar.OnSeekBarChangeListener {

	// MediaController ctrl;
	// ImageView songPicture;
	String mUrl;
	Resources res;
	// TextView songNameView;
	// TextView musicCurLoc;
	// TextView musicDuration;
	// SeekBar musicSeekBar;
	// ToggleButton playPauseButton;
	// ImageButton shuffleButton;

	String mSongPicUrl = null;
	private String mSongTitle = null;
	protected boolean musicThreadFinished = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music);

		// shuffleButton = (ImageButton) findViewById(R.id.shufflebutton);

		res = getResources(); // Resource object to get Drawables

		// songPicture = (ImageView) findViewById(R.id.thumbnail); // new ImageView(this);
		// songNameView = (TextView) findViewById(R.id.songName);
		//
		// ctrl = new MediaController(this);
		// ctrl.setMediaPlayer(this);
		// ctrl.setAnchorView(songPicture);

		// musicCurLoc = (TextView) findViewById(R.id.musicCurrentLoc);
		// musicDuration = (TextView) findViewById(R.id.musicDuration);
		// musicSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);
		// playPauseButton = (ToggleButton) findViewById(R.id.playPauseButton);
		// musicSeekBar.setOnSeekBarChangeListener(this);

		Button confirmButton = (Button) findViewById(R.id.confirmButton);

		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (MusicService.getInstance() == null) {
					return;
				}
				EditText musicPath = (EditText) findViewById(R.id.MuiscPathEditText);
				EditText playlistPath = (EditText) findViewById(R.id.playlistPathEditText);
				if (musicPath.getText() != null && musicPath.getText().length() > 0) {
					Log.i("MusicActivity", "Changed the music path to " + musicPath.getText().toString());
					MusicService.getInstance().setMusicPath(musicPath.getText().toString());
				}

				if (playlistPath.getText() != null && playlistPath.getText().length() > 0) {
					Log.i("MusicActivity", "Changed the playlist path to " + playlistPath.getText().toString());
					MusicService.getInstance().setPlaylistPath(playlistPath.getText().toString());
				}

			}
		});

		// playPauseButton.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // Perform action on clicks
		// if (playPauseButton.isChecked()) { // Checked -> Pause icon visible
		// start();
		// } else { // Unchecked -> Play icon visible
		// pause();
		// }
		// }
		// });
		//
		// shuffleButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// shuffleNext();
		// }
		// });

		// WebView upNextSong = (WebView) findViewById(R.id.up_next_song);
		// upNextSong.getSettings().setJavaScriptEnabled(true);
		// upNextSong.loadUrl(res.getString(R.string.host) + "services/suggestion.php?v=2.0&a=suggestion.upnext");

		new Thread(new Runnable() {
			@Override
			public void run() {
				int currentPosition = 0;
				while (!musicThreadFinished) {
					try {
						Thread.sleep(1000);
						currentPosition = getCurrentPosition();
					} catch (InterruptedException e) {
						return;
					} catch (Exception e) {
						return;
					}
					final int total = getDuration();

					final String totalTime = getAsTime(total);
					final String curTime = getAsTime(currentPosition);

					// musicSeekBar.setMax(total);
					// musicSeekBar.setProgress(currentPosition);
					// musicSeekBar.setSecondaryProgress(getBufferPercentage());
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// if (isPlaying()) {
							// if (!playPauseButton.isChecked()) {
							// playPauseButton.setChecked(true);
							// }
							// } else {
							// if (playPauseButton.isChecked()) {
							// playPauseButton.setChecked(false);
							// }
							// }
							// musicDuration.setText(totalTime);
							// musicCurLoc.setText(curTime);
						}
					});

				}
			}
		}).start();

		if (MusicService.getInstance() != null) {
			// Service already running
			try {
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(MusicService.getInstance().getSongPicUrl()).getContent());
				// songPicture.setImageBitmap(bitmap);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// songNameView.setText(MusicService.getInstance().getSongTitle());

			return;
		} else {
			// songPicture.setImageDrawable(res.getDrawable(R.drawable.test));
			// songNameView.setText("Music Shuffle");
		}

		mUrl = "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";

		// MusicService.setSong(mUrl, "Temp Song", null);

		if (MusicService.getInstance() == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					startService(new Intent("PLAY"));
				}
			}).start();
		}

	}

	@SuppressWarnings("boxing")
	protected String getAsTime(int t) {
		return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toSeconds(t) / 60, TimeUnit.MILLISECONDS.toSeconds(t) - TimeUnit.MILLISECONDS.toSeconds(t) / 60
				* 60);
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
				// MusicService.setSong(musicDetails.getString("link"), mSongTitle, mSongPicUrl);
				Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(mSongPicUrl).getContent());
				// songPicture.setImageBitmap(bitmap);
				MusicService.getInstance().restartMusic();
				// songNameView.setText(mSongTitle);
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

	// @Override
	// public boolean onTouchEvent(MotionEvent event) {
	// // the MediaController will hide after 3 seconds - tap the screen to make it appear again
	// ctrl.show();
	// return false;
	// }

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
			// return MusicService.getInstance().getMusicDuration();
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			seekTo(progress);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}
}
