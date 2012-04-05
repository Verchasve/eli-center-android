package com.eli.update;

import java.net.URL;

import org.xmlpull.v1.XmlPullParser;

import com.eli.update.pojo.Product;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.ListView;

public class ListUpdate extends Activity{
	final String XML_URL="http://14.63.212.204/eli-android-center/data/auto-update/listitem.xml";
	ListUpdateAdapter listProduct;
	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listupdate);		
		listView = (ListView) findViewById(R.id.lvListProduct);
		listProduct = new ListUpdateAdapter(this, R.layout.list);
		getAllXML();
	}
	
	private void getAllXML(){
		try {
			XmlPullParser xmlPull = Xml.newPullParser();
			
			URL url = new URL(XML_URL);
			xmlPull.setInput(url.openStream(),"UTF-8");
			
			int eventType;
			String id,name,size,version,link,description,icon;
			id=name=size=version=link=description=icon="";
			
			while((eventType = xmlPull.next()) != XmlPullParser.END_DOCUMENT){
				if(eventType == XmlPullParser.START_TAG){
					if(xmlPull.getName().equals("id")){
						id = xmlPull.nextText();
					}else if(xmlPull.getName().equals("name")){
						name = xmlPull.nextText();
					}else if(xmlPull.getName().equals("size")){
						size = xmlPull.nextText();
					}else if(xmlPull.getName().equals("icon")){
						icon = xmlPull.nextText();
					}else if(xmlPull.getName().equals("version")){
						version = xmlPull.nextText();
					}else if(xmlPull.getName().equals("link")){
						link = xmlPull.nextText();
					}else if(xmlPull.getName().equals("description")){
						description=  xmlPull.nextText();
						listProduct.add(new Product(id,name,Float.parseFloat(size),icon,version,link,description));
					}
				}
			}
			System.out.println(listProduct.getCount());
			listView.setAdapter(listProduct);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}
}
