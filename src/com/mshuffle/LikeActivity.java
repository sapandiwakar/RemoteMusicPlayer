/**
 * 
 */
package com.mshuffle;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Sapan
 */
public class LikeActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(this);
		textView.setText("The songs tab");
		setContentView(textView);
	}
}
