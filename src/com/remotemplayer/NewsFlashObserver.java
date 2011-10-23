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
public class NewsFlashObserver extends FileObserver {
	public String absolutePath;
	private MusicService musicService;

	public NewsFlashObserver(String path) {
		super(path, FileObserver.ALL_EVENTS);
		absolutePath = path;
		Log.i("NewsFlashObserver", "Created");
	}

	@Override
	public void onEvent(int event, String path) {
		if (event == FileObserver.ACCESS) {
			// Log.i("NewsFlashObserver", "Access");
		} else if (event == FileObserver.ATTRIB) {
			Log.i("NewsFlashObserver", "Attrib");
		} else if (event == FileObserver.CLOSE_NOWRITE) {
			Log.i("NewsFlashObserver", "Closed without writing");
		} else if (event == FileObserver.CLOSE_WRITE) {
			Log.i("NewsFlashObserver", "Closed after writing");
		} else if (event == FileObserver.CREATE) {
			Log.i("NewsFlashObserver", "File Created");
		} else if (event == FileObserver.DELETE) {
			Log.i("NewsFlashObserver", "Deleted");
		} else if (event == FileObserver.DELETE_SELF) {
			Log.i("NewsFlashObserver", "Delete self");

		} else if (event == FileObserver.MODIFY) {
			Log.i("NewsFlashObserver", "Modify");
		} else if (event == FileObserver.MOVE_SELF) {
			Log.i("NewsFlashObserver", "move self");
		} else if (event == FileObserver.MOVED_FROM) {
			Log.i("NewsFlashObserver", "moved from ");
		} else if (event == FileObserver.MOVED_TO) {
			Log.i("NewsFlashObserver", "moved to ");
		} else if (event == FileObserver.OPEN) {
			Log.i("NewsFlashObserver", "opened");
		}
		if (event == FileObserver.CREATE || event == FileObserver.CLOSE_WRITE) {
			Log.i("NewsFlashObserver", absolutePath + " modified/created");
			musicService.setNextNewsFlash(path);
			// musicService.startMusic();
		}
	}

	public void readNewsFlash(String path, boolean startPaused) {
		InputStream instream;
		try {
			instream = new FileInputStream(absolutePath + path);
			// if file the available for reading
			if (instream != null) {
				// prepare the file for reading
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);

				String line;

				// read every line of the file into the line-variable, on line at the time
				List<String> NewsFlash = new ArrayList<String>();
				boolean flag = true;
				do {
					try {
						line = buffreader.readLine();
						if (line == null) {
							flag = false;
						} else if (line.length() == 0) {
							continue;
						}
						Log.i("NewsFlashObserver", line + " added to NewsFlash");
						NewsFlash.add(line);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				} while (flag);

				// musicService.setNewsFlash(NewsFlash, startPaused);
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void setService(MusicService musicService) {
		this.musicService = musicService;
	}
}
