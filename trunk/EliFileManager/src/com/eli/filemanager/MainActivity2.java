package com.eli.filemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ftp.pojo.FtpConnect;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity2 extends TabActivity {
	String lang = "EN";
	Bundle language;
	// FtpUser emp = new FtpUser();
	TextView logout, username;
	FtpConnect ftp = new FtpConnect();

	public String getLang() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			lang = extras.getString("lang");
		}
		return lang;
	}

	ListView listFile;
	CheckFileAdapter adapter = new CheckFileAdapter();
	int flagtab =0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfile2);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		language = getIntent().getExtras().getBundle(getLang());
//		flagtab=getIntent().getExtras().getInt("flagtab", 0);
		flagtab = getIntent().getExtras().getInt("flagtab");
//		FtpClientActivity.flagChangeViewDownload = getIntent().getExtras().getBoolean("flagChangeViewDownload");
//		FtpClientActivity.flagChangeViewUpload = getIntent().getExtras().getBoolean("flagChangeViewUpload");
		
		System.out.println("kjlkjk: "+flagtab);
		if(flagtab==0){
			writefileUpload();
		}else if(flagtab==1){
			deletefile();
		}

		// Intent currentIntent = this.getIntent();
		ftp = (FtpConnect) getIntent().getExtras().get("ftp");
		username = (TextView) findViewById(R.id.username);
		username.setText(language.getString("welcome") + ": "
				+ ftp.getUsername());

		logout = (TextView) findViewById(R.id.logout);
		logout.setText(" - " + language.getString("logout"));
		logout.setOnClickListener(logoutAction);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		// Do the same for the other tabs
		Intent intent1;
		intent1 = new Intent().setClass(this, DownloadActivity.class);
		intent1.putExtras(getIntent().getExtras());
//		intent1.putExtra("flagChangeViewDownload", FtpClientActivity.flagChangeViewDownload);
		
		
		spec = tabHost.newTabSpec(language.getString("download"))
				.setIndicator(language.getString("download"), res.getDrawable(R.drawable.ic_tab_downloads))
				.setContent(intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		tabHost.addTab(spec);

		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, UploadActivity.class);
		intent.putExtras(getIntent().getExtras());
//		intent.putExtra("flagChangeViewUpload", FtpClientActivity.flagChangeViewUpload);
		
		// Initialize a TabSpec for each tab and add it to the TabHost
		
		spec = tabHost.newTabSpec(language.getString("upload"))
				.setIndicator(language.getString("upload"), res.getDrawable(R.drawable.ic_tab_uploads))
				.setContent(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
		tabHost.addTab(spec);

		
		tabHost.setCurrentTab(flagtab);

	}

	private OnClickListener logoutAction = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent(MainActivity2.this, FtpClientActivity.class);
			//i.putExtra("langexit", lang);
//			try {
//				writefile();
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			startActivity(i);
		}
	};
	
//	private void writefile() throws Exception{
//		//File f = new File("user.txt");
//		FileOutputStream file = openFileOutput("user.txt", Context.MODE_PRIVATE);
//		file.write(lang.getBytes());		
//		file.close();
//	}
	
	private void deletefile(){
		FileOutputStream file;
		try {
			String s="/";
			file = openFileOutput("download.txt", Context.MODE_PRIVATE);
			file.write(s.getBytes());		
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writefileUpload(){
		FileOutputStream file;
		try {
			String s=Environment.getExternalStorageDirectory().toString();

			file = openFileOutput("upload.txt", Context.MODE_PRIVATE);
			file.write(s.getBytes());		
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
