package com.anvy.projects.dssviewer.cloudservice.googledrive;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

import com.anvy.projects.dssviewer.DSSViewerApp;
import com.anvy.projects.dssviewer.LoadingActivity;
import com.anvy.projects.dssviewer.R;
import com.anvy.projects.dssviewer.cloudservice.ICloudSyncer;
import com.anvy.projects.dssviewer.config.KeyKeeper;
import com.anvy.projects.dssviewer.data.DriveFile;
import com.anvy.projects.dssviewer.data.database.DSSDataContract;
import com.anvy.projects.dssviewer.util.FileUtil;
import com.anvy.projects.dssviewer.util.Logger;
import com.anvy.projects.dssviewer.util.SharedPreferenceUtil;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Changes;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.Change;
import com.google.api.services.drive.model.ChangeList;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GoogleDriveSyncer implements ICloudSyncer {

	public static final String FILE_TYPE_FOLDER = "application/vnd.google-apps.folder";
	private static final String TAG = GoogleDriveSyncer.class.getSimpleName();
	/** Field query parameter used on request to the drive.about.get endpoint. */
	private static final String ABOUT_GET_FIELDS = "largestChangeId";
	private static final int NOTIFY_INIT_SYNC_FOLDER_ID = 0;
	private static final int NOTIFY_SYNC_FOLDER_ID = 1;
	private Context mContext;
	private boolean isAuthenticated;
	private String mAccountName;
	private Long mLargestChangeId;
	private Drive mDriveService = null;
	private NotificationManager mNotifyManager;
	private Builder mBuilder;

	public GoogleDriveSyncer(Context context, String accountName) {
		this.mContext = context;
		this.mAccountName = accountName;
		this.mLargestChangeId = getLargestChangeId();
	}

	@Override
	public void performSync() {
		// Check if account is authenticated
		if (isAuthenticated) {
			// If account is authenticated perform sync.
			if (mDriveService == null) {
				mDriveService = getDriveService();
			}

			Logger.log(TAG, "largestChangeId: " + mLargestChangeId);
			if (mLargestChangeId == -1) {
				performFullSync();
			} else {
				List<File> changedFiles = getChangedFiles(mLargestChangeId);
				if (changedFiles.size() > 0) {
					try {
						final Cursor cursor = DSSViewerApp
								.getContext()
								.getContentResolver()
								.query(DSSDataContract.DB_TABLES[DSSDataContract.TABLE_DRIVE_FOLER_INDEX]
										.getContentUri(), null, null, null,
										null);
						Log.d(TAG, "Got local folders: " + cursor.getCount());
						int syncCount = 0;
						for (boolean more = cursor.moveToFirst(); more; more = cursor
								.moveToNext()) {

							// Merge.
							DriveFile driveFile = DriveFile.consume(cursor);
							for (int i = 0, count = changedFiles.size(); i < count; i++) {
								final File file = changedFiles.get(i);
								final List<ParentReference> parents = file
										.getParents();
								if (parents == null || parents.size() == 0) {
									continue;
								}

								if (file.getParents().get(0).getId()
										.equals(driveFile.getId())) {
									syncCount++;
									initNotifySync();
									updateNotify(NOTIFY_SYNC_FOLDER_ID, false,
											(i * 100 / count));
									// File belonging to local synchronized
									// folder.
									if (file.getLabels().getTrashed()) {
										// File trashed -> Delete file local
										FileUtil.delete(driveFile.getLocalDir()
												+ "/" + file.getTitle());
									} else {
										// File is new -> download to local.
										download(file, driveFile.getLocalDir());
									}
								}
							}
						}
						if (syncCount > 0) {
							updateNotify(NOTIFY_SYNC_FOLDER_ID, false, 100);
						}
						mLargestChangeId += 1;
						storeLargestChangeId(mLargestChangeId);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		} else {
			// If account still not authenticate yet. Perform authenticate
			// request.
			authenticate();
		}
	}

	private void performFullSync() {
		initNotifyInitSyncFolder();
		updateNotify(NOTIFY_INIT_SYNC_FOLDER_ID, true, 0);
		final String selectedFolderId = SharedPreferenceUtil
				.getString(KeyKeeper.KEY_CLOUD_STORAGE_FOLDER_ID);
		try {
			// Download all file from sync
			final String syncFolder = SharedPreferenceUtil
					.getString(KeyKeeper.KEY_SYNC_FOLDER);
			File syncFolderFile = GoogleDriveManager.retrieveFile(
					mDriveService, selectedFolderId);

			Logger.log(TAG, "initing...");
			download(syncFolderFile, syncFolder);
			Logger.log(TAG, "all file has synced");

			// Store largestChangeId
			About about = mDriveService.about().get()
					.setFields(ABOUT_GET_FIELDS).execute();
			mLargestChangeId = about.getLargestChangeId();
			storeLargestChangeId(mLargestChangeId);
			updateNotify(NOTIFY_INIT_SYNC_FOLDER_ID, true, 100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void authenticate() {
		try {
			GoogleAuthUtil.getTokenWithNotification(mContext, mAccountName,
					"oauth2:" + DriveScopes.DRIVE_FILE, null);

			isAuthenticated = true;
		} catch (UserRecoverableNotifiedException e) {
			Logger.log("UserRecoverableNotifiedException",
					"Failed to get token but notified user");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GoogleAuthException e) {
			e.printStackTrace();
		}
	}

	private void download(File file, String output) throws IOException {
		final String fileOutput = output + "/" + file.getTitle();
		if (GoogleDriveManager.isFolder(file)) {
			Logger.log(TAG, "File download is folder: " + fileOutput);
			FileUtil.mkdirs(fileOutput);
			DriveFile driveFile = new DriveFile(file.getId(), fileOutput);
			try {
				DSSViewerApp
						.getContext()
						.getContentResolver()
						.insert(DSSDataContract.DB_TABLES[DSSDataContract.TABLE_DRIVE_FOLER_INDEX]
								.getContentUri(), driveFile.getContentValues());
			} catch (Exception e) {
				Logger.log(TAG, "Insert Error!" + e);
			}
			List<File> files = GoogleDriveManager.retrieveAllFilesInFolder(
					mDriveService, file.getId());
			if (files != null && files.size() > 0) {
				for (File child : files) {
					download(child, fileOutput);
				}
			}
		} else {
			Logger.log(TAG, "Starting download file: " + fileOutput);
			downloadFile(file.getDownloadUrl(), fileOutput);
		}
	}

	private void downloadFile(String url, String output) throws IOException {
		InputStream in = null;
		try {
			in = GoogleDriveManager.downloadFile(mDriveService, url);
			FileUtil.write(in, output);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Retrieve a collection of files that have changed since the provided
	 * {@code changeId}.
	 * 
	 * @param changeId
	 *            Change ID to retrieve changed files from.
	 * @return Map of changed files key'ed by their file ID.
	 */
	/**
	 * Retrieve a collection of files that have changed since the provided
	 * {@code changeId}.
	 * 
	 * @param changeId
	 *            Change ID to retrieve changed files from.
	 * @return Map of changed files key'ed by their file ID.
	 */
	private List<File> getChangedFiles(long changeId) {
		List<File> result = new ArrayList<File>();

		try {
			Changes.List request = mDriveService.changes().list()
					.setStartChangeId(changeId);
			do {
				ChangeList changes = request.execute();
				long largestChangeId = changes.getLargestChangeId().longValue();

				for (Change change : changes.getItems()) {
					if (!change.getDeleted()) {
						if (change.getFile() != null) {
							result.add(change.getFile());
						}
					}
				}

				if (largestChangeId > mLargestChangeId) {
					mLargestChangeId = largestChangeId;
				}
				request.setPageToken(changes.getNextPageToken());
			} while (request.getPageToken() != null
					&& request.getPageToken().length() > 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.d(TAG, "Got changed Drive files: " + result.size() + " - "
				+ mLargestChangeId);
		return result;
	}

	private Drive getDriveService() {
		GoogleAccountCredential credential = GoogleAccountCredential
				.usingOAuth2(mContext, DriveScopes.DRIVE);
		credential.setSelectedAccountName(mAccountName);
		// credential.getToken();
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(),
				new GsonFactory(), credential).build();
	}

	/**
	 * Retrieve the largest change ID for the current user if available.
	 * 
	 * @return The largest change ID, {@code -1} if not available.
	 */
	private long getLargestChangeId() {
		return SharedPreferenceUtil.getLong("largest_change_" + mAccountName);
	}

	/**
	 * Store the largest change ID for the current user.
	 * 
	 * @param changeId
	 *            The largest change ID to store.
	 */
	private void storeLargestChangeId(long changeId) {
		SharedPreferenceUtil
				.putLong("largest_change_" + mAccountName, changeId);
	}

	private void initNotifyInitSyncFolder() {
		mNotifyManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setContentTitle("Initializing Google Drive sync folder")
				.setContentText("Initializing...")
				.setSmallIcon(R.drawable.icon).setAutoCancel(true);
	}

	private void updateNotify(int id, boolean isContinuous, int progress) {

		if (progress < 100) {
			// Sets the progress indicator to a max value, the
			// current completion percentage, and "determinate"
			// state
			if (isContinuous) {
				mBuilder.setProgress(0, 0, true);
			} else {
				mBuilder.setProgress(100, progress, true);
			}
			// Displays the progress bar for the first time.
			mNotifyManager.notify(id, mBuilder.build());
		} else {
			addIntent();
			// When the loop is finished, updates the notification
			mBuilder.setContentText("Completed!")
			// Removes the progress bar
					.setProgress(0, 0, false);
			mNotifyManager.notify(id, mBuilder.build());
		}
	}

	private void initNotifySync() {
		mNotifyManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(mContext);
		mBuilder.setContentTitle(
				"Synchronizing local data with Google Drive folder")
				.setContentText("Sync in progress")
				.setSmallIcon(R.drawable.icon).setAutoCancel(true);
	}

	private void addIntent() {
		// Creates an Intent for the Activity
		Intent notifyIntent = new Intent(mContext, LoadingActivity.class);
		// Sets the Activity to start in a new, empty task
		notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// Creates the PendingIntent
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0,
				notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		// Puts the PendingIntent into the notification builder
		mBuilder.setContentIntent(pendingIntent);
	}

}
