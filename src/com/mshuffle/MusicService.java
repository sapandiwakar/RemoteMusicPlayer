/**
 * 
 */
package com.mshuffle;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * @author Sapan
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

	private static final String ACTION_PLAY = "PLAY";
	private static String url = "http://www.vorbis.com/music/Epoq-Lepidoptera.ogg";

	// The Media Player
	MediaPlayer mMediaPlayer = null;

	// indicates the state our service:
	enum State {
		Retrieving, // the MediaRetriever is retrieving music
		Stopped, // media player is stopped and not prepared to play
		Preparing, // media player is preparing...
		Playing, // playback active (media player ready!). (but the media player may actually be
					// paused in this state if we don't have audio focus. But we stay in this state
					// so that we know we have to resume playback once we get focus back)
		Paused
		// playback paused (media player ready!)
	};

	State mState = State.Retrieving;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (intent.getAction().equals(ACTION_PLAY)) {
			mMediaPlayer = new MediaPlayer(); // initialize it here
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			try {
				mMediaPlayer.setDataSource(url);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			mMediaPlayer.prepareAsync(); // prepare async to not block main thread
		}
		return 0;
	}

	/** Called when MediaPlayer is ready */
	@Override
	public void onPrepared(MediaPlayer player) {
		player.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onDestroy() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
		}
	}

}
