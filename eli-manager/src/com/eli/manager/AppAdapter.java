package com.eli.manager;

import java.net.URL;

import com.eli.manager.pojo.App;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AppAdapter extends ArrayAdapter<App>{

	LayoutInflater layoutInflater;
	int layoutID;
	
	public AppAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutID = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null){
			view = layoutInflater.inflate(layoutID, null);
		}
//		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		TextView name = (TextView)view.findViewById(R.id.product_name);
		TextView version = (TextView)view.findViewById(R.id.version);
		
		App app = getItem(position);
		name.setText(app.getAppname());
		version.setText("version : " + app.getVersionName());
//		try{
//			Drawable drawable = Drawable.createFromStream(new URL(app.getIcon()).openStream(),null);
//			icon.setImageDrawable(drawable);
//		}catch (Exception e) {
//			e.printStackTrace(System.out);
//		}
		return view;
	}

	
}
