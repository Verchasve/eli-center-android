package com.eli.filemanager;

import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LANAdapter extends ArrayAdapter<String>{

	LayoutInflater inflator;
	int layout;
	ArrayList<String> array;
	Context context;
	public LANAdapter(Context context, int layout,
			ArrayList<String> array) {
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
		
		String ip = array.get(position);
		TextView iptv = (TextView)view.findViewById(R.id.ip);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		try{
			Drawable drawable = context.getResources().getDrawable(R.drawable.computer);
			icon.setImageDrawable(drawable);
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		iptv.setText(ip);
		return super.getView(position, convertView, parent);
	}
}
