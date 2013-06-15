package com.anvy.projects.dssviewer.data;

import android.content.ContentValues;
import android.database.Cursor;

import com.anvy.projects.dssviewer.data.database.DriveFolderTable;

public class DriveFile {
	private final String id;
	private final String localDir;

	public DriveFile(String id, String localDir) {
		this.id = id;
		this.localDir = localDir;
	}

	public String getId() {
		return id;
	}

	public String getLocalDir() {
		return localDir;
	}

	public ContentValues getContentValues() {
		ContentValues values = new ContentValues();
		values.put(DriveFolderTable.DB_COLUMN_FILE_ID, id);
		values.put(DriveFolderTable.DB_COLUMN_DIRECTORY, localDir);
		return values;
	}

	public static DriveFile consume(Cursor cursor) {
		final String id = cursor.getString(cursor
				.getColumnIndex(DriveFolderTable.DB_COLUMN_FILE_ID));
		final String directory = cursor.getString(cursor
				.getColumnIndex(DriveFolderTable.DB_COLUMN_DIRECTORY));
		return new DriveFile(id, directory);
	}
}
