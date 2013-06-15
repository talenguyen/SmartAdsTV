package com.anvy.projects.dssviewer.cloudservice.googledrive;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.anvy.projects.dssviewer.util.Logger;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveManager {

	public static final String ROOT_FOLER_ID = "root";
	public static final String FILE_TYPE_FOLDER = "application/vnd.google-apps.folder";
	private static final String TAG = "GoogleDriveManager";

	/**
	 * Get all files belonging to drive
	 * 
	 * @param service
	 *            Drive API service instance
	 * @return {@link List} of {@link File} in drive
	 * @throws IOException
	 */
	public static List<File> retrieveAllFiles(Drive service) throws IOException {
		List<File> result = new ArrayList<File>();
		Files.List request = service.files().list();
		do {
			try {
				FileList files = request.execute();
				result.addAll(files.getItems());
				request.setPageToken(files.getNextPageToken());
			} catch (IOException e) {
				request.setPageToken(null);
				e.printStackTrace();
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);

		return result;
	}

	/**
	 * Get all folder belonging to drive
	 * 
	 * @param service
	 *            Drive API service instance
	 * @return {@link List} of {@link File} in drive
	 * @throws IOException
	 */
	public static List<File> retrieveAllFolders(Drive service)
			throws IOException {
		List<File> files = retrieveAllFiles(service);
		if (files.size() == 0) {
			return null;
		}
		List<File> result = new ArrayList<File>();
		for (File file : files) {
			if (file.getMimeType().equals(FILE_TYPE_FOLDER)) {
				result.add(file);
			}
		}
		return result;
	}

	/**
	 * Get files belonging to a folder.
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @param folderId
	 *            ID of the folder to get files from.
	 */
	public static List<File> retrieveAllFilesInFolder(Drive service,
			String folderId) throws IOException {

		final List<ChildReference> childrenReference = new ArrayList<ChildReference>();
		final Children.List request = service.children().list(folderId);
		do {
			try {
				ChildList children = request.execute();
				childrenReference.addAll(children.getItems());
				request.setPageToken(children.getNextPageToken());
			} catch (IOException e) {
				request.setPageToken(null);
				e.printStackTrace();
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);
		List<File> result = new ArrayList<File>(childrenReference.size());
		for (ChildReference childReference : childrenReference) {
			result.add(service.files().get(childReference.getId()).execute());
		}
		return result;
	}

	/**
	 * Get all file folder type belonging to a folder.
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @param folderId
	 *            ID of the folder to get files from.
	 */
	public static List<File> retrieveAllFoldersInFolder(Drive service,
			String folderId) throws IOException {
		List<File> files = retrieveAllFilesInFolder(service, folderId);
		if (files.size() == 0) {
			return null;
		}

		List<File> result = new ArrayList<File>(files.size());
		for (File file : files) {
			if (file.getMimeType().equals(FILE_TYPE_FOLDER)) {
				result.add(file);
			}
		}
		return result;
	}

	/**
	 * Get all file's id belonging to a folder.
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @param folderId
	 *            ID of the folder to get files from.
	 */
	public static String retrieveAllFolderIdsInFolder(Drive service,
			String folderId) throws IOException {

		final List<ChildReference> childrenReference = new ArrayList<ChildReference>();
		final Children.List request = service.children().list(folderId);
		do {
			try {
				ChildList children = request.execute();
				childrenReference.addAll(children.getItems());
				request.setPageToken(children.getNextPageToken());
			} catch (IOException e) {
				request.setPageToken(null);
				e.printStackTrace();
			}
		} while (request.getPageToken() != null
				&& request.getPageToken().length() > 0);
		final StringBuilder result = new StringBuilder();
		for (ChildReference childReference : childrenReference) {
			File file = service.files().get(childReference.getId()).execute();
			if (file.getMimeType().equals(GoogleDriveManager.FILE_TYPE_FOLDER)) {
				result.append(file.getId() + ";");
				result.append(retrieveAllFolderIdsInFolder(service,
						file.getId()));
			}
		}
		Logger.log(TAG, "Children IDs: " + result.toString());
		return result.toString();
	}

	/**
	 * Download a file's content.
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @param file
	 *            Drive File instance.
	 * @return InputStream containing the file's content if successful,
	 *         {@code null} otherwise.
	 */
	public static InputStream downloadFile(Drive service, String url) {
		if (url != null && url.length() > 0) {
			try {
				HttpResponse resp = service.getRequestFactory()
						.buildGetRequest(new GenericUrl(url)).execute();
				return resp.getContent();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * Get a {@link File} on Drive
	 * 
	 * @param service
	 *            Drive API service instance.
	 * @return File a Drive file instance.
	 * @throws IOException
	 */
	public static File retrieveFile(Drive service, String fileId)
			throws IOException {
		return service.files().get(fileId).execute();
	}

	public static boolean isFolder(File file) {
		return file.getMimeType().equals(FILE_TYPE_FOLDER);
	}

}
