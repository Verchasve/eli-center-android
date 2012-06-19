package com.eli.filemanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.widget.Toast;

@SuppressWarnings("unused")
public class BluetoothService {

	BluetoothActivity activity;
	
	// Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private AcceptThread mAcceptThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	
	public BluetoothService(Handler handler, BluetoothActivity activity, BluetoothAdapter mmAdapter) {
		this.activity = activity;
		mAdapter = mmAdapter;
        mState = STATE_NONE;
        mHandler = handler;
	}
	
	public synchronized void start() {

        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        setState(STATE_LISTEN);

        if (mAcceptThread == null) {
        	mAcceptThread = new AcceptThread();
        	mAcceptThread.start();
        }
    }

	public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
        	mAcceptThread.cancel();
        	mAcceptThread = null;
        }
        setState(STATE_NONE);
    }
	
	private void connectionFailed() {
        viewToast("Unable to connect device");
        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }
	
	private void connectionLost() {
        viewToast("Device connection was lost");
        // Start the service over to restart listening mode
        BluetoothService.this.start();
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
	
	private synchronized void setState(int state) {
        mState = state;

        mHandler.obtainMessage(ProcessBluetooth.ACTION_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }
    
    public void viewToast(String string) {
    	Message msg = mHandler.obtainMessage(ProcessBluetooth.ACTION_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(ProcessBluetooth.TOAST, string);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
	}
    
    private class AcceptThread extends Thread {
	    private final BluetoothServerSocket mmServerSocket;
	    private UUID MY_UUID = null;

	    public AcceptThread() {
	    	MY_UUID = createUUID();
	    	System.out.println("UUID: " + MY_UUID);
	        BluetoothServerSocket tmp = null;
	        try {
	            tmp = mAdapter.listenUsingRfcommWithServiceRecord("AcceptThread", MY_UUID);
	        } catch (IOException e) { }
	        mmServerSocket = tmp;
	    }

	    public void run() {
	        BluetoothSocket socket = null;
            while (mState != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }

                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                        case STATE_LISTEN:
                        case STATE_CONNECTING:
                            connected(socket);
                            break;
                        case STATE_NONE:
                        case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                            	viewToast("Could not close unwanted socket");
                            }
                            break;
                        }
                    }
                }
            }
	    }

	    public void cancel() {
	        try {
	            mmServerSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	public synchronized void connect(BluetoothDevice device) {
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}
        
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
	
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
			mAdapter.cancelDiscovery();
	        try {
	            mmSocket.connect();
	        } catch (IOException connectException) {
	            try {
	                mmSocket.close();
	            } catch (IOException closeException) {
		            
	            }
	            connectionFailed();
                return;
	        }
	        
	        synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
	        
	        connected(mmSocket);
	    }
	 
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}

	public synchronized void connected(BluetoothSocket socket) {
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
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
	        byte[] buffer = new byte[1024];
	        int bytes;
	 
	        // Keep listening to the InputStream until an exception occurs
	        while (true) {
	            try {
	                // Read from the InputStream
	                bytes = mmInStream.read(buffer);
	                // Send the obtained bytes to the UI activity
	                mHandler.obtainMessage(2, bytes, -1, buffer)
	                        .sendToTarget();
	            } catch (IOException e) {
	            	connectionLost();
                    BluetoothService.this.start();
	                break;
	            }
	        }
	    }
	 
	    /* Call this from the main activity to shutdown the connection */
	    public void cancel() {
	        try {
	            mmSocket.close();
	        } catch (IOException e) { }
	    }
	}
	
	/*public void pairDevice(BluetoothDevice device) {
	    String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
	    Intent intent = new Intent(ACTION_PAIRING_REQUEST);
	    String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
	    intent.putExtra(EXTRA_DEVICE, device);
	    String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
	    int PAIRING_VARIANT_PIN = 0;
	    intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    activity.startActivity(intent);
	}*/
	
}
