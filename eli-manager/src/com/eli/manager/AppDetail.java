package com.eli.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		try{
			if(listProgram==null){
				listProgram = getInstalledApps(false);
			}
			
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
			tvSize.setText("Size: "+item[4].toString());
			tvVersion.setText("Version: "+item[5].toString());
			tvDescription.setText("Description: "+item[2].toString());
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
	
	public OnClickListener download(){
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW ,Uri.parse(link));
				startActivity(intent);
				Intent intentInstall = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/" + "demo.apk")),"application/vnd.android.package-archive");
				startActivity(intent);
				startActivity(intentInstall);
				System.out.println("ABC");
				intentInstall.setAction(Intent.ACTION_DELETE);
//				finish();
			}
		};
		return clickListener;
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
