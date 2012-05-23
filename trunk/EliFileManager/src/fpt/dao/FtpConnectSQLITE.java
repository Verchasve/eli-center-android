package fpt.dao;

import java.util.ArrayList;
import java.util.List;

import ftp.pojo.FtpConnect;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class FtpConnectSQLITE {
	private SQLiteDatabase database;
	private MysqliteHelper helper;
	private String[] allColumns = {MysqliteHelper.COLUMN_ID,MysqliteHelper.COLUMN_HOSTNAME,MysqliteHelper.COLUMN_PORT,
			MysqliteHelper.COLUMN_USERNAME,MysqliteHelper.COLUMN_PASSWORD};
	
	private FtpConnect ftpConnect ;
	
	public FtpConnectSQLITE(Context context){
		helper = new MysqliteHelper(context);
	}
	
	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}
	
	public FtpConnect createFtpConnect(String host,String port,String us,String ps) {
		ContentValues values = new ContentValues();
		values.put(MysqliteHelper.COLUMN_HOSTNAME, host);
		values.put(MysqliteHelper.COLUMN_PORT, port);
		values.put(MysqliteHelper.COLUMN_USERNAME, us);
		values.put(MysqliteHelper.COLUMN_PASSWORD, ps);
		
		long insertId = database.insert(MysqliteHelper.TABLE_FTP, null,
				values);
		Cursor cursor = database.query(MysqliteHelper.TABLE_FTP,
				allColumns, MysqliteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		cursor.moveToFirst();
		FtpConnect newFtpConnect = cursorToFtp(cursor);
		cursor.close();
		return newFtpConnect;
	}
	
	
	
	public List<FtpConnect> getAllConnect() {
		List<FtpConnect> ftpConnects = new ArrayList<FtpConnect>();

		Cursor cursor = database.query(MysqliteHelper.TABLE_FTP,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			 ftpConnect = cursorToFtp(cursor);
			ftpConnects.add(ftpConnect);
			
			System.out.println("lst " + ftpConnect.getHostname());
			
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return ftpConnects;
	}
	
	public void deleteFtp(FtpConnect ftpConnect) {
		long id = ftpConnect.getIdftpconnect();
		System.out.println("ftp deleted with id: " + id);
		database.delete(MysqliteHelper.TABLE_FTP, MysqliteHelper.COLUMN_ID
				+ " = " + id, null);
		close();
	}
	
	// update ftp
	public boolean updateFtp(FtpConnect connect){
		ContentValues values = new ContentValues();
		values.put(MysqliteHelper.COLUMN_HOSTNAME, connect.getHostname());
		values.put(MysqliteHelper.COLUMN_PORT, connect.getPort());
		values.put(MysqliteHelper.COLUMN_USERNAME, connect.getUsername());
		values.put(MysqliteHelper.COLUMN_PASSWORD, connect.getPassword());
		
		return database.update(MysqliteHelper.TABLE_FTP, values, MysqliteHelper.COLUMN_ID + "=" + connect.getIdftpconnect(), null) >0;
	}
	
	public FtpConnect cursorToFtp(Cursor cursor){
		 ftpConnect = new FtpConnect();
		try{
			ftpConnect.setIdftpconnect(cursor.getInt(0));
			ftpConnect.setHostname(cursor.getString(1));
			ftpConnect.setPort(cursor.getString(2));
			ftpConnect.setUsername(cursor.getString(3));
		System.out.println("pass la gi ++ " + cursor.getString(4));
		ftpConnect.setPassword(cursor.getString(4));
		}catch(Exception e){
			System.out.println("pass la gi ++ ");
			e.printStackTrace();
		}
		return ftpConnect;
	}
	
}
