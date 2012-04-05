package com.eli.update;

import java.net.URL;

import com.eli.update.pojo.Product;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListUpdateAdapter extends ArrayAdapter<Product>{
	LayoutInflater layoutInflater;
	int layoutId;
	
	public ListUpdateAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if(view == null){
			view = layoutInflater.inflate(layoutId, null);
		}
		TextView name = (TextView)view.findViewById(R.id.tvName);
		TextView version = (TextView)view.findViewById(R.id.tvVersion);
		ImageView icon = (ImageView)view.findViewById(R.id.ivIcon);
		
		Product p = getItem(position);
		
		name.setText(p.getName());
		version.setText(p.getVersion());
		try{
			Drawable dr = Drawable.createFromStream(new URL(p.getIcon()).openStream(),null);
			icon.setImageDrawable(dr);
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return view;
	}
}
