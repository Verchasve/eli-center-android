package com.eli.filemanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eli.filemanager.pojo.Files;
import com.eli.util.Util;

public class ProcessLAN {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	ProgressDialog mProgressDialog;
	LANActivity activity;
	GridView gridview;
	ExtendAdapter adapter;
	String absoluteIP, username, password;
	boolean flag = true; // flag == true la scan all, false la absolute
	ArrayList<String> paths;
	String path;
	NtlmPasswordAuthentication auth;
	SmbFile dirRoot;
	SmbFile[] childsRoot;
	Drawable drawable;
	TextView address;

	// File smb
	ArrayList<Files> files, folders, list, tempListComputer;
	Drawable icon;
	ArrayList<String> listFileCopy = new ArrayList<String>();

	public ProcessLAN(LANActivity activity) {
		this.activity = activity;
		initObject();
	}

	public void initObject() {
		list = new ArrayList<Files>();
		files = new ArrayList<Files>();
		folders = new ArrayList<Files>();
		address = (TextView) activity.findViewById(R.id.idAddress);
		gridview = (GridView) activity.findViewById(R.id.gridview);
		gridview.setOnItemClickListener(itemClick());
		gridview.setOnItemLongClickListener(itemLongClick());
		paths = new ArrayList<String>();
		auth = new NtlmPasswordAuthentication(null, null, null);
		
		adapter = new ExtendAdapter(activity, R.layout.extenddetail, list);
		gridview.setAdapter(adapter);
		address.setText("Choose Machine to connect");
	}

	public String getPath() {
		path = "";
		for (String a : paths) {
			path +=  a+File.separator;
		}
		return path;
	}

	public void backButton() {
		if(flag){

			System.out.println(paths.size());
			if (paths.size() > 1) {
				paths.remove(paths.size() - 1);
				loginToSharedFolder(username, password);
			}else{
				list = tempListComputer;
				refresh();
			}
		}else{
			if (paths.size() > 1) {
				paths.remove(paths.size() - 1);
				loginToSharedFolder(username, password);
			}
		}
		
	}

	private OnItemClickListener itemClick() {
		OnItemClickListener action = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Files object = (Files) arg0.getItemAtPosition(arg2);		
				if(object.isFolder()){
					paths.add(object.getName());
					try {
						loginToSharedFolder(username, password);
						address.setText(getPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		return action;
	}

	private void sort(ArrayList<Files> lst, int sort) {

		switch (sort) {
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
					return object1.getModified().compareTo(
							object2.getModified());
				}
			});
			break;
		}
	}

	public void scanAll() {
		flag = true;
		absoluteIP = "";
		paths = new ArrayList<String>();
		processScan();
	}

	public void refresh() {
		adapter = new ExtendAdapter(activity, R.layout.extenddetail, list);
		gridview.setAdapter(adapter);
	}

	public void breakDuringScan() {
		adapter = new ExtendAdapter(activity, R.layout.extenddetail, list);
		gridview.setAdapter(adapter);
	}

