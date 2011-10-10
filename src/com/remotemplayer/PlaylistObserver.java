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
		Log.i("PlaylistObserver", "Some event");
		if (event == FileObserver.MODIFY) {
			Log.i("PlaylistObserver", absolutePath + " modified");

			InputStream instream;
			try {
				instream = new FileInputStream(absolutePath);
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
							}
							playlist.add(line);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} while (flag);

					musicService.setPlaylist(playlist);
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// musicService.startMusic();
		}

		if (path == null) {
			return;
		}
		// a new file or subdirectory was created under the monitored directory
		if ((FileObserver.CREATE & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" path " is created\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is created");
		}
		// a file or directory was opened
		if ((FileObserver.OPEN & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = path " is opened\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is opened");
		}
		// data was read from a file
		if ((FileObserver.ACCESS & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" path " is accessed/read\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is accessed");
		}
		// data was written to a file
		if ((FileObserver.MODIFY & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" path " is modified\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is modified");
		}
		// someone has a file or directory open read-only, and closed it
		if ((FileObserver.CLOSE_NOWRITE & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = path " is closed\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is closed");
		}
		// someone has a file or directory open for writing, and closed it
		if ((FileObserver.CLOSE_WRITE & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" path " is written and closed\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is written and closed");
		}
		// [todo: consider combine this one with one below]
		// a file was deleted from the monitored directory
		if ((FileObserver.DELETE & event) != 0) {
			// for testing copy file
			// FileUtils.copyFile(absolutePath "/" path);
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" path " is deleted\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is deleted");
		}
		// the monitored file or directory was deleted, monitoring effectively stops
		if ((FileObserver.DELETE_SELF & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" " is deleted\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is deleted");
		}
		// a file or subdirectory was moved from the monitored directory
		if ((FileObserver.MOVED_FROM & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" path " is moved to somewhere " "\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is moved");
		}
		// a file or subdirectory was moved to the monitored directory
		if ((FileObserver.MOVED_TO & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = "File is moved to " absolutePath "/" path "\n";
			Log.i("PlaylistObserver", "File is moved to " + absolutePath + "/" + path);
		}
		// the monitored file or directory was moved; monitoring continues
		if ((FileObserver.MOVE_SELF & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = path " is moved\n";
			Log.i("PlaylistObserver", path + " is moved");
		}
		// Metadata (permissions, owner, timestamp) was changed explicitly
		if ((FileObserver.ATTRIB & event) != 0) {
			// FileAccessLogStatic.accessLogMsg = absolutePath "/" path " is changed (permissions, owner, timestamp)\n";
			Log.i("PlaylistObserver", absolutePath + "/" + path + " is changed (permissions etc)");
		}
	}

	public void setService(MusicService musicService) {
		this.musicService = musicService;

	}
}
