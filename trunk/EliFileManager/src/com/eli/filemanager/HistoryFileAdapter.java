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

public class HistoryFileAdapter extends ArrayAdapter<Files>{

	LayoutInflater layoutInflater;
	int layoutId;
	ArrayList<Files> array;
	 
	public HistoryFileAdapter(Context context, int textViewResourceId, ArrayList<Files> array) {
		super(context,textViewResourceId,array);
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutId = textViewResourceId;
		this.array = array; 
	}
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {        
		View v = convertView;
		if(v == null){
			v = layoutInflater.inflate(layoutId, null);
		}
		
		Files files = array.get(position);
        
        ImageView iv = (ImageView) v.findViewById(R.id.historyIcon);
        TextView tv = (TextView) v.findViewById(R.id.historyText);
        iv.setImageDrawable(files.getIcon());      
        tv.setText(files.getName());
        return v;
    }
}
