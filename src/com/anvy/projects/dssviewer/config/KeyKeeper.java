package com.anvy.projects.dssviewer.config;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.ECLAIR)
public class KeyKeeper {
	public static final String KEY_ACCOUNT_NAME = AccountManager.KEY_ACCOUNT_NAME;
	public static final String KEY_SCREEN_WIDTH = "key:screenWidth";
	public static final String KEY_CONTENT_DIR = "key:contentDir";
	public static final String KEY_CLOUD_STORAGE_FOLDER_ID = "key:cloudFolderId";
	public static final String KEY_SYNC_FOLDER = "key:storageFolder";
	public static final String KEY_TOKEN = "key:token";
}
