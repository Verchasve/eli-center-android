package com.eli.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;


public class SettingActivity extends Activity{
	Spinner backgroundSpinner;
	Spinner displaySpinner;
	
	String[] backgroundColor = {"White","Black"};
	String[] displayStyle = {"List View","Grid View"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		backgroundSpinner = (Spinner) findViewById(R.id.snBackground);
		displaySpinner = (Spinner) findViewById(R.id.snDisplay);
		
		backgroundSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}
