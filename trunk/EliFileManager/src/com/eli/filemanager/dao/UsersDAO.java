package com.eli.filemanager.dao;


import com.eli.filemanager.database.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UsersDAO {
	private DatabaseHelper dbhelper;
	Context context;

	public UsersDAO(Context context) {
		this.context = context;
		dbhelper = new DatabaseHelper(context);
	}

	public void saveData(int b, int dp,int l) {
		try {
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			ContentValues contentValue = new ContentValues();
			contentValue.put(DatabaseHelper.ID, 1);
			contentValue.put(DatabaseHelper.BACKGROUND, b);
			contentValue.put(DatabaseHelper.DISPLAY, dp);
			contentValue.put(DatabaseHelper.LANGUAGE,l);
			delete();
			if (!db.isOpen()) {
			    db = context.getApplicationContext().openOrCreateDatabase(
			                "/data/data/com.eli.filemanager/databases/elifilemanager.db",
			                SQLiteDatabase.OPEN_READWRITE, null);
			 }
			db.insert(DatabaseHelper.TABLE_NAME, null, contentValue);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		try {
			String[] arg = { 1 + "" };
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.ID + " = ?",
					arg);
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
