/**
 * 
 */
package com.mshuffle;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author Sapan
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

	private static final String ACTION_PLAY = "PLAY";
	private static String mUrl;

	private static MusicService mInstance = null;

	@Override
	public void onCreate() {
		mInstance = this;
	}

	public class LocalBinder extends Binder {
		MusicService getService() {
			return MusicService.this;
		}
	}

	// The Media Player
	MediaPlayer mMediaPlayer = null;

	private final IBinder mBinder = new LocalBinder();

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
	private int mBufferPosition;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction().equals(ACTION_PLAY)) {
			mMediaPlayer = new MediaPlayer(); // initialize it here
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
				@Override
				public void onBufferingUpdate(MediaPlayer mp, int progress) {
					setBufferPosition(progress);
				}
			});
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			initMediaPlayer();
		}
		return START_STICKY;
	}

	private void initMediaPlayer() {
		try {
			mMediaPlayer.setDataSource(mUrl);
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
		mState = State.Preparing;
	}

	public void restartMusic() {
		mState = State.Retrieving;
		mMediaPlayer.reset();
		initMediaPlayer();
	}

	protected void setBufferPosition(int progress) {
		mBufferPosition = progress;

	}

	/** Called when MediaPlayer is ready */
	@Override
	public void onPrepared(MediaPlayer player) {
		mState = State.Playing;
		mMediaPlayer.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
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
		mState = State.Retrieving;
	}

	public MediaPlayer getMediaPlayer() {
		return mMediaPlayer;
	}

	public void pauseMusic() {
		if (mState.equals(State.Playing)) {
			mMediaPlayer.pause();
			mState = State.Paused;
		}
	}

	public void startMusic() {
		if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
			mMediaPlayer.start();
			mState = State.Playing;
		}
	}

	public boolean isPlaying() {
		if (mState.equals(State.Playing)) {
			return true;
		}
		return false;
	}

	public int getMusicDuration() {
		if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
			return mMediaPlayer.getDuration();
		}
		return 0;
	}

	public int getCurrentPosition() {
		if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public int getBufferPercentage() {
		if (mState.equals(State.Preparing)) {
			return mBufferPosition;
		}
		return 0;
	}

	public void seekMusicTo(int pos) {
		if (mState.equals(State.Playing) || mState.equals(State.Paused)) {
			mMediaPlayer.seekTo(pos);
		}

	}

	public static MusicService getInstance() {
		return mInstance;
	}

	public static void setUrl(String url) {
		mUrl = url;
	}
}
