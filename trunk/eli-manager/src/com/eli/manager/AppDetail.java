package com.eli.manager;

import java.util.ArrayList;
import java.util.List;

import com.eli.manager.pojo.App;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class AppDetail extends Activity{
	private static ArrayList<App> listProgram;
	ImageView icon;
	TextView tvAppName,tvAppPkg,tvSize,tvVersion,tvDescription;
	String link;
	
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
//			Drawable drawable = Drawable.createFromStream(new URL(item[3]).openStream(),null);
//			icon.setImageDrawable(drawable);

		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
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
