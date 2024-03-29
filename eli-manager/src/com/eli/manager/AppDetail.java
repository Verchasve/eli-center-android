package com.eli.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eli.manager.pojo.App;

public class AppDetail extends Activity{
	private static ArrayList<App> listProgram;
	ImageView icon;
	TextView tvAppName,tvAppPkg,tvSize,tvVersion,tvDescription;
	String link;
	int versionCode = 0;
	Button btUpdate,btInstall,btUpdated;	
	ProgressDialog mProgressDialog;
	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		try{
			listProgram = getInstalledApps(false);
			
			tvAppName = (TextView) findViewById(R.id.tvAppName);
			tvAppPkg = (TextView) findViewById(R.id.tvAppPkg);
			tvSize = (TextView) findViewById(R.id.tvSize);
			tvVersion = (TextView) findViewById(R.id.tvVersion);
			tvDescription = (TextView) findViewById(R.id.tvDescription);
			btUpdate = (Button) findViewById(R.id.btUpdate);
			btInstall = (Button) findViewById(R.id.btInstall);
			btUpdated = (Button) findViewById(R.id.btUpdated);
			
			//icon = (ImageView)findViewById(R.id.icon);
			Intent intent = getIntent();
			String[] item = intent.getExtras().getStringArray("item");
			//item = { name,icon,description,link,size,Vname};
			tvAppName.setText(item[0].toString());
			tvAppPkg.setText(intent.getExtras().getString("pkg"));
			tvSize.setText(item[4].toString());
			tvVersion.setText(item[5].toString());
			tvDescription.setText(item[2].toString());
			link = item[3].toString();
			versionCode = intent.getExtras().getInt("verCode");
			
			btInstall.setVisibility(View.VISIBLE);
			btUpdate.setVisibility(View.GONE);
			btUpdated.setVisibility(View.GONE);
			for(int i=0;i<listProgram.size();i++) {
				if(tvAppPkg.getText().equals(listProgram.get(i).getPname())){
					if(versionCode>listProgram.get(i).getVersionCode()){
						btInstall.setVisibility(View.GONE);
						btUpdate.setVisibility(View.VISIBLE);
						btUpdated.setVisibility(View.GONE);
					}else{
						btInstall.setVisibility(View.GONE);
						btUpdate.setVisibility(View.GONE);
						btUpdated.setVisibility(View.VISIBLE);
					}
					break;
				}				
			}
//			Drawable drawable = Drawable.createFromStream(new URL(item[3]).openStream(),null);
//			icon.setImageDrawable(drawable);
			btInstall.setOnClickListener(download());
			btUpdate.setOnClickListener(download());
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
        case DIALOG_DOWNLOAD_PROGRESS:
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Downloading file..");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            return mProgressDialog;
        default:
            return null;
        }
	}

	public OnClickListener download(){
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				downloading();
			}
		};
		return clickListener;
	}
	
	public void downloading(){
		new AsyncTask<String, String, String>(){

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
				try{
					URL url = new URL(link);
			        HttpURLConnection c = (HttpURLConnection) url.openConnection();
			        c.setRequestMethod("GET");
			        c.setDoOutput(true);
			        c.connect();
			        
			        
			        File file = new File(Environment.getExternalStorageDirectory() + "/elicenter/");
					if(!file.exists()){
						file.mkdir();
					}
			        
					file = new File(Environment.getExternalStorageDirectory() + "/elicenter/" + "eli-app.apk");
					if(!file.exists()){
						file.createNewFile();
					}
					
					File outputFile = new File(Environment.getExternalStorageDirectory() + "/elicenter/" + "eli-app.apk");
			        FileOutputStream fos = new FileOutputStream(outputFile);

			        InputStream is = c.getInputStream();

			        byte[] buffer = new byte[1024];
			        
			        long total = 0;
			        int count;
			        int lenghtOfFile = c.getContentLength();
			        while ((count = is.read(buffer)) != -1) {
			            total += count;
			            publishProgress(""+(int)((total*100)/lenghtOfFile));
			            fos.write(buffer, 0, count);
			        }
			 
			        fos.flush();
			        fos.close();
			        is.close();			

			        Intent intentInstall = new Intent(Intent.ACTION_VIEW);
			        intentInstall.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/elicenter/" + "eli-app.apk")),"application/vnd.android.package-archive");
					startActivity(intentInstall);
				}catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		}.execute("");
	}

    private ArrayList<App> getInstalledApps(boolean getSysPackages) {
        ArrayList<App> res = new ArrayList<App>();        
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            App newInfo = new App();
            //newInfo.setAppname(p.applicationInfo.loadLabel(getPackageManager()).toString());
            newInfo.setPname(p.packageName);
            //newInfo.setVersionName(p.versionName);
            newInfo.setVersionCode(p.versionCode);
            //newInfo.setIcon(p.applicationInfo.loadIcon(getPackageManager()));
            res.add(newInfo);
        }
        return res; 
    }
	
}
