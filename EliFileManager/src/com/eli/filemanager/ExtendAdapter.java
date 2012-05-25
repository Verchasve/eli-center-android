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

public class ExtendAdapter extends ArrayAdapter<Files>{

	LayoutInflater inflator;
	int layout;
	ArrayList<Files> array;
	Context context;
	Drawable drawable;
	
	public ExtendAdapter(Context context, int layout,
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
		
		Files item = array.get(position);
		TextView iptv = (TextView)view.findViewById(R.id.name);
		ImageView icon = (ImageView)view.findViewById(R.id.icon);
		try{
			drawable = item.getIcon();
			icon.setImageDrawable(drawable);
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		iptv.setText(item.getName());
		return view;
	}
}
