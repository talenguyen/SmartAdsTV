package com.anvy.samples.smartads.util;

import android.util.Log;

import com.anvy.samples.smartads.BuildConfig;

public class Logger {

	public static final void log(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg);
		}
	}
}
