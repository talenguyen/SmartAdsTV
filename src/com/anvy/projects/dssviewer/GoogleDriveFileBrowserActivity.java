package com.anvy.projects.dssviewer;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anvy.projects.dssviewer.R;
import com.anvy.projects.dssviewer.async.RequestAsyncTask.RequestCallback;
import com.anvy.projects.dssviewer.cloudservice.GoogleCloudStorageSyncService;
import com.anvy.projects.dssviewer.cloudservice.googledrive.GoogleDriveCheckAccessTask;
import com.anvy.projects.dssviewer.cloudservice.googledrive.GoogleDriveGetFoldersTask;
import com.anvy.projects.dssviewer.cloudservice.googledrive.GoogleDriveManager;
import com.anvy.projects.dssviewer.config.KeyKeeper;
import com.anvy.projects.dssviewer.config.Settings;
import com.anvy.projects.dssviewer.util.FileUtil;
import com.anvy.projects.dssviewer.util.Logger;
import com.anvy.projects.dssviewer.util.SharedPreferenceUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

public class GoogleDriveFileBrowserActivity extends BaseActivity implements
		OnItemClickListener {

	static final int REQUEST_ACCOUNT_PICKER = 1;
	static final int REQUEST_AUTHORIZATION = 2;
	static final String SYNC_FOLDER_NAME = "GoogleDriveSync";

	protected static final String TAG = GoogleDriveFileBrowserActivity.class
			.getSimpleName();

	private static Drive mService;
	private GoogleAccountCredential mCredential;
	private FileAdapter mAdapter;
	private Stack<String> mFolderIdStack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cloud_storage_browser);
		
		mFolderIdStack = new Stack<String>();
		mFolderIdStack.push(GoogleDriveManager.ROOT_FOLER_ID);
		mAdapter = new FileAdapter(this);
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);
		findViewById(R.id.apply).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				final String driveSyncFolder = Settings.sContentDir.getPath()
						+ java.io.File.separator + SYNC_FOLDER_NAME;
				FileUtil.mkdirs(driveSyncFolder);
				SharedPreferenceUtil.putString(KeyKeeper.KEY_SYNC_FOLDER,
						driveSyncFolder);
				SharedPreferenceUtil.putString(
						KeyKeeper.KEY_CLOUD_STORAGE_FOLDER_ID,
						mFolderIdStack.pop());
				startService(new Intent(GoogleDriveFileBrowserActivity.this,
						GoogleCloudStorageSyncService.class));
				finish();
			}
		});

		mCredential = GoogleAccountCredential.usingOAuth2(
				DSSViewerApp.getContext(), DriveScopes.DRIVE);
		startActivityForResult(mCredential.newChooseAccountIntent(),
				REQUEST_ACCOUNT_PICKER);
	}

	protected void onSaveInstanceState(Bundle outState) {
	};

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		switch (requestCode) {
		case REQUEST_ACCOUNT_PICKER:
			if (resultCode == RESULT_OK && data != null
					&& data.getExtras() != null) {
				String accountName = data
						.getStringExtra(KeyKeeper.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					// Save account
					SharedPreferenceUtil.putString(KeyKeeper.KEY_ACCOUNT_NAME,
							accountName);
					mCredential.setSelectedAccountName(accountName);
					grantDriveAccess();
				}
			} else {
				finish();
			}
			break;
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				grantDriveAccess();
			} else {
				startActivityForResult(mCredential.newChooseAccountIntent(),
						REQUEST_ACCOUNT_PICKER);
			}
			break;
		}
	}

	private void grantDriveAccess() {
		new GoogleDriveCheckAccessTask(mCredential,
				new RequestCallback<UserRecoverableAuthException>() {

					@Override
					public void onPreExecute() {
						showLoadingDialog(null);
					}

					@Override
					public void onPostExecute(
							UserRecoverableAuthException results) {
						dismissLoadingDialog();
						if (results == null) {
							Logger.log(TAG, "Drive access OK!");
							mService = getDriveService(mCredential);
							displayFolder(mFolderIdStack.peek());
						} else {
							startActivityForResult(results.getIntent(),
									REQUEST_AUTHORIZATION);
						}
					}

				}).execute();

	}

	private void displayFolder(String folderId) {
		new GoogleDriveGetFoldersTask(mService, folderId,
				new RequestCallback<List<File>>() {

					@Override
					public void onPreExecute() {
						showLoadingDialog(null);
					}

					@Override
					public void onPostExecute(List<File> results) {
						dismissLoadingDialog();
						mAdapter.changeFileSet(results);
					}
				}).execute();
	}

	private Drive getDriveService(GoogleAccountCredential credential) {
		return new Drive.Builder(AndroidHttp.newCompatibleTransport(),
				new GsonFactory(), credential).build();
	}

	private static class FileAdapter extends BaseAdapter {

		private List<File> mItems;
		private LayoutInflater mLayoutInflater;

		/**
		 * Constructor of {@link FileAdapter}
		 * 
		 * @param activity
		 *            the activity
		 */
		public FileAdapter(Activity activity) {
			mItems = new ArrayList<File>();
			mLayoutInflater = activity.getLayoutInflater();
		}

		public void changeFileSet(List<File> files) {
			if (files == null || files.size() == 0) {
				mItems.clear();
				notifyDataSetChanged();
				return;
			}
			mItems = files;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public File getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.list_file_item,
						null);
			}

			ViewHolder holder = getViewHolder(convertView);
			bindView(holder, getItem(position));

			return convertView;
		}

		private void bindView(ViewHolder holder, File item) {
			if (item.getMimeType().equals(GoogleDriveManager.FILE_TYPE_FOLDER)) {
				holder.checkBox.setVisibility(View.GONE);
				holder.image.setImageResource(R.drawable.ic_type_folder_big);
			} else {
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.image.setImageResource(R.drawable.ic_type_file_big);
			}
			holder.textView.setText(item.getTitle());
		}

		private ViewHolder getViewHolder(View view) {
			ViewHolder holder = (ViewHolder) view.getTag();
			if (holder == null) {
				holder = new ViewHolder(view);
				view.setTag(holder);
			}
			return holder;
		}

		private class ViewHolder {
			CheckBox checkBox;
			ImageView image;
			TextView textView;

			public ViewHolder(View view) {
				checkBox = (CheckBox) view.findViewById(R.id.checkBox);
				image = (ImageView) view.findViewById(R.id.imgType);
				textView = (TextView) view.findViewById(R.id.textView);
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final String folderId = mAdapter.getItem(position).getId();
		mFolderIdStack.push(folderId);
		displayFolder(folderId);
	}

	@Override
	public void onBackPressed() {
		mFolderIdStack.pop();
		if (mFolderIdStack.size() > 0) {
			displayFolder(mFolderIdStack.peek());
		} else {
			super.onBackPressed();
		}
	}

}
