package com.mshuffle;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class MusicShuffleActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, MoodsActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("moods").setIndicator("Moods", res.getDrawable(R.drawable.ic_tab_shuffle)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, ShuffleActivity.class);
		spec = tabHost.newTabSpec("music").setIndicator("Music", res.getDrawable(R.drawable.ic_tab_shuffle)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, LikeActivity.class);
		spec = tabHost.newTabSpec("like").setIndicator("Like", res.getDrawable(R.drawable.ic_tab_shuffle)).setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(2);

	}
}
