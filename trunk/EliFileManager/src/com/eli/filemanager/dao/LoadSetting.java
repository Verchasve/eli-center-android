package com.eli.filemanager.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.eli.filemanager.R;
import com.eli.filemanager.database.DatabaseHelper;
import com.eli.filemanager.pojo.Users;

public class LoadSetting {
	public static int background;
	public static Users users;
	public static void load(Context context){
		DatabaseHelper  dbhelper = new DatabaseHelper(context);
		users = new Users();
		try {
			SQLiteDatabase db = dbhelper.getWritableDatabase();
			String query = "select * from " + DatabaseHelper.TABLE_NAME
					+ " where " + DatabaseHelper.ID + " = " + 1;
			Cursor cursor = db.rawQuery(query, null);
			while (cursor.moveToNext()) {
				users.setId(cursor.getInt(0));
				users.setBackground(cursor.getInt(2));
				users.setDisplay(cursor.getInt(1));
				users.setLanguage(cursor.getInt(3));
			}
			cursor.close();
			db.close();
			
			if(users.getBackground()==0){
				background=R.color.white;
			}else if(users.getBackground()==1){
				background = R.color.black;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
