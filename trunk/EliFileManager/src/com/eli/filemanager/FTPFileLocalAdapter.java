package com.eli.filemanager;

import java.io.File;

import org.apache.commons.net.ftp.FTPFile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FTPFileLocalAdapter extends BaseAdapter implements OnItemClickListener {
	File[] lstfile = new File[1];
	Context context;
	
	public FTPFileLocalAdapter(File[] lst, Context context){
		super();
		this.lstfile=lst;
		this.context=context;
	}
	
	
	
	public File[] getLstfile() {
		return lstfile;
	}



	public void setLstfile(File[] lstfile) {
		this.lstfile = lstfile;
	}



	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lstfile.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return lstfile[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.ftpfilelocaladapter, null);
		}
		File file=lstfile[position];
		TextView name=(TextView) convertView.findViewById(R.id.ftpfilelocal);
		
		if(file.isDirectory()){
			name.setText(file.getName());
			name.setTextColor(Color.parseColor("#FFFF00"));
		}else{
			name.setText(file.getName());
			name.setTextColor(Color.parseColor("#FFFFFF"));
		}
		return convertView;
	}

	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
	
	

}
