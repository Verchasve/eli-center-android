package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FolderLayout extends LinearLayout implements OnItemClickListener {

	Context context;
	IFolderItemListener folderListener;
//	private List<String> item = null;
	private List<String> path = null;
	private String root = "/mnt/sdcard";
	private TextView myPath,back;
	private ListView lstView;
	FTPFileLocalAdapter ad;
	String tempPath="/mnt/sdcard";

	public FolderLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

		// TODO Auto-generated constructor stub
		this.context = context;

		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.folderview, this);
		back=(TextView) findViewById(R.id.back);
		myPath = (TextView) findViewById(R.id.path);
		lstView = (ListView) findViewById(R.id.list);

		Log.i("FolderView", "Constructed");
		getDir(root, lstView);
		
	}

	public void setIFolderItemListener(IFolderItemListener folderItemListener) {
		this.folderListener = folderItemListener;
	}

	// Set Directory for view at anytime
	public void setDir(String dirPath) {
		getDir(dirPath, lstView);
	}

	private void getDir(String dirPath, ListView v) {

		myPath.setText(dirPath);
//		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (!dirPath.equals(root)) {
			back.setText("../");
			back.setText("../");
			back.setTextSize(20);
			back.setOnClickListener(backEvent);
			
//			item.add(root);
//			path.add(root);
//			item.add("../");
//			path.add(f.getParent());
			
		}else{
			back.setText("/");
			back.setTextSize(20);
		}
//		for (int i = 0; i < files.length; i++) {
//			File file = files[i];
//			path.add(file.getPath());
//			if (file.isDirectory()){
//				item.add(file.getName() + "/");
//			}else
//				item.add(file.getName());
//
//		}

		Log.i("Folders", files.length + "");

//		setItemList(item);
		
		setItemList(files,v);
	}

	// can manually set Item to display, if u want
//	public void setItemList(List<String> item) {
//		ArrayAdapter<String> fileList = new ArrayAdapter<String>(context,
//				R.layout.row, item);
//		lstView.setAdapter(fileList);
//		lstView.setOnItemClickListener(this);
//	}
	
	public void setItemList(File[] lst,ListView v) {
		ad = new FTPFileLocalAdapter(lst, context);
		v.setAdapter(ad);
		v.setOnItemClickListener(this);
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
//		File file = new File(path.get(position));
		File file = ad.getLstfile()[position];
		if (file.isDirectory()) {
			if (file.canRead())
//				getDir(path.get(position), l);
				getDir(ad.getLstfile()[position].getPath(), l);
			else {
				// what to do when folder is unreadable
				if (folderListener != null) {
					folderListener.OnCannotFileRead(file);

				}

			}
		} else {

			// what to do when file is clicked
			// You can add more,like checking extension,and performing separate
			// actions
			if (folderListener != null) {
				folderListener.OnFileClicked(file);
			}

		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		onListItemClick((ListView) arg0, arg0, arg2, arg3);
	}

	public List<String> getPath() {
		return path;
	}

	public void setPath(List<String> path) {
		this.path = path;
	}

	public TextView getMyPath() {
		return myPath;
	}

	public void setMyPath(TextView myPath) {
		this.myPath = myPath;
	}
	
	OnClickListener backEvent=new OnClickListener() {		
		@Override
		public void onClick(View arg0) {
			int lasIndex=myPath.getText().toString().lastIndexOf("/");
			System.out.println("lastindex; "+lasIndex);
			if(lasIndex<=4)
				tempPath="/mnt/sdcard";
			else
				tempPath=myPath.getText().toString().substring(0,lasIndex);
			
			getDir(tempPath, lstView);
		}
	};

}
