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
public class MoodsActivity extends Activity {

	WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources res = getResources();
		/*
		 * TextView textView = new TextView(this); textView.setText("The moods tab"); setContentView(textView);
		 */

		setContentView(R.layout.moods);

		mWebView = (WebView) findViewById(R.id.webview);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(res.getString(R.string.host) + "forms/popups/moods.popup.forms.php");

	}
}
