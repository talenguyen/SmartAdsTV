package com.anvy.projects.dssviewer.cloudservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import com.anvy.projects.dssviewer.cloudservice.CloudSyncerFactory.SyncerType;
import com.anvy.projects.dssviewer.config.KeyKeeper;
import com.anvy.projects.dssviewer.util.Logger;
import com.anvy.projects.dssviewer.util.SharedPreferenceUtil;

public class GoogleCloudStorageSyncService extends Service {

	static final String TAG = GoogleCloudStorageSyncService.class
			.getSimpleName();

	public static final int NOTIFICATION_FILE_CHANGE_ID = 1;
	public static final long TIME_PERIOD = 10 * 1000;

	Handler mHandler;
	boolean isExecuting;
	ICloudSyncer mSyncer;

	Runnable mDetectChange = new Runnable() {

		@SuppressLint("InlinedApi")
		@Override
		public void run() {

			Logger.log(TAG, "Entered Runnable");
			if (!isExecuting) {
				isExecuting = true;
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						mSyncer.performSync();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						isExecuting = false;
					}
				}.execute();
			}
			mHandler.postDelayed(mDetectChange, TIME_PERIOD);

		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	public void onCreate() {
		super.onCreate();
		Logger.log(TAG, "{onCreate()}");
		mHandler = new Handler();
		mSyncer = CloudSyncerFactory.getCloudSyncer(SyncerType.GoogleDrive,
				this,
				SharedPreferenceUtil.getString(KeyKeeper.KEY_ACCOUNT_NAME));
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mHandler.removeCallbacks(mDetectChange);
		mHandler.post(mDetectChange);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		Logger.log(TAG, "Service is destroyed!");
		mHandler.removeCallbacks(mDetectChange);
		super.onDestroy();
	}

}
