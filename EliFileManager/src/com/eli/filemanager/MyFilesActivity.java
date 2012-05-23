package com.eli.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class MyFilesActivity extends Activity{
	ListView myUpload;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myfiles);
		
		myUpload=(ListView) findViewById(R.id.myUpload);
		
		loadMyUpload();
	}
	
	//get list upload
	private void loadMyUpload(){
		//List<String> lst= FTPUtil.getListFile(null);
//		//System.out.prln("site: "+lst.size());
//		myUpload.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, lst));
	}
}
