package com.eli.filemanager;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LstPathViewAdapter extends BaseAdapter{

	File[] list = new File[1];
	
	public File[] getList() {
		return list;
	}

	public void setList(File[] list) {
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
