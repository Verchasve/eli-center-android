package com.eli.filemanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "elifilemanager.db";
	public static final int DATABASE_VERSION = 3;
	public static final String TABLE_NAME = "users";
	public static final String ID = "id";
	public static final String DISPLAY = "display";
	public static final String BACKGROUND = "background";
	public static final String LANGUAGE = "language";
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String query = "create table " + TABLE_NAME + " (" + ID
				+ " INTEGER PRIMARY KEY, " + DISPLAY + " TEXT, " + BACKGROUND
				+ " TEXT ,"+LANGUAGE+" TEXT)";
		db.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String query = "drop table if exists " + TABLE_NAME;
		db.execSQL(query);
		onCreate(db);
	}

}
