package com.anvy.projects.dssviewer.data.database;

import com.thechange.libs.dbtable.AbsDBTable;

public class DSSDataContract {

	public static final String AUTHORITY = "com.anvy.samples.smartads.provider";

	public static final AbsDBTable[] DB_TABLES = { new DriveFolderTable(AUTHORITY) };

	public static final int TABLE_DRIVE_FOLER_INDEX = 0;
}
