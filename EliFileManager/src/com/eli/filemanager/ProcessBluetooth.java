package com.eli.filemanager;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
	BluetoothAdapter mBluetoothAdapter;
	
	Set<BluetoothDevice> pairedDevices;
	
	public ProcessBluetooth(BluetoothActivity activity){
		this.activity = activity;
		list_device = new ArrayList<Files>();
		gridview = (GridView)activity.findViewById(R.id.gridview);
		gridview.setOnItemClickListener(onClickItem());
		checkDevice();
	}
	
	public void refresh(){
		adapter = new ExtendAdapter(activity, R.layout.extenddetail, list_device);
		gridview.setAdapter(adapter);
	}
	
	public void checkDevice(){
		try{
			list_device = new ArrayList<Files>();
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if (mBluetoothAdapter == null) {
				Toast.makeText(activity, "Your device dosen't support Bluetooth" , Toast.LENGTH_SHORT).show();
				return;
			}
			if (!mBluetoothAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			    return;
			}
			pairedDevices = mBluetoothAdapter.getBondedDevices();
			// If there are paired devices
			if (pairedDevices.size() > 0) {
				drawable = activity.getResources().getDrawable(R.drawable.device);
			    for (BluetoothDevice device : pairedDevices) {
			    	item = new Files();			    	
			    	item.setIcon(drawable);
			    	item.setName(device.getName() + "\n" + device.getAddress());
			    	item.setBluetooth(device);
			    	list_device.add(item);
			    }
			    refresh();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private OnItemClickListener onClickItem(){
		OnItemClickListener action = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				BluetoothSocket socket;
				try{
					final TelephonyManager tm = (TelephonyManager) activity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

				    final String tmDevice, tmSerial, androidId;
				    tmDevice = "" + tm.getDeviceId();
				    tmSerial = "" + tm.getSimSerialNumber();
				    androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

				    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
					
					Files file = (Files) arg0.getItemAtPosition(arg2);
					socket = file.getBluetooth().createRfcommSocketToServiceRecord(deviceUuid);

					mBluetoothAdapter.cancelDiscovery();
					socket.connect();
				}catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		};		
		return action;
	}
}
