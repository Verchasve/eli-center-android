package com.eli.filemanager.dao;

import java.util.ArrayList;
import java.util.Calendar;

import com.eli.filemanager.database.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsersDAO {
	private DatabaseHelper dbhelper;
	
	public UsersDAO(Context context){
		dbhelper = new DatabaseHelper(context);
	}
	
	public void saveData(String text) {
		try {
			String time = Calendar.getInstance().getTime().toString();			
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			ContentValues contentValue = new ContentValues();
			contentValue.put(DatabaseHelper.ID, 1);
			contentValue.put(DatabaseHelper.BACKGROUND, time);
			contentValue.put(DatabaseHelper.DISPLAY, text);
			db.insert(DatabaseHelper.TABLE_NAME, null, contentValue);			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public ArrayList<String> searchData(int id) {
		ArrayList<String> tempList = new ArrayList<String>();
		try {			
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			// SQLiteStatement sqlStatement = null;
			String query = "select * from " + DatabaseHelper.TABLE_NAME
					+ " where " + DatabaseHelper.ID + " = " + 1;
			// sqlStatement = db.compileStatement(query);
			// sqlStatement.bindString(1, text);
			Cursor cursor = db.rawQuery(query, null);
			while (cursor.moveToNext()) {
				tempList.add(cursor.getString(1) + "\n" + cursor.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}		
		return tempList;
	}
}
