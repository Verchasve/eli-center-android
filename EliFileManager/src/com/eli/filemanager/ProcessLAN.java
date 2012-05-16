package com.eli.filemanager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video.Thumbnails;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.pojo.Files;
import com.eli.util.Util;

public class ProcessLAN {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	ProgressDialog mProgressDialog;
	LANActivity activity;
	GridView gridview;
	LANAdapter adapter;
	String absoluteIP = "";
	boolean flag = true; // flag == true la scan all, false la absolute
	ArrayList<String> paths;
	String path;
	NtlmPasswordAuthentication auth;
	
	//File smb
	ArrayList<Files> files,folders,list;
	Drawable icon;

	public ProcessLAN(LANActivity activity) {
		this.activity = activity;
		initObject();
	}

	public void initObject() {
		gridview = (GridView) activity.findViewById(R.id.gridview);
		paths = new ArrayList<String>();
		auth = new NtlmPasswordAuthentication(null,null,null);
	}
	
	public String getPath(){
		path = "";
		for (String a:paths){
			path += a;
		}
		return path;
	}
	
	// get files and folders of folder
	public void getAllListFile(String path) {
		list = new ArrayList<Files>();
		try {
		files = new ArrayList<Files>();
		folders = new ArrayList<Files>();
		SmbFile dir = new SmbFile(path);
		for(SmbFile f:dir.listFiles()){
			System.out.println(f.getName());
		}
//			for (SmbFile f : dir.listFiles()) {
//				if (f.isFile()) {
//					Bitmap bitmap = null;
//					if (Util.checkExtendFile(f.getName(), ".txt")
//							|| Util.checkExtendFile(f.getName(), ".csv")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.text_file);
//					}else if (Util.checkExtendFile(f.getName(), ".xml")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.xml_file);
//					} else if (Util.checkExtendFile(f.getName(), ".flv")
//							|| Util.checkExtendFile(f.getName(), ".3gp")
//							|| Util.checkExtendFile(f.getName(), ".avi")) {
//						bitmap = ThumbnailUtils.createVideoThumbnail(
//								f.getCanonicalPath(), Thumbnails.MICRO_KIND);
//						icon = new BitmapDrawable(bitmap);
//					} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.mp3_file);
//					} else if (Util.checkExtendFile(f.getName(), ".doc")
//							|| Util.checkExtendFile(f.getName(), ".docx")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.word_file);
//					} else if (Util.checkExtendFile(f.getName(), ".ppt")
//							|| Util.checkExtendFile(f.getName(), ".pptx")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.pptx_file);
//					} else if (Util.checkExtendFile(f.getName(), ".xls")
//							|| Util.checkExtendFile(f.getName(), ".xlsx")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.xlsx_file);
//					} else if (Util.checkExtendFile(f.getName(), ".zip")
//							|| Util.checkExtendFile(f.getName(), ".rar")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.rar_file);
//					} else if (Util.checkExtendFile(f.getName(), ".jpg")
//							|| Util.checkExtendFile(f.getName(), ".jpeg")
//							|| Util.checkExtendFile(f.getName(), ".png")
//							|| Util.checkExtendFile(f.getName(), ".bmp")
//							|| Util.checkExtendFile(f.getName(), ".gif")) {
//						icon = new BitmapDrawable(bitmap);
//					} else if (Util.checkExtendFile(f.getName(), ".apk")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.apk_file);
//					} else if (Util.checkExtendFile(f.getName(), ".exe")) {
//						icon = activity.getResources().getDrawable(
//								R.drawable.exe_file);
//					} else {
//						icon = activity.getResources().getDrawable(
//								R.drawable.unknown_file);
//					}
//					Files ff = new Files();
//					ff.setIcon(icon);
//					ff.setName(f.getName());
//					ff.setFolder(false);
//					ff.setSize(f.length());
//					ff.setModified(f.lastModified());
//					ff.setChildFile(ff.getSize()/(1024)+" KB | "+ Util.format1.format(ff.getModified()));
//					ff.setPath(f.getCanonicalPath());
//					files.add(ff);
//				} else if (f.isDirectory()) {
//					if(LoadSetting.users.getIcon()==0)
//						icon = activity.getResources().getDrawable(
//							R.drawable.folder_blue);
//					else if(LoadSetting.users.getIcon()==1)
//						icon = activity.getResources().getDrawable(
//								R.drawable.folder_blue2);
//					else if(LoadSetting.users.getIcon()==2)
//						icon = activity.getResources().getDrawable(
//								R.drawable.folder_yellow);
//					else if(LoadSetting.users.getIcon()==3)
//						icon = activity.getResources().getDrawable(
//								R.drawable.folder_yellow2);
//					Files ff = new Files();
//					ff.setIcon(icon);
//					ff.setName(f.getName());
//					ff.setFolder(true);
//					ff.setPath(f.getCanonicalPath());
//					folders.add(ff);
//				}
//			}
			sort(files,0);
			sort(folders,0);
			list.addAll(folders);
			list.addAll(files);
		} catch (NullPointerException e) {
			list.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sort(ArrayList<Files> lst,int sort) {

		switch (sort){					
			case 0:
				Collections.sort(lst, new Comparator<Files>() {
						@Override
						public int compare(Files object1, Files object2) {
							return object1.getName().toLowerCase()
									.compareTo(object2.getName().toLowerCase());
						}
				});
			break;
			case 1:
				Collections.sort(lst, new Comparator<Files>() {
					@Override
					public int compare(Files object1, Files object2) {
						return object1.getSize().compareTo(object2.getSize());
					}
				});
				break;
			case 2:
				Collections.sort(lst, new Comparator<Files>() {
					@Override
					public int compare(Files object1, Files object2) {
						return object1.getModified().compareTo(object2.getModified());
					}
				});
				break;
		}
	}

	public void scanAll() {
		processScan();
	}

	public void refresh() {
		list = new ArrayList<Files>();
		adapter = new LANAdapter(activity, R.layout.landetail, list);
		gridview.setAdapter(adapter);
	}

	public void breakDuringScan() {
		adapter = new LANAdapter(activity, R.layout.landetail, list);
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
					checkValidIP(absoluteIP);
				}
			}
		});
		builder.show();
	}
	
	public void checkValidIP(String ip){
		try{
			SmbFile smbFile = new SmbFile(ip,auth);
			smbFile.connect();
			SmbFile[] childs = smbFile.listFiles();
			if(childs.length > 0){
				analyzeListSMB(childs);
			}
		}catch (Exception e) {
			try{
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				
				final LinearLayout root = new LinearLayout(activity);
				root.setLayoutParams(lp);
				root.setOrientation(LinearLayout.VERTICAL);
				
				final EditText username = new EditText(activity);
				final EditText password = new EditText(activity);
				
				username.setLayoutParams(lp);
				username.setLines(1);
				username.setSingleLine(true);
				username.setHint("Username");
				password.setLayoutParams(lp);
				password.setLines(1);
				password.setSingleLine(true);
				password.setHint("Password");
				
				root.addView(username);
				root.addView(password);
				
				builder.setView(root);
				builder.setTitle("Authentication");
				builder.setNegativeButton("Cancel", null);
				builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String user = username.getText().toString();
						String pass = password.getText().toString();
						if (user == null) {
							user = "";
						}
						if(pass == null){
							pass = "";
						}
						loginToSharedFolder(user,pass);
					}
				});
				builder.show();
			}catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void loginToSharedFolder(String username, String password){
		try{
			auth = new NtlmPasswordAuthentication(null, username, password);
			SmbFile dir = new SmbFile(absoluteIP,auth);
			SmbFile[] childs = dir.listFiles();
			if(childs.length > 0){
				analyzeListSMB(childs);
			}
		}catch (Exception e) {
			e.printStackTrace();
			checkValidIP(absoluteIP);
		}
	}
	
	public void analyzeListSMB(SmbFile[] childs){
		try{
			//start
			list = new ArrayList<Files>();
			for (SmbFile f : childs) {
				if (f.isFile()) {
					Bitmap bitmap = null;
					if (Util.checkExtendFile(f.getName(), ".txt")
							|| Util.checkExtendFile(f.getName(), ".csv")) {
						icon = activity.getResources().getDrawable(
								R.drawable.text_file);
					}else if (Util.checkExtendFile(f.getName(), ".xml")) {
						icon = activity.getResources().getDrawable(
								R.drawable.xml_file);
					} else if (Util.checkExtendFile(f.getName(), ".flv")
							|| Util.checkExtendFile(f.getName(), ".3gp")
							|| Util.checkExtendFile(f.getName(), ".avi")) {
						bitmap = ThumbnailUtils.createVideoThumbnail(
								f.getCanonicalPath(), Thumbnails.MICRO_KIND);
						icon = new BitmapDrawable(bitmap);
					} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
						icon = activity.getResources().getDrawable(
								R.drawable.mp3_file);
					} else if (Util.checkExtendFile(f.getName(), ".doc")
							|| Util.checkExtendFile(f.getName(), ".docx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.word_file);
					} else if (Util.checkExtendFile(f.getName(), ".ppt")
							|| Util.checkExtendFile(f.getName(), ".pptx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.pptx_file);
					} else if (Util.checkExtendFile(f.getName(), ".xls")
							|| Util.checkExtendFile(f.getName(), ".xlsx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.xlsx_file);
					} else if (Util.checkExtendFile(f.getName(), ".zip")
							|| Util.checkExtendFile(f.getName(), ".rar")) {
						icon = activity.getResources().getDrawable(
								R.drawable.rar_file);
					} else if (Util.checkExtendFile(f.getName(), ".jpg")
							|| Util.checkExtendFile(f.getName(), ".jpeg")
							|| Util.checkExtendFile(f.getName(), ".png")
							|| Util.checkExtendFile(f.getName(), ".bmp")
							|| Util.checkExtendFile(f.getName(), ".gif")) {
						icon = new BitmapDrawable(bitmap);
					} else if (Util.checkExtendFile(f.getName(), ".apk")) {
						icon = activity.getResources().getDrawable(
								R.drawable.apk_file);
					} else if (Util.checkExtendFile(f.getName(), ".exe")) {
						icon = activity.getResources().getDrawable(
								R.drawable.exe_file);
					} else {
						icon = activity.getResources().getDrawable(
								R.drawable.unknown_file);
					}
					Files ff = new Files();
					ff.setIcon(icon);
					ff.setName(f.getName());
					ff.setFolder(false);
					ff.setSize(f.length());
					ff.setModified(f.lastModified());
					ff.setChildFile(ff.getSize()/(1024)+" KB | "+ Util.format1.format(ff.getModified()));
					ff.setPath(f.getCanonicalPath());
					files.add(ff);
				} else if (f.isDirectory()) {
					if(LoadSetting.users.getIcon()==0)
						icon = activity.getResources().getDrawable(
							R.drawable.folder_blue);
					else if(LoadSetting.users.getIcon()==1)
						icon = activity.getResources().getDrawable(
								R.drawable.folder_blue2);
					else if(LoadSetting.users.getIcon()==2)
						icon = activity.getResources().getDrawable(
								R.drawable.folder_yellow);
					else if(LoadSetting.users.getIcon()==3)
						icon = activity.getResources().getDrawable(
								R.drawable.folder_yellow2);
					Files ff = new Files();
					ff.setIcon(icon);
					ff.setName(f.getName());
					ff.setFolder(true);
					ff.setPath(f.getCanonicalPath());
					folders.add(ff);
				}
			}
			sort(files,0);
			sort(folders,0);
			list.addAll(folders);
			list.addAll(files);
			refresh();
		}catch (Exception e) {
			e.printStackTrace();
		}
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
						Files f ;
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
								f =new Files();
								f.setName(address.toString());
								list.add(f);
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
