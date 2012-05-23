package com.eli.filemanager;


import ftp.pojo.FtpUser;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity {
	String lang = "EN";
	Bundle language;
	FtpUser emp = new FtpUser();
	TextView logout, username;
	ListView myUpload;

	public String getLang() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			lang = extras.getString("lang");
		}
		return lang;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfile);

		language = getIntent().getExtras().getBundle(getLang());

		emp = (FtpUser) getIntent().getExtras().get("em");
		Intent currentIntent = this.getIntent();
		emp = (FtpUser) currentIntent.getSerializableExtra("em");
		username = (TextView) findViewById(R.id.username);
		username.setText(language.getString("welcome") + ": "
				+ emp.getUsername());

		logout = (TextView) findViewById(R.id.logout);
		logout.setText(" - " + language.getString("logout"));
		logout.setOnClickListener(logoutAction);
		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, MyUploadActivity.class);
		intent.putExtras(getIntent().getExtras());

		spec = tabHost
				.newTabSpec("Upload")
				.setIndicator("Upload")
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, MyDownloadActivity.class);
		intent.putExtras(getIntent().getExtras());
		spec = tabHost
				.newTabSpec("Download")
				.setIndicator("Download")
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, MyFilesActivity.class);
		intent.putExtras(getIntent().getExtras());
		spec = tabHost
				.newTabSpec("My file")
				.setIndicator("My file")
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

	}

	private OnClickListener logoutAction = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i=new Intent(MainActivity.this, LogoutActivity.class);
			i.putExtra("ac","new");
			startActivity(i);
		}
	};
}
