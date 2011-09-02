/**
 * 
 */
package com.mshuffle.activity;

import com.mshuffle.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * @author Sapan
 */
public class RecentActivityActivity extends Activity {

	WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * TextView textView = new TextView(this); textView.setText("The activity tab"); setContentView(textView);
		 */

		setContentView(R.layout.recentactivity);

		mWebView = (WebView) findViewById(R.id.activitywebview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl("http://shoutz.in/nf?ajax=1");

	}
}
