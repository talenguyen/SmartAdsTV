package com.anvy.projects.dssviewer;

import android.app.Application;
import android.content.Context;

public class DSSViewerApp extends Application {

	private static DSSViewerApp sInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		sInstance = this;
	}

	public static Context getContext() {
		return sInstance;
	}
	
	public static void startSync() {
		
	}
}
