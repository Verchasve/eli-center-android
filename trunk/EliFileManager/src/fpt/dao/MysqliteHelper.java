package fpt.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MysqliteHelper extends SQLiteOpenHelper{
	public static final String TABLE_FTP = "ftpconnect";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_HOSTNAME = "ftp_hostname";
	public static final String COLUMN_PORT = "ftp_port";
	public static final String COLUMN_USERNAME = "ftp_username";
	public static final String COLUMN_PASSWORD = "ftp_password";

	private static final String DATABASE_NAME = "eliftp.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_FTP + "( " + COLUMN_ID + " integer primary key autoincrement, " + COLUMN_HOSTNAME
			+ " text not null , " + COLUMN_PORT + " text not null, "+ COLUMN_USERNAME + " text not null, " + COLUMN_PASSWORD + " text not null );";
	
	public MysqliteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MysqliteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FTP);
		onCreate(db);
	}

}
