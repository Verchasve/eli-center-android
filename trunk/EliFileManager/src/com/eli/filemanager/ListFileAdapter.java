package com.eli.filemanager;

import java.io.File;
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
	boolean isMultiSelect = false;
	private ArrayList<String> positions = new ArrayList<String>();
	private boolean isListView = true;
	
	ArrayList<File> fileFavorite = new ArrayList<File>();
	
	public boolean isListView() {
		return isListView;
	}

	public void setListView(boolean isListView) {
		this.isListView = isListView;
	}

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

	public ListFileAdapter(Context context, int textViewResourceId,ArrayList<Files> lst, boolean msl, ArrayList<String> pst, boolean isLV, ArrayList<File> fileFavorite) {
		super(context, textViewResourceId,lst);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = textViewResourceId;
		arr = lst;
		isMultiSelect = msl;
		positions = pst;
		isListView = isLV;
		this.fileFavorite = fileFavorite;
	}
	
	public ListFileAdapter(Context context, int textViewResourceId,ArrayList<Files> lst, boolean isLV, ArrayList<File> fileFavorite) {
		super(context, textViewResourceId,lst);
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = textViewResourceId;
		arr = lst;
		isListView = isLV;
		this.fileFavorite = fileFavorite;
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
		
		ImageView iconBookmark = (ImageView) view.findViewById(R.id.ivBookmark);
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
			if(isListView){
				if(isMultiSelect){
					iconCheck.setVisibility(ImageView.VISIBLE);
					iconBookmark.setVisibility(ImageView.GONE);
					if(positions != null && positions.contains(files.getName())){
						iconCheck.setImageDrawable(view.getResources().getDrawable(R.drawable.checkbox_checked));
					} else {
						iconCheck.setImageDrawable(view.getResources().getDrawable(R.drawable.checkbox_unchecked));
					}
				} else {
					iconCheck.setVisibility(ImageView.GONE);
					boolean isCheck = false;
					for (File ifile : fileFavorite) {
						if (ifile.getAbsolutePath().equals(files.getPath())) {
							isCheck = true;
							break;
						}
					}
					if(isCheck){
						iconBookmark.setVisibility(ImageView.VISIBLE);
					} else {
						iconBookmark.setVisibility(ImageView.GONE);
					}
				}
			} else {
				iconCheck.setVisibility(ImageView.GONE);
				iconBookmark.setVisibility(ImageView.GONE);
				if(isMultiSelect){
					if(positions != null && positions.contains(files.getName())){
						iconCheck.setVisibility(ImageView.VISIBLE);
						iconCheck.setImageDrawable(view.getResources().getDrawable(R.drawable.check));
					}
				} else {
					iconCheck.setVisibility(ImageView.GONE);
					boolean isCheck = false;
					for (File ifile : fileFavorite) {
						if (ifile.getName().equals(files.getName())) {
							isCheck = true;
							break;
						}
					}
					if(isCheck){
						iconBookmark.setVisibility(ImageView.VISIBLE);
					} else {
						iconBookmark.setVisibility(ImageView.GONE);
					}
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return view;
	}

}
