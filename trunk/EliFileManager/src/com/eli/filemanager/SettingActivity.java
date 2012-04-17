package com.eli.filemanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.dao.UsersDAO;

public class SettingActivity extends Activity {
	Spinner backgroundSpinner;
	Spinner displaySpinner;
	SpinnerAdapter backgroundAdapter;
	SpinnerAdapter displayAdapter;
	ProgressDialog mProgressDialog;
	UsersDAO usersDAO;
	TextView test;

	boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		test = (TextView) findViewById(R.id.test);

		usersDAO = new UsersDAO(this);

		backgroundSpinner = (Spinner) findViewById(R.id.snBackground);
		displaySpinner = (Spinner) findViewById(R.id.snDisplay);

		LoadSetting.load(this);

		backgroundSpinner
				.setOnItemSelectedListener(selectBackground(LoadSetting.users
						.getBackground()));
		displaySpinner
				.setOnItemSelectedListener(selectDisplay(LoadSetting.users
						.getDisplay()));

		backgroundSpinner.setSelection(LoadSetting.users.getBackground());
		displaySpinner.setSelection(LoadSetting.users.getDisplay());

	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
        case ProcessSearch.DIALOG_DOWNLOAD_PROGRESS:
        	mProgressDialog = new ProgressDialog(this);
        	mProgressDialog.setTitle("Saving...");
        	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        	mProgressDialog.setCancelable(false);
        	mProgressDialog.show();
            return mProgressDialog;
        default:
            return null;
        }
	}

	@Override
	protected void onResume() {
		flag = false;
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			loading();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void loading(){
		new AsyncTask<String, String, Void>(){

			@Override
		    protected void onPreExecute() {
				mProgressDialog = ProgressDialog.show(SettingActivity.this, "", "Saving...", true);
		    }
			
			protected void onProgressUpdate(String... progress) {
		         mProgressDialog.setMessage(progress[0]);
		    }
			
			@Override
			protected Void doInBackground(String... arg0) {
				publishProgress("Saving...");
				if (flag) {
					Intent intent = new Intent(SettingActivity.this, ListActivity.class);
					startActivity(intent);
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void arg) {
			    if (mProgressDialog.isShowing()){
			    	mProgressDialog.dismiss();
			    }
			}
		}.execute("");
	}

	private OnItemSelectedListener selectBackground(final int background) {

		OnItemSelectedListener action = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (background != arg2) {
					usersDAO = new UsersDAO(SettingActivity.this);
					usersDAO.saveData(arg2,
							displaySpinner.getSelectedItemPosition());
					ListActivity a = new ListActivity();
					a.destroy();
					flag = true;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
		return action;
	}

	private OnItemSelectedListener selectDisplay(final int display) {

		OnItemSelectedListener action = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (display != arg2) {
					usersDAO = new UsersDAO(SettingActivity.this);
					usersDAO.saveData(
							backgroundSpinner.getSelectedItemPosition(), arg2);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
		return action;
	}
}
