package com.anvy.projects.dssviewer.data.database;

import java.util.ArrayList;
import java.util.List;

import com.thechange.libs.dbtable.AbsDBTable;
import com.thechange.libs.dbtable.DBColumn;
import com.thechange.libs.dbtable.DataType;

public class DriveFolderTable extends AbsDBTable {

	public static final String DB_COLUMN_FILE_ID = "file_id";
	public static final String DB_COLUMN_DIRECTORY = "directory";

	private static final String TABLE_NAME = "drive_file";

	public DriveFolderTable(String authority) {
		super(authority);
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public List<DBColumn> getColumns() {
		List<DBColumn> columns = new ArrayList<DBColumn>(6);
		columns.add(new DBColumn(DB_COLUMN_FILE_ID, DataType.TEXT,
				"NOT NULL UNIQUE"));
		columns.add(new DBColumn(DB_COLUMN_DIRECTORY, DataType.TEXT, "NOT NULL"));
		return columns;
	}

}
