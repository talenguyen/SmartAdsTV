package com.anvy.projects.dssviewer.cloudservice.googledrive;

import java.io.IOException;
import java.util.List;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class GoogleDriveGetFoldersTask extends
		GoogleDriveRequestAsyncTask<List<File>> {

	private String mFolderId;

	public GoogleDriveGetFoldersTask(Drive service, String folderId,
			RequestCallback<List<File>> callback) {
		super(service, callback);
		mFolderId = folderId;
	}

	@Override
	protected List<File> doInBackground(Void... params) {
		try {
			return GoogleDriveManager.retrieveAllFoldersInFolder(service,
					mFolderId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
