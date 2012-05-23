package com.eli.filemanager;


import java.util.List;

import fpt.util.FTPUtil;
import ftp.pojo.FtpConnect;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CopyOfMainActivity2 extends Activity {
	String lang = "EN";
	Bundle language;
	//FtpUser emp = new FtpUser();
	TextView logout, username;
	FtpConnect ftp=new FtpConnect();
	Button bntDownload,bntGo;
	EditText locaton;

	public String getLang() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			lang = extras.getString("lang");
		}
		return lang;
	}
	ListView listFile;
	CheckFileAdapter adapter=new CheckFileAdapter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfile2);

		language = getIntent().getExtras().getBundle(getLang());
		locaton=(EditText) findViewById(R.id.locaton);
		bntGo=(Button) findViewById(R.id.bntGo);
		
		//Intent currentIntent = this.getIntent();
		ftp =  (FtpConnect) getIntent().getExtras().get("ftp");
		username = (TextView) findViewById(R.id.username);
		username.setText(language.getString("welcome") + ": "+ ftp.getUsername());

		logout = (TextView) findViewById(R.id.logout);
		logout.setText(" - " + language.getString("logout"));
		logout.setOnClickListener(logoutAction);
		
		locaton.setText("/");
		listFile=(ListView) findViewById(R.id.lstFile);
//		bntDownload=(Button) findViewById(R.id.bntDownload);
		
		loadListFiles("/");
		bntDownload.setOnClickListener(downloadEvent);
		
		bntGo.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				loadListFiles(locaton.getText().toString());
				
			}
		});
		

	}
	private OnClickListener downloadEvent=new OnClickListener() {		
		@Override
		public void onClick(View arg0) {
			downloadDialog();
			
		}
	};
	public void loadLocation(String path){
		String temp=locaton.getText().toString();
		if("/".equals(temp)){
			if(FTPUtil.checkFile(temp+path)==1){
				locaton.setText(temp+path);
				loadListFiles(temp+path);
			}
		
		}else{
			if(FTPUtil.checkFile(temp+"/"+path)==1){
				locaton.setText(temp+"/"+path);
				loadListFiles(temp+"/"+path);
			}
		}
		
	}
	//get list upload
	public void loadListFiles(String path){
		//List<String> lst= FTPUtil.getListFile(path);
		
		adapter=new CheckFileAdapter();	
		
		//adapter.list=lst;
		//adapter.context=this;
		
		listFile.setAdapter(adapter);
	}
	
	
	private void downloadDialog(){
		LayoutInflater inflater=(LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		 
		View view=inflater.inflate(R.layout.folders,null);
		final FolderLayout localFolders = (FolderLayout) view.findViewById(R.id.localfolders);
		localFolders.setDir(Environment.getExternalStorageDirectory().toString());
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//System.out.prln("size: "+adapter.getListCheck().size());
				if(adapter.getListCheck().size()>0){
					//FTPUtil.download(localFolders.getMyPath().getText().toString(),adapter.getListCheck());
				}
				
				
			}

		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}


	private OnClickListener logoutAction = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i=new Intent(CopyOfMainActivity2.this, LogoutActivity.class);
			i.putExtra("ac","new");
			startActivity(i);
		}
	};
}
