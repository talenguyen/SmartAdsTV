package com.anvy.samples.smartads;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.anvy.samples.smartads.config.Settings;
import com.anvy.samples.smartads.dialog.SelectRotationDialog;
import com.anvy.samples.smartads.dialog.SelectRotationDialog.SelectRotationDialogListener;
import com.anvy.samples.smartads.util.Logger;

public class LoadingActivity extends BaseActivity {

	public static final String KEY_ACTIVE_ITEM_POSITION = "key:activeItemPosition";
	public static final String SHARED_PREFS_NAME = "app:sharedprefs";

	private static final String KEY_SCREEN_WIDTH = "key:screenWidth";
	private static final String TAG = LoadingActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_NAME,
				MODE_PRIVATE);
		Settings.sScreenWidth = prefs.getInt(KEY_SCREEN_WIDTH, 0);
		if (Settings.sScreenWidth == 0) {
			Settings.sScreenWidth = getScreenWidth();
			prefs.edit().putInt(KEY_SCREEN_WIDTH, Settings.sScreenWidth)
					.commit();
		}

		Settings.sScreenHeight = (int) (Settings.sScreenWidth / Settings.SCREEN_RATIO);

		Settings.sContentDir = findContentDir(new File("/mnt/"));

		if (Settings.sContentDir != null
				&& Settings.sContentDir.list().length > 0) {

			SelectRotationDialog dialog = new SelectRotationDialog();
			dialog.setSelectRotationDialogListener(new SelectRotationDialogListener() {

				@Override
				public void onDismissListener() {
					startActivity(new Intent(LoadingActivity.this,
							SettingsActivity.class));
					finish();
				}
			});
			dialog.show(getSupportFragmentManager(), TAG);
		} else {
			Toast.makeText(this, R.string.no_content, Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private int getScreenWidth() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		if (displaymetrics.widthPixels > displaymetrics.heightPixels) {
			return displaymetrics.widthPixels;
		} else {
			return (int) (displaymetrics.widthPixels * Settings.SCREEN_RATIO);
		}
	}

	private File findContentDir(File dir) {
		Logger.log(TAG, dir.getPath());
		if (dir == null || !dir.isDirectory() || dir.getName().contains(".")
				|| dir.getName().contains("secure")) {
			return null;
		}

		if (dir.getName().equals(Settings.APP_DIR)) {
			return dir;
		}

		for (File child : dir.listFiles()) {
			final File result = findContentDir(child);
			if (result != null) {
				return result;
			}
		}

		return null;
	}

	@Override
	protected View getContentView() {
		return getLayoutInflater().inflate(R.layout.activity_loading, null);
	}

}
