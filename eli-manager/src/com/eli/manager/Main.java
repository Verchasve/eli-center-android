package com.eli.manager;

import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import com.eli.manager.pojo.App;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Main extends Activity {
	
    public static String URL_LIST_PRODUCT = "http://14.63.212.204/eli-android-center/data/auto-update/listitem.xml";

	ListView list_product;
	AppAdapter appAdapter;
	ProgressDialog progress;
	Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		processing(this);
	}
	
	public void processing(Context con){
		this.context = con;
		new AsyncTask<String, Void, Void>() {
			
			@Override
		    protected void onPreExecute() {
		        progress = ProgressDialog.show(context, "", "Loading...", true);
		    }
			
			@Override
			protected Void doInBackground(String... params) {
				list_product = (ListView) findViewById(R.id.list_product);
				appAdapter = new AppAdapter(context, R.layout.display);
				getProduct();
				return null;
			}

			@Override
			protected void onPostExecute(Void arg) {
			    if (progress.isShowing()){
			    	list_product.setAdapter(appAdapter);
			    	setEventClick();
			    	progress.dismiss();
			    }
			}
		}.execute("");
	}

	public void setEventClick(){
		list_product.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list, View view,
					int position, long id) {
				App app = (App) list.getItemAtPosition(position);
				String[] item = { app.getAppname(),app.getIcon(),app.getDescription(),app.getLink(),app.getAppsize()};
				Intent intent = new Intent("com.installandroid.PRODUCTDETAIL");
				intent.putExtra("item", item);
				intent.putExtra("pkg", app.getPname());
				intent.putExtra("verCode", app.getVersionCode());
				startActivity(intent);
			}
		});
	}
	
	public void getProduct() {
		try {
			XmlPullParser xmlPullParser = Xml.newPullParser();

			URL url = new URL(URL_LIST_PRODUCT);
			xmlPullParser.setInput(url.openStream(), "UTF-8");

			App app = null;
			int eventType;
			String name, size, icon, version, link, description,pkg;
			int versionCode=0;
			name = size = icon = version = link = description = pkg = "";
			while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					if (xmlPullParser.getName().equals("name")) {
						name = xmlPullParser.nextText();
					}else if (xmlPullParser.getName().equals("package")) {
						pkg = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("size")) {
						size = xmlPullParser.nextText();
//					} else if (xmlPullParser.getName().equals("icon")) {
//						icon = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("version")) {
						version = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("version-code")) {
						versionCode = Integer.parseInt(xmlPullParser.nextText());						
					} else if (xmlPullParser.getName().equals("link")) {
						link = xmlPullParser.nextText();
					} else if (xmlPullParser.getName().equals("description")) {
						description = xmlPullParser.nextText();
						app = new App();
						app.setAppname(name);
						app.setPname(pkg);
						app.setAppsize(size);
						//app.setIcon(icon);
						app.setVersionName(version);
						app.setVersionCode(versionCode);
						app.setLink(link);
						app.setDescription(description);
						appAdapter.add(app);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
}