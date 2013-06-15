package com.anvy.projects.dssviewer.data.database;

import android.net.Uri;

import com.thechange.libs.contentprovider.AbsContentProvider;
import com.thechange.libs.dbtable.AbsDBTable;


public class DSSContentProvider extends AbsContentProvider {

	private static final String DB_NAME = "AnvyDSS.db";
	private static final int DB_VERSION = 1; 
	@Override
	protected String getDBName() {
		return DB_NAME;
	}

	@Override
	protected int getDBVersion() {
		return DB_VERSION;
	}

	@Override
	protected AbsDBTable[] getTables() {
		return DSSDataContract.DB_TABLES;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

}
