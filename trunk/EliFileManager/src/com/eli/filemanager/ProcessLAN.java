package com.eli.filemanager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

public class ProcessLAN {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	ProgressDialog mProgressDialog;
	LANActivity activity;
	GridView gridview;
	LANAdapter adapter;
	ArrayList<String> listLan;
	String absoluteIP = "";
	boolean flag = true; // flag == true la scan all, false la absolute

	public ProcessLAN(LANActivity activity) {
		this.activity = activity;
		initObject();
	}

	public void initObject() {
		listLan = new ArrayList<String>();
		gridview = (GridView) activity.findViewById(R.id.gridview);
	}

	public void scanAll() {
		processScan();
	}

	public void refresh() {
		listLan = new ArrayList<String>();
		adapter = new LANAdapter(activity, R.layout.landetail, listLan);
		gridview.setAdapter(adapter);
	}

	public void breakDuringScan() {
		adapter = new LANAdapter(activity, R.layout.landetail, listLan);
		gridview.setAdapter(adapter);
	}

	public void scanAbsoluteIP() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final EditText input = new EditText(activity);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		input.setLayoutParams(lp);
		input.setLines(1);
		input.setSingleLine(true);
		builder.setView(input);
		builder.setTitle("Absolute IP");
		builder.setNegativeButton("Cancel", null);
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String iptxt = input.getText().toString();
				if (iptxt == null || iptxt.equals("")) {
					return;
				} else {
					absoluteIP = iptxt;
					// do something
				}
			}
		});
		builder.show();
	}

	AsyncTask<String, String, Void> asyn;
	boolean running = true;
	public void processScan() {
		running = true;
		asyn = new AsyncTask<String, String, Void>() {
			@Override
			protected void onPreExecute() {
				mProgressDialog = ProgressDialog.show(activity, "",
						"Loading...", true);
				mProgressDialog.setCancelable(true);
				mProgressDialog.setOnCancelListener(new OnCancelListener() {
		            @Override
		            public void onCancel(DialogInterface dialog) {
		                running = false;
		            }
		        });
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setMessage(progress[0]);
			}

			@Override
			protected Void doInBackground(String... params) {
				try {
					if(running){
						InetAddress localhost = null;
						for (Enumeration<NetworkInterface> en = NetworkInterface
								.getNetworkInterfaces(); en.hasMoreElements();) {
							NetworkInterface intf = en.nextElement();
							for (Enumeration<InetAddress> enumIpAddr = intf
									.getInetAddresses(); enumIpAddr
									.hasMoreElements();) {
								InetAddress inetAddress = enumIpAddr.nextElement();
								publishProgress("Address : "
										+ inetAddress.getHostAddress().toString());
								if (!inetAddress.isLoopbackAddress()) {
									publishProgress(inetAddress.getHostAddress()
											.toString());
									localhost = inetAddress;
								}
							}
						}
						byte[] ip = localhost.getAddress();

						for (int i = 1; i <= 254; i++) {
							ip[3] = (byte) i;
							InetAddress address = InetAddress.getByAddress(ip);
							if (address.isReachable(1000)) {
								System.out
										.println(address
												+ " machine is turned on and can be pinged");
								publishProgress(address.toString());
							} else if (!address.getHostAddress().equals(
									address.getHostName())) {
								listLan.add(address.toString());
								publishProgress(address.toString());
								System.out.println(address
										+ " machine is known in a DNS lookup");
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void arg) {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
					refresh();
				}
			}
			
			protected void onCancelled() {
				running = false;
			};
		};
		asyn.execute("");
	}
}
