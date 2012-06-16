package com.eli.filemanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

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
import android.telephony.TelephonyManager;
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
	
	ListView pairedListView;
	ListView newDevicesListView;
	Drawable drawable;
	
    // Member fields
    BluetoothAdapter mBtAdapter;
    ArrayList<Files> mPairedDevicesArray;
    ArrayList<Files> mNewDevicesArray;
    
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    
    public Handler handler = new Handler();
    protected static ProgressDialog dialog;
    BluetoothSocket socket;

    private static final int REQUEST_ENABLE_BT = 3;

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
				Method m;
				try {
					m = device.getClass().getMethod("createBond", (Class[])null);
					m.invoke(device, (Object[])null);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}

				int bondState = device.getBondState();
				if (bondState == BluetoothDevice.BOND_NONE || bondState == BluetoothDevice.BOND_BONDING)
				{
				    pairDevice(device);
				}
				
				mConnectThread = new ConnectThread(file.getBluetooth());
				mConnectThread.start();
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
	
	public void pairDevice(BluetoothDevice device) {
        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        Intent intent = new Intent(ACTION_PAIRING_REQUEST);
        String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
        intent.putExtra(EXTRA_DEVICE, device);
        String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
        int PAIRING_VARIANT_PIN = 0;
        intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }
	
	public UUID createUUID() {
    	final TelephonyManager tm = (TelephonyManager) activity.getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    return deviceUuid;
	}
	
	/*private class AcceptThread extends Thread {
	    private final BluetoothServerSocket mmServerSocket;
	    private UUID MY_UUID = null;

	    public AcceptThread() {
	    	MY_UUID = createUUID();
	        BluetoothServerSocket tmp = null;
	        try {
	            tmp = mBtAdapter.listenUsingRfcommWithServiceRecord("AcceptThread", MY_UUID);
	        } catch (IOException e) { }
	        mmServerSocket = tmp;
	    }

	    public void run() {
	        BluetoothSocket socket = null;
	        while (true) {
	            try {
	                socket = mmServerSocket.accept();
	            } catch (IOException e) {
	                break;
	            }
	            if (socket != null) {
	            	mConnectedThread = new ConnectedThread(socket);
	            	mConnectedThread.start();
	                try {
						mmServerSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	                break;
	            }
	        }
	    }

	    public void cancel() {
	        try {
	            mmServerSocket.close();
	        } catch (IOException e) { }
	    }
	}*/
	
	private class ConnectThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final BluetoothDevice mmDevice;
	    private UUID MY_UUID = null;
	 
	    public ConnectThread(BluetoothDevice device) {
	        BluetoothSocket tmp = null;
	        mmDevice = device;
	        MY_UUID = createUUID();
	 
	        try {
	            tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) { }
	        mmSocket = tmp;
	    }
	 
		public void run() {
	        mBtAdapter.cancelDiscovery();
	        try {
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) { }
	            return;
	        }
	        mConnectedThread = new ConnectedThread(mmSocket);
	        mConnectedThread.start();
	    }
	 
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

	private class ConnectedThread extends Thread {
	    private final BluetoothSocket mmSocket;
	    private final InputStream mmInStream;
	    private final OutputStream mmOutStream;
	 
	    public ConnectedThread(BluetoothSocket socket) {
	        mmSocket = socket;
	        InputStream tmpIn = null;
	        OutputStream tmpOut = null;
	 
	        // Get the input and output streams, using temp objects because
	        // member streams are final
	        try {
	            tmpIn = socket.getInputStream();
	            tmpOut = socket.getOutputStream();
	        } catch (IOException e) { }
	 
	        mmInStream = tmpIn;
	        mmOutStream = tmpOut;
	    }
	 
	    public void run() {
	        byte[] buffer = new byte[1024];  // buffer store for the stream
	        int bytes; // bytes returned from read()
	 
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	                handler.obtainMessage(2, bytes, -1, buffer)
	                        .sendToTarget();
	            } catch (IOException e) {
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to send data to the remote device */
	    public void write(byte[] bytes) {
	        try {
	            mmOutStream.write(bytes);
	        } catch (IOException e) { }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

}