	public void scanAbsoluteIP() {
		flag = false;
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
					paths.add(absoluteIP);
					checkValidIP(absoluteIP);
				}
			}
		});
		builder.show();
	}

	public void checkValidIP(String ip) {
		try {
			SmbFile smbFile = new SmbFile("smb://" + ip + "", auth);
			SmbFile[] childs = smbFile.listFiles();
			if (childs.length > 0) {
				analyzeListSMB(childs);
			}
		} catch (Exception e) {
			e.printStackTrace();
			try {
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);

				final LinearLayout root = new LinearLayout(activity);
				root.setLayoutParams(lp);
				root.setOrientation(LinearLayout.VERTICAL);

				final EditText usernameEt = new EditText(activity);
				final EditText passwordEt = new EditText(activity);

				usernameEt.setLayoutParams(lp);
				usernameEt.setLines(1);
				usernameEt.setSingleLine(true);
				usernameEt.setHint("Username");
				passwordEt.setLayoutParams(lp);
				passwordEt.setLines(1);
				passwordEt.setSingleLine(true);
				passwordEt.setHint("Password");

				root.addView(usernameEt);
				root.addView(passwordEt);

				builder.setView(root);
				builder.setTitle("Authentication");
				builder.setNegativeButton("Cancel", null);
				builder.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String user = usernameEt.getText().toString();
								String pass = passwordEt.getText().toString();
								if (user == null) {
									user = "";
								}
								if (pass == null) {
									pass = "";
								}
								username = user;
								password = pass;
								loginToSharedFolder(user, pass);
							}
						});
				builder.show();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void loginToSharedFolder(String username, String password) {
		try {
			auth = new NtlmPasswordAuthentication(null, username, password);
			System.out.println("Path: "+getPath());
			dirRoot = new SmbFile("smb://" +getPath(), auth);
			childsRoot = dirRoot.listFiles();
			if (childsRoot.length > 0) {
				analyzeListSMB(childsRoot);
			}
		} catch (Exception e) {
			e.printStackTrace();
			checkValidIP(absoluteIP);
		}
	}

	public void analyzeListSMB(final SmbFile[] childs) {
		list = new ArrayList<Files>();
		asyn = new AsyncTask<String, String, Void>() {
			@Override
			protected void onPreExecute() {
				mProgressDialog = ProgressDialog.show(activity, "",
						"Loading...", true);
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setMessage(progress[0]);
			}

			@Override
			protected Void doInBackground(String... params) {
				publishProgress("Getting files");
				try {
					list = new ArrayList<Files>();
					files = new ArrayList<Files>();
					folders = new ArrayList<Files>();
					for (SmbFile f : childs) {
						if (f.isFile()) {
							Bitmap bitmap = null;
							if (Util.checkExtendFile(f.getName(), ".txt")
									|| Util.checkExtendFile(f.getName(), ".csv")) {
								icon = activity.getResources().getDrawable(
										R.drawable.text_file);
							} else if (Util.checkExtendFile(f.getName(), ".xml")) {
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
							ff.setChildFile(ff.getSize() / (1024) + " KB | "
									+ Util.format1.format(ff.getModified()));
							ff.setPath(f.getCanonicalPath());
							files.add(ff);
						} else if (f.isDirectory()) {
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
					sort(files, 0);
					sort(folders, 0);
					list.addAll(folders);
					list.addAll(files);
					
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
		};
		asyn.execute("");
	}

	AsyncTask<String, String, Void> asyn;
	boolean running = true;

	public void processScan() {
		list = new ArrayList<Files>();
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
						refresh();
					}
				});
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setMessage(progress[0]);
			}

			@Override
			protected Void doInBackground(String... params) {
				try {
					publishProgress("Scanning");
					if (running) {
						InetAddress localhost = null;
						for (Enumeration<NetworkInterface> en = NetworkInterface
								.getNetworkInterfaces(); en.hasMoreElements();) {
							NetworkInterface intf = en.nextElement();
							for (Enumeration<InetAddress> enumIpAddr = intf
									.getInetAddresses(); enumIpAddr
									.hasMoreElements();) {
								InetAddress inetAddress = enumIpAddr.nextElement();
								if (!inetAddress.isLoopbackAddress()) {
									localhost = inetAddress;
								}
							}
						}
						byte[] ip = localhost.getAddress();
						Files files = null;
						for (int i = 1; i <= 254; i++) {
							if(running){
								System.out.println(running);
								ip[3] = (byte) i;
								InetAddress address = InetAddress.getByAddress(ip);
								System.out.println("I: "+i);
								if (address.isReachable(1100)) {
									files = new Files();
									String temp = address.toString();
									temp = temp.substring(1,temp.length());
									files.setName(temp);
									files.setFolder(true);
									drawable = activity.getResources().getDrawable(R.drawable.computer);
									files.setIcon(drawable);
									list.add(files);
									tempListComputer = list;
									System.out.println(address + " machine is turned on and can be pinged");
								}
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
	
	public void addFileDownload(String pathFile){
		if(listFileCopy.contains(pathFile)){
			listFileCopy.remove(pathFile);
		} else {
			listFileCopy.add(pathFile);
		}
	}
	
	public void downloadFile(){
		if(listFileCopy.size() == 0){
			Toast.makeText(activity, "No file download!", Toast.LENGTH_SHORT);
		}
		File fParent = new File("/mnt/sdcard/download");
		if(!fParent.exists()){
			fParent.mkdir();
		}
		for (String fileName : listFileCopy) {
			try {
				SmbFile smbFile = new SmbFile(fileName);
				if(smbFile.isFile()){
					File fileDownload = new File("/mnt/sdcard/download", smbFile.getName());
					copyFile(smbFile, fileDownload);
					Toast.makeText(activity, "Download file successfull", Toast.LENGTH_SHORT).show();
				} else if(smbFile.isDirectory()){
					File fileDownload = new File("/mnt/sdcard/download", smbFile.getName());
					copyDirectory(smbFile, fileDownload);
					Toast.makeText(activity, "Download file successfull", Toast.LENGTH_SHORT).show();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (SmbException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		refresh();
	}

	public OnItemLongClickListener itemLongClick() {
		OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Files object = (Files) parent.getItemAtPosition(position);
				openOptionDialog(object.getName());
				return true;
			}
		};
		return longClickListener;
	}

	public void openOptionDialog(final String name) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					activity);
			builder.setTitle("Option");
			builder.setItems(R.array.option_arr_lan,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case 0:
								String urlFile = "smb://" + absoluteIP + getPath() + name;
								addFileDownload(urlFile);
								downloadFile();
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

	// copy file
	public void copyFile(SmbFile fileSource, File filePaste)
			throws IOException {
		FileOutputStream out = new FileOutputStream(filePaste);
		SmbFileInputStream in = new SmbFileInputStream(fileSource);
		byte[] buf = new byte[1024];
		in.read(buf);
		do {
			out.write(buf);
		} while (in.read(buf) != -1);
	}

	// copy directory
	public void copyDirectory(SmbFile fileSource, File filePaste)
			throws IOException {
		if (fileSource.isDirectory()) {
			if (!filePaste.exists()) {
				filePaste.mkdirs();
			}
			String[] children = fileSource.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new SmbFile(fileSource, children[i]), new File(filePaste, children[i]));
			}
		} else {
			copyFile(fileSource, filePaste);
		}
	}
	public void switchTo() {
		Intent intent = new Intent(activity,ListActivity.class);
		activity.startActivity(intent);
		activity.finish();
	}
}
