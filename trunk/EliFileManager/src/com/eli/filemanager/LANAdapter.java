package com.eli.filemanager;

import java.util.ArrayList;

import com.eli.filemanager.pojo.Files;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LANAdapter extends ArrayAdapter<Files>{

	LayoutInflater inflator;
	int layout;
	ArrayList<Files> array;
	Context context;
	Drawable drawable;
	
	public LANAdapter(Context context, int layout,
			ArrayList<Files> array) {
		super(context, layout, array);
		inflator = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layout;
		this.array = array;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null){
			view = inflator.inflate(layout, null);
		}
		
		Files ip = array.get(position);
		TextView iptv = (TextView)view.findViewById(R.id.ip);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		try{
			drawable = ip.getIcon();
			icon.setImageDrawable(drawable);
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		iptv.setText(ip.getName());
		return super.getView(position, convertView, parent);
	}
}
