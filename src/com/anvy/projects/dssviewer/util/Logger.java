package com.anvy.projects.dssviewer.util;

import android.util.Log;

import com.anvy.projects.dssviewer.BuildConfig;

public class Logger {

	public static final void log(String tag, String msg) {
		if (BuildConfig.DEBUG) {
			Log.d(tag, msg);
		}
	}
}
