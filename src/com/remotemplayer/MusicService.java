/**
 * 
 */
package com.remotemplayer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Sapan
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener,
		OnCompletionListener {

	private static final String ACTION_PLAY = "PLAY";
	private static final String PLAYLIST_PATH = "/sdcard/My SugarSync Folders/Uploaded by Email/playlist.txt";
	private static final String MUSIC_PATH = "/sdcard/music/";

	private static String mUrl;
	// NotificationManager mNotificationManager;
	// Notification mNotification = null;

	// The ID we use for the notification (the onscreen alert that appears at the notification
	// area at the top of the screen as an icon -- and as text as well if the user expands the
	// notification area).
	final int NOTIFICATION_ID = 1;
	private static PlaylistObserver playlistObserver;

	private static MusicService mInstance = null;

	@Override
	public void onCreate() {
		mInstance = this;

		// mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

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
	private List<String> playlist;
	private int currentSongIndex;
	private static String mSongTitle;
	private static String mSongPicUrl;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getAction().equals(ACTION_PLAY)) {
			mMediaPlayer = new MediaPlayer(); // initialize it here
			mMediaPlayer.setOnPreparedListener(this);
			mMediaPlayer.setOnCompletionListener(this);
			mMediaPlayer.setOnErrorListener(this);
			mMediaPlayer.setOnBufferingUpdateListener(this);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// initMediaPlayer();
		}
		playlistObserver = new PlaylistObserver(PLAYLIST_PATH);
		playlistObserver.setService(this);
		playlistObserver.startWatching();
		Log.i("MusicService", "STarted watching for file events");
		return START_STICKY;
	}

	private void initMediaPlayer() {
		try {
			mMediaPlayer.setDataSource(mUrl);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Workaround for bug: http://code.google.com/p/android/issues/detail?id=957
			mMediaPlayer.reset();
			try {
				mMediaPlayer.setDataSource(mUrl);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			mMediaPlayer.prepareAsync(); // prepare async to not block main thread
		} catch (IllegalStateException e) {
			// TODO Workaround for bug: http://code.google.com/p/android/issues/detail?id=957
			mMediaPlayer.reset();
			try {
				mMediaPlayer.setDataSource(mUrl);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			mMediaPlayer.prepareAsync();
		}
		mState = State.Preparing;
		setUpAsForeground(mSongTitle + " (loading)");
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
		setUpAsForeground(mSongTitle + " (playing)");
	}

	/** Updates the notification. */
	void updateNotification(String text) {
		PendingIntent pi =
				PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MusicActivity.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
		// mNotification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), text,
		// pi);
		// mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	/**
	 * Configures service as a foreground service. A foreground service is a service that's doing something the user is
	 * actively aware of (such as playing music), and must appear to the user as a notification. That's why we create
	 * the notification here.
	 */
	void setUpAsForeground(String text) {
		PendingIntent pi =
				PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MusicActivity.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
		// mNotification = new Notification();
		// mNotification.tickerText = text;
		// mNotification.icon = R.drawable.ic_mshuffle_icon;
		// mNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		// mNotification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name), text,
		// pi);
		// startForeground(NOTIFICATION_ID, mNotification);
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
			updateNotification(mSongTitle + " (paused)");
		}
	}

	public void startMusic() {
		if (!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)) {
			mMediaPlayer.start();
			mState = State.Playing;
			updateNotification(mSongTitle + " (playing)");
		}
	}

	public boolean isPlaying() {
		if (mState.equals(State.Playing)) {
			return true;
		}
		return false;
	}

	public void setPlaylist(List<String> playlist) {
		this.playlist = playlist;
		startPlayingFromPlayList();
	}

	private void startPlayingFromPlayList() {
		if (playlist.size() == 0) {
			return;
		}
		currentSongIndex = 0;
		playSong(currentSongIndex);

	}

	private void playSong(int currentSongIndex) {
		if (currentSongIndex < playlist.size()) {
			mUrl = this.MUSIC_PATH + playlist.get(currentSongIndex);
			File file = new File(mUrl);
			if (file.exists()) {
				initMediaPlayer();
			} else {
				currentSongIndex++;
				playSong(currentSongIndex);
			}
		}
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
		// if (mState.equals(State.Preparing)) {
		return mBufferPosition;
		// }
		// return getMusicDuration();
	}

	public void seekMusicTo(int pos) {
		if (mState.equals(State.Playing) || mState.equals(State.Paused)) {
			mMediaPlayer.seekTo(pos);
		}

	}

	public static MusicService getInstance() {
		return mInstance;
	}

	public static void setSong(String url, String title, String songPicUrl) {
		mUrl = url;
		mSongTitle = title;
		mSongPicUrl = songPicUrl;
	}

	public String getSongTitle() {
		return mSongTitle;
	}

	public String getSongPicUrl() {
		return mSongPicUrl;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		setBufferPosition(percent * getMusicDuration() / 100);
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		currentSongIndex++;
		playSong(currentSongIndex);
	}
}
