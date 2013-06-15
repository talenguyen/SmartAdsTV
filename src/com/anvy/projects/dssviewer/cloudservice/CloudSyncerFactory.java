package com.anvy.projects.dssviewer.cloudservice;

import android.content.Context;

import com.anvy.projects.dssviewer.cloudservice.googledrive.GoogleDriveSyncer;

public class CloudSyncerFactory {

	public enum SyncerType {
		GoogleDrive,
		Dropbox
	}
	
	public static ICloudSyncer getCloudSyncer(SyncerType type, Context context, String accountName) {
		switch (type) {
		case GoogleDrive:
			return new GoogleDriveSyncer(context, accountName);

		default:
			return null;
		}
	}
}
