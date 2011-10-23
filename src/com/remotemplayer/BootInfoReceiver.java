package com.remotemplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootInfoReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent("PLAY"));
	}
}
