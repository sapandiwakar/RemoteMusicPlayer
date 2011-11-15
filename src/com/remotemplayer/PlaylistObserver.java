/**
 * 
 */
package com.remotemplayer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.os.FileObserver;
import android.util.Log;

/**
 * @author Sapan
 */
public class PlaylistObserver extends FileObserver {
	public String absolutePath;
	private MusicService musicService;

	public PlaylistObserver(String path) {
		super(path, FileObserver.ALL_EVENTS);
		absolutePath = path;
		Log.i("PlaylistObserver", "Created");
	}

	@Override
	public void onEvent(int event, String path) {
		if (event == FileObserver.ACCESS) {
			Log.i("PlaylistObserver", "Access");
		} else if (event == FileObserver.ATTRIB) {
			Log.i("PlaylistObserver", "Attrib");
		} else if (event == FileObserver.CLOSE_NOWRITE) {
			Log.i("PlaylistObserver", "Closed without writing");
		} else if (event == FileObserver.CLOSE_WRITE) {
			Log.i("PlaylistObserver", "Closed after writing");
		} else if (event == FileObserver.CREATE) {
			Log.i("PlaylistObserver", "File Created");
		} else if (event == FileObserver.DELETE) {
			Log.i("PlaylistObserver", "Deleted");
		} else if (event == FileObserver.DELETE_SELF) {
			Log.i("PlaylistObserver", "Delete self");

		} else if (event == FileObserver.MODIFY) {
			Log.i("PlaylistObserver", "Modify");
		} else if (event == FileObserver.MOVE_SELF) {
			Log.i("PlaylistObserver", "move self");
		} else if (event == FileObserver.MOVED_FROM) {
			Log.i("PlaylistObserver", "moved from ");
		} else if (event == FileObserver.MOVED_TO) {
			Log.i("PlaylistObserver", "moved to ");
		} else if (event == FileObserver.OPEN) {
			Log.i("PlaylistObserver", "opened");
		}
		if (event == FileObserver.CREATE || event == FileObserver.CLOSE_WRITE) {
			Log.i("PlaylistObserver", absolutePath + " modified");
			// Check for txt files
			// Sugarsync creates temp files in directory
			if (path.endsWith(".txt")) {
				readPlaylist(path);
			}

			// musicService.startMusic();
		}
	}

	public void readPlaylist(String path) {
		InputStream instream;
		try {
			instream = new FileInputStream(absolutePath + path);

			Log.i("PlaylistObserver", "Trying to read playlist at " + absolutePath + path);
			// if file the available for reading
			if (instream != null) {
				// prepare the file for reading
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);

				String line;

				// read every line of the file into the line-variable, on line at the time
				List<String> playlist = new ArrayList<String>();
				boolean flag = true;
				do {
					try {
						line = buffreader.readLine();
						if (line == null) {
							flag = false;
						} else if (line.length() == 0) {
							continue;
						}
						Log.i("PlaylistObserver", line + " added to playlist");
						playlist.add(line);
					} catch (IOException e) {
						Log.e("PlaylistObserver", "Unable to read playlist", e);
						// e.printStackTrace();
					}

				} while (flag);

				Log.i("PlaylistObserver", "Now trying to play from playlist");
				musicService.setPlaylist(playlist);
			} else {
				Log.e("PlaylistObserver", "Playlist file not found");
			}
		} catch (FileNotFoundException e1) {
			Log.e("PlaylistObserver", "Unable to read playlist", e1);
		}
	}

	public void setService(MusicService musicService) {
		this.musicService = musicService;
	}
}
