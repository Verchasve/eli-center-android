package com.eli.manager;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.eli.manager.pojo.App;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class AppDetail extends Activity{
	private static ArrayList<App> listProgram;
	ImageView icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		try{
			if(listProgram==null){
				listProgram = getInstalledApps(false);
			}
			
			//icon = (ImageView)findViewById(R.id.icon);
			Intent intent = getIntent();
			String[] item = intent.getExtras().getStringArray("item");
			
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
