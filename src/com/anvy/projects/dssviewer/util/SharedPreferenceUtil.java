package com.anvy.projects.dssviewer.util;

import android.app.Activity;

import com.anvy.projects.dssviewer.DSSViewerApp;

public class SharedPreferenceUtil {

	private static final String PREF_NAME = "Anvy_pref";

	public static final int getInt(String key) {
		return DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
				.getInt(key, 0);
	}

	public static final String getString(String key) {
		return DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
				.getString(key, null);
	}

	public static final void putInt(String key, int value) {
		DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE).edit()
				.putInt(key, value).commit();
	}

	public static final void putString(String key, String value) {
		DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE).edit()
				.putString(key, value).commit();
	}

	public static void putLong(String key, long value) {
		DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE).edit()
				.putLong(key, value).commit();
	}

	public static long getLong(String key) {
		return DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
				.getLong(key, -1);
	}

	public static boolean getBoolean(String key) {
		return DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
				.getBoolean(key, false);
	}

	public static void putBoolean(String key, boolean value) {
		DSSViewerApp.getContext()
				.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE).edit()
				.putBoolean(key, value).commit();
	}

}
