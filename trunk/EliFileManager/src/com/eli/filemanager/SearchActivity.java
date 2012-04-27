package com.eli.filemanager;

import java.util.Locale;

import com.eli.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;

public class SearchActivity extends Activity{

	ProcessSearch process;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLocale();
		setContentView(R.layout.search);
		process = new ProcessSearch(this);
	}
	
	public void initLocale(){
		int key = Util.users.getLanguage();
		String languageToLoad = Util.locale(key);  
	    Locale locale = new Locale(languageToLoad);   
	    Locale.setDefault(locale);  
	    Configuration config = new Configuration();  
	    config.locale = locale;  
	    getBaseContext().getResources().updateConfiguration(config,   
	    getBaseContext().getResources().getDisplayMetrics());  
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
        case ProcessSearch.DIALOG_DOWNLOAD_PROGRESS:
        	process.mProgressDialog = new ProgressDialog(this);
        	process.mProgressDialog.setTitle("Searching...");
        	process.mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	process.mProgressDialog.setCancelable(false);
        	process.mProgressDialog.setCanceledOnTouchOutside(true);
        	process.mProgressDialog.setButton(DialogInterface.BUTTON1, "Cancel" , new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
        	process.mProgressDialog.show();
            return process.mProgressDialog;
        default:
            return null;
        }
	}
}
