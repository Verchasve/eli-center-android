package com.eli.filemanager;

import com.eli.filemanager.pojo.Files;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity{

	ProcessSearch process;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		process = new ProcessSearch(this);
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
