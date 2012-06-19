package com.eli.filemanager;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BluetoothActivity extends Activity{

	ProcessBluetooth processBT;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_bluetooth);
		processBT = new ProcessBluetooth(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.scan_bluetooth:
				processBT.doDiscovery();
				break;
			case R.id.discoverable:
				processBT.ensureDiscoverable();
				break;
			case R.id.backbt:
				processBT.switchTo();
				break;
			case R.id.refresh:
				processBT.refresh();
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_bluetooth, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (processBT.mBtAdapter != null) {
        	processBT.mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(processBT.mReceiver);
        
        processBT.destroy();
    }
	
	@Override
	protected void onResume() {
		processBT.resume();
		super.onResume();
	}

}
