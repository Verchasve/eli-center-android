package com.eli.filemanager;

import org.apache.commons.net.ftp.FTPFile;

import fpt.util.FTPUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FTPLayout extends LinearLayout implements OnItemClickListener {
	ListView lstView;
	TextView back,path;
	FTPFile[] lstFile = FTPUtil.getListFile("/");
	String tempPath="/";
	Context context;
	
	public FTPLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.ftpview, this);
		path=(TextView) findViewById(R.id.path);
		back=(TextView) findViewById(R.id.back);
		lstView = (ListView) findViewById(R.id.lstView);
		path.setText(tempPath);
		lstView.setAdapter(new FTPFileAdapter(lstFile, context));
		lstView.setOnItemClickListener(this);
	}
	
	public void loadListView(){
		System.out.println("tempPath.lastIndexOf(/): "+tempPath.lastIndexOf("/"));
		path.setText(tempPath);
		if(tempPath.lastIndexOf("/")>-1 && !tempPath.equals("/")){
			back.setText("../");
			back.setTextSize(20);
			back.setOnClickListener(backEvent);			
		}else{
			back.setText("/");
			back.setTextSize(20);
		}
		lstFile = FTPUtil.getListFile(tempPath);
		lstView.setAdapter(new FTPFileAdapter(lstFile, context));
		lstView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		//System.out.prln("onItemClick");
		onListItemClick((ListView) arg0, arg0, arg2, arg3);

	}

	private void onListItemClick(ListView l, View v, int position, long id) {
		FTPFile file= lstFile[position];
		if(tempPath.equals("/"))
			tempPath="/"+file.getName();
		else
			tempPath+="/"+file.getName();
		loadListView();
		
	}
	
	OnClickListener backEvent=new OnClickListener() {		
		@Override
		public void onClick(View arg0) {
			int lasIndex=tempPath.lastIndexOf("/");
			if(lasIndex==-1)
				tempPath="/";
			else
				tempPath=tempPath.substring(0,lasIndex);
			loadListView();
		}
	};
	
	public String getPath(){
		return tempPath;
	}

}
