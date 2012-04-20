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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		usersDAO = new UsersDAO(this);

		backgroundSpinner = (Spinner) findViewById(R.id.snBackground);
		displaySpinner = (Spinner) findViewById(R.id.snDisplay);

		LoadSetting.load(this);
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
						"Saving...", true);
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setMessage(progress[0]);
			}

			@Override
			protected Void doInBackground(String... arg0) {
				publishProgress("Saving...");
				usersDAO.saveData(backgroundSpinner.getSelectedItemPosition(),
						displaySpinner.getSelectedItemPosition());
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