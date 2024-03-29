package com.eli.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class LANActivity extends Activity {

	ProcessLAN processLAN;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listlan);
		processLAN = new ProcessLAN(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			processLAN.backButton();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.scan:
				processLAN.scanAll();
				break;
			case R.id.absip:
				processLAN.scanAbsoluteIP();
				break;	
			case R.id.back:
				processLAN.switchTo();
				break;	
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menulan, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
}
