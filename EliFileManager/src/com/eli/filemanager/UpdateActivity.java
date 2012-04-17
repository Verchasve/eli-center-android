package com.eli.filemanager;

import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Xml;

import com.eli.filemanager.pojo.App;

public class UpdateActivity extends Activity {
	public static String URL_LIST_PRODUCT = "http://14.63.212.204/eli-android-center/data/auto-update/listitem.xml";
	private ArrayList<App> listApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update);
		if(isLastVersion()){
			Intent intent = new Intent(this,ListActivity.class);
			startActivity(intent);
			finish();
		}else{
			
		}
	}

	private boolean isLastVersion(){
		try{
		getProduct();
		PackageInfo manager=getPackageManager().getPackageInfo(getPackageName(), 0);
		for(App a: listApp){
			if(a.getPname().equals(manager.packageName)){
				if(a.getVersionCode()>manager.versionCode){
					return false;
				}
			}
		}
		}catch (Exception e) {
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
