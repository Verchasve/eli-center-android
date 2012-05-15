package com.eli.filemanager;

import java.net.InetAddress;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
	
	public ProcessLAN(LANActivity activity){
		this.activity = activity;
		initObject();
	}
	
	public void initObject(){
		listLan = new ArrayList<String>();
		gridview = (GridView)activity.findViewById(R.id.gridview);
	}
	
	public void scanAll(){
		processScan();
	}
	
	public void refresh(){
		listLan = new ArrayList<String>();
		adapter = new LANAdapter(activity, R.layout.landetail, listLan);
		gridview.setAdapter(adapter);
	}
	
	public void breakDuringScan(){
		adapter = new LANAdapter(activity, R.layout.landetail, listLan);
		gridview.setAdapter(adapter);
	}
	
	public void scanAbsoluteIP(){
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
				if(iptxt == null || iptxt.equals("")){
					return;
				}else{
					absoluteIP = iptxt;
					//do something
				}
			}
		});
		builder.show();
	}
	
	public void processScan(){
		new AsyncTask<String, String, Void>() {

			@Override
			protected void onPreExecute() {
				mProgressDialog = ProgressDialog.show(activity, "", "Loading...", true);
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setMessage(progress[0]);
			}

			@Override
			protected Void doInBackground(String... params) {
				try {
					InetAddress localhost = InetAddress.getLocalHost();
					// this code assumes IPv4 is used
					byte[] ip = localhost.getAddress();

					for (int i = 1; i <= 254; i++) {
						ip[3] = (byte) i;
						InetAddress address = InetAddress.getByAddress(ip);
						if (address.isReachable(1000)) {
							System.out.println(address + " machine is turned on and can be pinged");
						} else if (!address.getHostAddress().equals(address.getHostName())) {
							listLan.add(address.toString());
							System.out.println(address + " machine is known in a DNS lookup");
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
		}.execute("");
	}
}
