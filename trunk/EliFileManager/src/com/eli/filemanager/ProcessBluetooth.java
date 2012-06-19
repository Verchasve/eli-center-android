package com.eli.filemanager;

import java.util.ArrayList;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.eli.filemanager.BluetoothAdapters;
import com.eli.filemanager.pojo.Files;

public class ProcessBluetooth {
	
	BluetoothActivity activity;
	BluetoothAdapters adapterPairedDevices;
	BluetoothAdapters adapterNewDevices;
	BluetoothService mBluetoothService;
	
	ListView pairedListView;
	ListView newDevicesListView;
	Drawable drawable;
	
    // Member fields
    BluetoothAdapter mBtAdapter;
    ArrayList<Files> mPairedDevicesArray;
    ArrayList<Files> mNewDevicesArray;
    
    public Handler handler = new Handler();
    protected static ProgressDialog dialog;
    BluetoothSocket socket;
    
    private static final int REQUEST_ENABLE_BT = 3;
    
    public static final int ACTION_STATE_CHANGE = 1;
    public static final int ACTION_TOAST = 2;
    
    public static final String TOAST = "toast";
    public static String DEVICE_NAME = "name_device";

	public ProcessBluetooth(BluetoothActivity activity ) {
		this.activity = activity;
		initObject();
	}
	
	private void initObject() {
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) {
			Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			Intent intent0 = new Intent(activity,ListActivity.class);
			activity.startActivity(intent0);
			activity.finish();
        	return;
        }
		
		if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
		
		mPairedDevicesArray = new ArrayList<Files>();
		mNewDevicesArray = new ArrayList<Files>();
		
		pairedListView = (ListView) activity.findViewById(R.id.paired_devices);
		pairedListView.setOnItemClickListener(itemDeviceClick());
		newDevicesListView = (ListView) activity.findViewById(R.id.new_devices);
		newDevicesListView.setOnItemClickListener(itemDeviceClick());
		
		mBluetoothService = new BluetoothService(mHandler, activity, mBtAdapter);
		
		getDevice();
	}
	
	AsyncTask<String, String, Void> asyn;
	boolean running = true;
	
	public OnItemClickListener itemDeviceClick() {
		OnItemClickListener action = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Files file = (Files) arg0.getItemAtPosition(arg2);
				BluetoothDevice device = file.getBluetooth();
				DEVICE_NAME = device.getName();
				mBluetoothService.connect(device);
			}
		};
		return action;
	}
	
	public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                	Files f = new Files();
                	f.setName(device.getName());
                	f.setBluetooth(device);
                    mNewDevicesArray.add(f);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	dialog.dismiss();
                if (mNewDevicesArray.size() == 0) {
                    Toast.makeText(activity, R.string.none_found, Toast.LENGTH_SHORT).show();
                }
                refresh();
            }
        }
    };
    
    @SuppressWarnings("static-access")
	public void resume() {
    	System.out.println("RESUME ACTIVITY");
    	if (mBluetoothService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mBluetoothService.getState() == mBluetoothService.STATE_NONE) {
              // Start the Bluetooth chat services
            	mBluetoothService.start();
            }
        }
	}
    
    public void destroy() {
    	if (mBluetoothService != null) mBluetoothService.stop();
	}
    
    /**
     * Start device discover with the BluetoothAdapter
     */
    public void doDiscovery() {
    	mNewDevicesArray = new ArrayList<Files>();
    	dialog = ProgressDialog.show(activity, "", 
                "Scanning for devices...", true);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBtAdapter.startDiscovery();
    }
	
	public void switchTo() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					activity);
			builder.setTitle("Change View");
			builder.setItems(R.array.switchto_arr,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								Intent intent0 = new Intent(activity,ListActivity.class);
								activity.startActivity(intent0);
								activity.finish();
								break;
							case 1:
								Intent intent = new Intent(activity,LANActivity.class);
								activity.startActivity(intent);
								activity.finish();
								break;
							case 2:
								Intent intent2 = new Intent(activity,ListActivity.class);
								activity.startActivity(intent2);
								activity.finish();
								break;
							case 3:
								Intent intent3 = new Intent(activity,BluetoothActivity.class);
								activity.startActivity(intent3);
								activity.finish();
								break;
							default:
								break;
							}
						}

					});
			builder.setPositiveButton("Cancel", null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public void refresh() {
        adapterPairedDevices = new BluetoothAdapters(activity, R.layout.bluetooth_detail, mPairedDevicesArray);
        pairedListView.setAdapter(adapterPairedDevices);
		adapterNewDevices = new BluetoothAdapters(activity, R.layout.bluetooth_detail, mNewDevicesArray);
		newDevicesListView.setAdapter(adapterNewDevices);
	}
	
	public void getDevice() {
     // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, filter);

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            activity.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
            	drawable = activity.getResources().getDrawable(R.drawable.bluetooth_icon);
            	Files f = new Files();
            	f.setName(device.getName());
            	f.setBluetooth(device);
            	f.setIcon(drawable);
                mPairedDevicesArray.add(f);
            }
        } else {
        	Toast.makeText(activity, R.string.none_found, Toast.LENGTH_SHORT).show();
        }
        refresh();
	}
	
	public void ensureDiscoverable() {
        if (mBtAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivity(discoverableIntent);
        }
    }
	
	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ACTION_STATE_CHANGE:
                switch (msg.arg1) {
                case BluetoothService.STATE_CONNECTED:
                	//HIEN THI THONG BAO: DA KET NOI
                    //HAM QUAN LY FILE TREN THIET BI BLUETOOTH
                	dialog.dismiss();
                    break;
                case BluetoothService.STATE_CONNECTING:
                	dialog = ProgressDialog.show(activity, "", 
                            "Connecting to devices " + DEVICE_NAME, true);
                    break;
                case BluetoothService.STATE_LISTEN:
                case BluetoothService.STATE_NONE:
                	Toast.makeText(activity, "Disconnect to device " + DEVICE_NAME,Toast.LENGTH_SHORT).show();
                    break;
                }
                break;
            case ACTION_TOAST:
                Toast.makeText(activity, msg.getData().getString(TOAST),Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };

}
