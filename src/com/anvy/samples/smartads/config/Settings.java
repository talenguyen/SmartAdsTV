package com.anvy.samples.smartads.config;

import java.io.File;

import com.anvy.samples.smartads.view.RotatedFrameLayout.Rotation;

import android.content.pm.ActivityInfo;

public class Settings {

	// ======================= Static public =======================
	public static final float SCREEN_RATIO = (float) 16 / 9;
	public static final int DEGREE_OFFSET = 90;

	public static final String IMG_PREVIEW_NAME = "preview.jpg";
	public static final String HTML_NAME = "index.html";
	public static final String APP_DIR = "AnvyAds";

	// ======================= Static =======================
	public static int sScreenWidth = 0;
	public static int sScreenHeight = 0;
	public static int sOrientation = Orientation.PORTRAIT;
	public static boolean isFirstEnter = true;
	public static File sContentDir = null;
	public static Rotation sRotation = Rotation.Degrees_0;

	public class Orientation {
		public static final int PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		public static final int LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	}

}
