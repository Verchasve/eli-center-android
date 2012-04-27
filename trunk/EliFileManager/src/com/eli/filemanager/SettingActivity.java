package com.eli.filemanager;

import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.dao.UsersDAO;
import com.eli.util.Util;

public class SettingActivity extends Activity {
	Spinner backgroundSpinner;
	Spinner displaySpinner;
	Spinner languageSpinner;
	SpinnerAdapter backgroundAdapter;
	SpinnerAdapter displayAdapter;
	ProgressDialog mProgressDialog;
	UsersDAO usersDAO;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLocale();
		setContentView(R.layout.setting);

		usersDAO = new UsersDAO(this);

		backgroundSpinner = (Spinner) findViewById(R.id.snBackground);
		displaySpinner = (Spinner) findViewById(R.id.snDisplay);
		languageSpinner = (Spinner) findViewById(R.id.snLanguage);

		LoadSetting.load(this);
		backgroundSpinner.setSelection(LoadSetting.users.getBackground());
		displaySpinner.setSelection(LoadSetting.users.getDisplay());
		languageSpinner.setSelection(LoadSetting.users.getLanguage());
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
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setTitle(R.string.saving + "...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			loading();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void loading() {
		new AsyncTask<String, String, Void>() {

			@Override
			protected void onPreExecute() {
				mProgressDialog = ProgressDialog.show(SettingActivity.this, "",
						R.string.saving + "...", true);
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setMessage(progress[0]);
			}

			@Override
			protected Void doInBackground(String... arg0) {
				publishProgress("Saving...");
				usersDAO.saveData(backgroundSpinner.getSelectedItemPosition(),
						displaySpinner.getSelectedItemPosition(),languageSpinner.getSelectedItemPosition());
				Intent intent = new Intent(SettingActivity.this,
						ListActivity.class);
				startActivity(intent);
				finish();
				return null;
			}

			@Override
			protected void onPostExecute(Void arg) {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}
		}.execute("");
	}
}
