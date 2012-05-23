package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FTPFileAdapter extends BaseAdapter{
	FTPFile[] lst=new FTPFile[1];
	Context context;

	public FTPFileAdapter(FTPFile[] lst, Context context) {
		super();
		this.lst = lst;
		this.context = context;
	}

	@Override
	public int getCount() {
		
		return lst.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return lst[arg0];
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.ftpfileadapter, null);
		}
		FTPFile file=lst[position];
		TextView name=(TextView) convertView.findViewById(R.id.ftpFile);
		
		if(file.isDirectory()){
			name.setText(file.getName());
			name.setTextColor(Color.parseColor("#FFFF00"));
		}else{
			name.setText(file.getName());
			name.setTextColor(Color.parseColor("#FFFFFF"));
		}
		
		
		return convertView;
	}

}
