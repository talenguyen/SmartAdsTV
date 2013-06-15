package com.anvy.projects.dssviewer;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.anvy.projects.dssviewer.config.KeyKeeper;
import com.anvy.projects.dssviewer.config.Settings;
import com.anvy.projects.dssviewer.interfaces.RotationSettingCallback;
import com.anvy.projects.dssviewer.util.FileUtil;
import com.anvy.projects.dssviewer.util.SharedPreferenceUtil;
import com.anvy.projects.dssviewer.view.RotatedFrameLayout.Rotation;

public class LoadingActivity extends BaseActivity {

	public static final String KEY_ACTIVE_ITEM_POSITION = "key:activeItemPosition";
	public static final String SHARED_PREFS_NAME = "app:sharedprefs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		new InitTask(this).execute();

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

	private static class InitTask extends
			AsyncTask<Void, Void, Class<? extends Activity>> {

		private WeakReference<Activity> mContextWeakRef;

		public InitTask(Activity activity) {
			mContextWeakRef = new WeakReference<Activity>(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			((BaseActivity) mContextWeakRef.get()).showLoadingDialog(null);
		}

		@Override
		protected Class<? extends Activity> doInBackground(Void... params) {

			// Check token. If there is no already token require User enter CD
			// Key.
			Settings.sToken = SharedPreferenceUtil
					.getString(KeyKeeper.KEY_TOKEN);
			if (Settings.sToken == null) {
				return CDKeyActivity.class;
			}

			Activity activity = mContextWeakRef.get();
			Settings.sScreenWidth = SharedPreferenceUtil
					.getInt(KeyKeeper.KEY_SCREEN_WIDTH);
			if (Settings.sScreenWidth == 0) {
				Settings.sScreenWidth = ((LoadingActivity) activity)
						.getScreenWidth();
				SharedPreferenceUtil.putInt(KeyKeeper.KEY_SCREEN_WIDTH,
						Settings.sScreenWidth);
			}

			Settings.sScreenHeight = (int) (Settings.sScreenWidth / Settings.SCREEN_RATIO);

			String dir = SharedPreferenceUtil
					.getString(KeyKeeper.KEY_CONTENT_DIR);

			if (dir == null) {
				if (FileUtil.isExternalStorageWriteable()) {
					dir = Environment.getExternalStorageDirectory().getPath()
							+ "/" + Settings.APP_DIR;
				} else {
					dir = activity.getFilesDir().getPath() + "/"
							+ Settings.APP_DIR;
				}

				SharedPreferenceUtil.putString(KeyKeeper.KEY_CONTENT_DIR, dir);

				FileUtil.mkdirs(dir);
			}

			Settings.sContentDir = new File(dir);
			if (Settings.sContentDir.list().length == 0) {
				try {
					String zipFile = Settings.APP_DIR + ".zip";
					FileUtil.write(activity.getAssets().open(zipFile),
							Settings.sContentDir + File.separator + zipFile);
					FileUtil.unZip(Settings.sContentDir.getPath()
							+ File.separator + zipFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			return SettingsActivity.class;
		}

		@Override
		protected void onPostExecute(final Class<? extends Activity> result) {
			super.onPostExecute(result);
			final BaseActivity activity = (BaseActivity) mContextWeakRef.get();
			activity.dismissLoadingDialog();

			Toast.makeText(activity, R.string.loading_completed,
					Toast.LENGTH_LONG).show();

			if (result == CDKeyActivity.class) {
				activity.startActivity(new Intent(activity, result));
				activity.finish();
			} else {
				activity.showRotationSettings(new RotationSettingCallback() {

					@Override
					public void onSetting(Rotation rotation) {
						activity.startActivity(new Intent(activity, result));
						activity.finish();
					}
				});
			}
		}

	}

}
