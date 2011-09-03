/**
 * 
 */
package com.mshuffle;

import android.app.Activity;
import android.content.res.Resources;
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
		Resources res = getResources();
		/*
		 * TextView textView = new TextView(this); textView.setText("The activity tab"); setContentView(textView);
		 */

		setContentView(R.layout.recentactivity);

		mWebView = (WebView) findViewById(R.id.activitywebview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(res.getString(R.string.host) + "nf?ajax=1");

	}
}
