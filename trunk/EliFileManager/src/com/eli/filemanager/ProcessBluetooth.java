package com.eli.filemanager;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.widget.GridView;
import android.widget.Toast;

import com.eli.filemanager.pojo.Files;

public class ProcessBluetooth {

	private final int REQUEST_ENABLE_BT = 1;
	ArrayList<Files> list_device;
	Files item;
	BluetoothActivity activity;
	ExtendAdapter adapter;
	GridView gridview;
	Drawable drawable;
	
	public ProcessBluetooth(BluetoothActivity activity){
		this.activity = activity;
		list_device = new ArrayList<Files>();
		gridview = (GridView)activity.findViewById(R.id.gridview);
		checkDevice();
	}
	
	public void refresh(){
		adapter = new ExtendAdapter(activity, R.layout.extenddetail, list_device);
		gridview.setAdapter(adapter);
	}
	
	public void checkDevice(){
		try{
			list_device = new ArrayList<Files>();
			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null) {
				Toast.makeText(activity, "Your device dosen't support Bluetooth" , Toast.LENGTH_SHORT).show();
				return;
			}
			if (!mBluetoothAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    return;
			}
			Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				drawable = activity.getResources().getDrawable(R.drawable.device);
			    for (BluetoothDevice device : pairedDevices) {
			    	item = new Files();
			    	
			    	item.setIcon(drawable);
			    	item.setName(device.getName() + "\n" + device.getAddress());
			    	list_device.add(item);
			    }
			    refresh();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
