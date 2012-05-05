package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListBookmarkAdapter extends ArrayAdapter<File> {

	LayoutInflater layoutInflater;
	int layout;
	ArrayList<File> arr;
	
	public ListBookmarkAdapter(Context context, int textViewResourceId,ArrayList<File> lst) {
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
		File file = getItem(position);

		TextView name = (TextView) view.findViewById(R.id.bkFileName);
		TextView child = (TextView) view.findViewById(R.id.bkFilePath);
		try{
			child.setText(file.getAbsolutePath());
			name.setText(file.getName());
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return view;
	}
}
