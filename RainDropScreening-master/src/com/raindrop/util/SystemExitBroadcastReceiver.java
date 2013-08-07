package com.raindrop.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import android.app.Activity;
public class SystemExitBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.i("sjl", "¼àÌýµ½¹Ø±Õ²Ù×÷");
		((Activity)context).finish();
	}

}
