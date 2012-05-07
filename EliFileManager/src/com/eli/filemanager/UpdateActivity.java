package com.eli.filemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Xml;
import android.widget.TextView;

import com.eli.filemanager.pojo.App;
import com.eli.util.Util;

public class UpdateActivity extends Activity {
	public static String URL_LIST_PRODUCT = "http://14.63.212.204/eli-android-center/data/auto-update/listitem.xml";
	private ArrayList<App> listApp;
	ProgressDialog mProgressDialog;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	String link = "";
	TextView version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		version = (TextView) findViewById(R.id.tvVersion);
		try {
			PackageInfo manager = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version.setText("Version "+manager.versionName);
			
			Thread timer = new Thread(){
				public void run(){
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						check();
					}
				}
			};
			timer.start();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void check(){
		if (isLastVersion()) {
			nextToListActivity();
		} else {
			Looper.prepare();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Update new version");
			builder.setMessage("A new version of EliFileManager is available for update\n"
					+ "Would you like to update ?");
			builder.setPositiveButton("Ok",
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							downloading();
						}
					});
			builder.setNegativeButton("Cancel",
					new AlertDialog.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							nextToListActivity();
						}
					});
			builder.show();
			Looper.loop();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getString(R.string.downloading) + "...");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	public void nextToListActivity() {
		Intent intent = new Intent(this, ListActivity.class);
		startActivity(intent);
		finish();
	}

	public void downloading() {
		new AsyncTask<String, String, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				showDialog(DIALOG_DOWNLOAD_PROGRESS);
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setProgress(Integer.parseInt(progress[0]));
			}

			@Override
			protected void onPostExecute(String unused) {
				dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
				finish();
			}

			@Override
			protected String doInBackground(String... arg0) {
				try {
					URL url = new URL(link);
					HttpURLConnection c = (HttpURLConnection) url
							.openConnection();
					c.setRequestMethod("GET");
					c.setDoOutput(true);
					c.connect();

					File file = new File(
							Environment.getExternalStorageDirectory()
									+ "/elicenter/");
					if (!file.exists()) {
						file.mkdir();
					}

					file = new File(Environment.getExternalStorageDirectory()
							+ "/elicenter/" + "eli-app.apk");
					if (!file.exists()) {
						file.createNewFile();
					}

					File outputFile = new File(
							Environment.getExternalStorageDirectory()
									+ "/elicenter/" + "eli-app.apk");
					FileOutputStream fos = new FileOutputStream(outputFile);

					InputStream is = c.getInputStream();

					byte[] buffer = new byte[1024];

					long total = 0;
					int count;
					int lenghtOfFile = c.getContentLength();
					while ((count = is.read(buffer)) != -1) {
						total += count;
						publishProgress(""
								+ (int) ((total * 100) / lenghtOfFile));
						fos.write(buffer, 0, count);
					}

					fos.flush();
					fos.close();
					is.close();

					Intent intentInstall = new Intent(Intent.ACTION_VIEW);
					intentInstall.setDataAndType(Uri.fromFile(new File(
							Environment.getExternalStorageDirectory()
									+ "/elicenter/" + "eli-app.apk")),
							"application/vnd.android.package-archive");
					startActivity(intentInstall);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute("");
	}

	private boolean isLastVersion() {
		try {
			getProduct();
			PackageInfo manager = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			for (App a : listApp) {
				if (a.getPname().equals(manager.packageName)) {
					if (a.getVersionCode() > manager.versionCode) {
						link = a.getLink();
						return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void getProduct() {
		try {
			listApp = new ArrayList<App>();
			XmlPullParser xmlPullParser = Xml.newPullParser();

			URL url = new URL(URL_LIST_PRODUCT);
			xmlPullParser.setInput(url.openStream(), "UTF-8");

			App app = null;
			int eventType;
			String name, size, icon, version, link, description, pkg;
			int versionCode = 0;
			name = size = icon = version = link = description = pkg = "";
			while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xmlPullParser.getName().equals("name")) {
						name = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("package")) {
						pkg = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("size")) {
						size = xmlPullParser.nextText();
						// } else if (xmlPullParser.getName().equals("icon")) {
						// icon = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("version")) {
						version = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("version-code")) {
						versionCode = Integer
								.parseInt(xmlPullParser.nextText());
					} else if (xmlPullParser.getName().equals("link")) {
						link = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("description")) {
						description = xmlPullParser.nextText();
						app = new App();
						app.setAppname(name);
						app.setPname(pkg);
						app.setAppsize(size);
						// app.setIcon(icon);
						app.setVersionName(version);
						app.setVersionCode(versionCode);
						app.setLink(link);
						app.setDescription(description);
						listApp.add(app);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
