package com.eli.filemanager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eli.filemanager.pojo.Files;

public class ListFileAdapter extends ArrayAdapter<Files>{

	LayoutInflater layoutInflater;
	int layout;
	ArrayList<Files> arr;
	
	public ListFileAdapter(Context context, int textViewResourceId,ArrayList<Files> lst) {
		super(context, textViewResourceId,lst);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = textViewResourceId;
		arr = lst;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null){
			view = layoutInflater.inflate(layout, null);
		}		
		ImageView icon = (ImageView) view.findViewById(R.id.ivFileImg);
		TextView name = (TextView) view.findViewById(R.id.tvFileName);		
		TextView child = (TextView) view.findViewById(R.id.tvChild);
		Files files = getItem(position);
		try{
			icon.setImageDrawable(files.getIcon());
			name.setText(files.getName());
			child.setText(files.getChildFile());
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}	
		return view;
	}

}
