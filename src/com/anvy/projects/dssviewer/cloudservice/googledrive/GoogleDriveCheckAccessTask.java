package com.anvy.projects.dssviewer.cloudservice.googledrive;

import java.io.IOException;

import com.anvy.projects.dssviewer.async.RequestAsyncTask;
import com.anvy.projects.dssviewer.util.Logger;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

public class GoogleDriveCheckAccessTask extends
		RequestAsyncTask<UserRecoverableAuthException> {

	private GoogleAccountCredential credential;

	public GoogleDriveCheckAccessTask(GoogleAccountCredential credential,
			RequestCallback<UserRecoverableAuthException> callback) {
		super(callback);
		this.credential = credential;
	}

	@Override
	protected UserRecoverableAuthException doInBackground(Void... params) {
		try {
			// Trying to get a token right away to see if we are authorized
			credential.getToken();
		} catch (UserRecoverableAuthException e) {
			Logger.log("GoogleDriveCheckAccessTask", "grantDriveAccess");
			return e;
		} catch (GoogleAuthException e) {
			Logger.log("GoogleDriveCheckAccessTask", "GoogleAuthException");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
