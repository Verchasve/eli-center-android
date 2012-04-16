package com.eli.filemanager;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.DropBoxManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eli.filemanager.pojo.Files;

public class ListFileAdapter extends ArrayAdapter<Files>{

	LayoutInflater layoutInflater;
	int layout;
	ArrayList<Files> arr;
	boolean isMultiSelect = false;
	private ArrayList<String> positions = new ArrayList<String>();
	
	public boolean isMultiSelect() {
		return isMultiSelect;
	}

	public void setMultiSelect(boolean isMultiSelect) {
		this.isMultiSelect = isMultiSelect;
	}

	public ArrayList<String> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<String> positions) {
		this.positions = positions;
	}

	public ListFileAdapter(Context context, int textViewResourceId,ArrayList<Files> lst, boolean msl, ArrayList<String> pst) {
		super(context, textViewResourceId,lst);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = textViewResourceId;
		arr = lst;
		isMultiSelect = msl;
		positions = pst;
	}
	
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
		Files files = getItem(position);

		ImageView icon = (ImageView) view.findViewById(R.id.ivFileImg);
		TextView name = (TextView) view.findViewById(R.id.tvFileName);
		ImageView iconCheck = (ImageView) view.findViewById(R.id.ivCheckImage);
		TextView child = null;
		try{
			icon.setImageDrawable(files.getIcon());
			if(view.findViewById(R.id.tvChild) != null){
				child = (TextView) view.findViewById(R.id.tvChild);
				child.setText(files.getChildFile());
			}
			name.setText(files.getName());
			if(isMultiSelect){
				iconCheck.setVisibility(ImageView.VISIBLE);
				if(positions != null && positions.contains(files.getName())){
					iconCheck.setImageDrawable(view.getResources().getDrawable(R.drawable.checkbox_checked));
				} else {
					iconCheck.setImageDrawable(view.getResources().getDrawable(R.drawable.checkbox_unchecked));
				}
			} else {
				iconCheck.setVisibility(ImageView.GONE);
			}
			
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return view;
	}

}
