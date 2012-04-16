package com.eli.filemanager;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.dao.UsersDAO;
import com.eli.filemanager.pojo.Users;

public class SettingActivity extends Activity {
	Spinner backgroundSpinner;
	Spinner displaySpinner;
	SpinnerAdapter backgroundAdapter;
	SpinnerAdapter displayAdapter;

	UsersDAO usersDAO;
	TextView test;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		test = (TextView) findViewById(R.id.test);

		usersDAO = new UsersDAO(this);
		
		backgroundSpinner = (Spinner) findViewById(R.id.snBackground);
		displaySpinner = (Spinner) findViewById(R.id.snDisplay);

		LoadSetting.load(this);
		
		backgroundSpinner.setOnItemSelectedListener(selectBackground(LoadSetting.users.getBackground()));
		displaySpinner.setOnItemSelectedListener(selectDisplay(LoadSetting.users.getDisplay()));

		backgroundSpinner.setSelection(LoadSetting.users.getBackground());
		displaySpinner.setSelection(LoadSetting.users.getDisplay());
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
