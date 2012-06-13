package com.eli.filemanager;

import java.util.ArrayList;

import com.eli.filemanager.pojo.Files;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothAdapters extends ArrayAdapter<Files> {

	LayoutInflater inflator;
	int layout;
	ArrayList<Files> array;
	Context context;
	Drawable drawable;
	
	public BluetoothAdapters(Context context, int layout,
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
		
		Files f = array.get(position);
		TextView namedv = (TextView)view.findViewById(R.id.tvNameDeviceBluetooth);
		TextView address = (TextView)view.findViewById(R.id.tvAddressDevice);
		namedv.setText(f.getName());
		address.setText(f.getBluetooth().getAddress());
		return view;
	}

}
