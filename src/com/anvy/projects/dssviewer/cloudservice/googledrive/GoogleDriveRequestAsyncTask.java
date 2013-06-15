package com.anvy.projects.dssviewer.cloudservice.googledrive;

import com.anvy.projects.dssviewer.async.RequestAsyncTask;
import com.google.api.services.drive.Drive;

public abstract class GoogleDriveRequestAsyncTask<T> extends
		RequestAsyncTask<T> {

	protected Drive service;

	public GoogleDriveRequestAsyncTask(Drive service,
			RequestCallback<T> callback) {
		super(callback);
		this.service = service;
	}

}
