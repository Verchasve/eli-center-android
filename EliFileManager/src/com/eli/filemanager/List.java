package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eli.filemanager.pojo.Files;

public class List extends Activity {
	ArrayList pathArr;
	ListView listFile;
	Button btBack;
	ListFileAdapter listFileAdapter;
	TextView currentFile;
	ArrayList<Files> arr,arrFile,arrFolder;
	
	EditText nameFolder;
	Button btCreatFolder;
	String path = "";
	
	Drawable icon;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        pathArr = new ArrayList();
        btBack = (Button)findViewById(R.id.btBack);
        btBack.setOnClickListener(onBackClick());
        listFile = (ListView) findViewById(R.id.lvFile);
        currentFile = (TextView) findViewById(R.id.tvCurrentFile);
        path = "/";
        getAllListFile(path);
        listFileAdapter = new ListFileAdapter(this, R.layout.list_detail,arr);
        listFile.setAdapter(listFileAdapter);
        nameFolder = (EditText) findViewById(R.id.nameFolder);
        btCreatFolder = (Button) findViewById(R.id.btCreateFolder);
        listFile.setOnItemClickListener(itemClick());
        listFile.setOnItemLongClickListener(itemLongClick());
        
    }
    
    public OnClickListener onBackClick(){
    	OnClickListener onBackClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
//				String pathFolder = "";
				String src = "";
				if(pathArr.size()==0){return;}
				pathArr.remove(pathArr.size()- 1);
				for (int i = 0; i < pathArr.size(); i++) {
					src += pathArr.get(i);
				}
				getAllListFile(src);
				listFileAdapter.clear();
				for (int i = 0; i < arr.size(); i++) {
					listFileAdapter.add(arr.get(i));
				}
				listFileAdapter.notifyDataSetChanged();
			}
		};
		return onBackClick;
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater=getMenuInflater();
	    inflater.inflate(R.menu.option, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		    case R.id.move:
		    	Toast.makeText(this,"Move", 1000).show();
		        break;
		    case R.id.copy:
		    	Toast.makeText(this,"Copy", 1000).show();
		        break;
		    case R.id.delete:
		    	Toast.makeText(this,"Delete", 1000).show();
		        break;
		 	case R.id.rename:
		 		Toast.makeText(this,"Rename", 1000).show();
		        break;
		 	case R.id.newFolder:
	        	Intent intent = new Intent(List.this, NewFolder.class);
	        	startActivity(intent);
	            return true;
		}
		return true;
	}

	public OnItemLongClickListener itemLongClick(){
    	OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Files object = (Files)parent.getItemAtPosition(position);
				if(object.isFolder()){
					List.this.openOptionsMenu();
					System.out.println("Folder");
				}else{
					List.this.openOptionsMenu();
					System.out.println("File");
				}
				return true;
			}
		};
		return longClickListener;
    }
    
    public OnItemClickListener itemClick(){
    	OnItemClickListener clickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Files object = (Files)parent.getItemAtPosition(position);
				if(object.isFolder()){
					String src = "";
					pathArr.add(object.getName());
					System.out.println("size : " + pathArr.size());
					for (int i = 0; i < pathArr.size(); i++) {
						src += pathArr.get(i);
					}
					getAllListFile(src);
					listFileAdapter.clear();
					for (int i = 0; i < arr.size(); i++) {
						listFileAdapter.add(arr.get(i));
					}
					listFileAdapter.notifyDataSetChanged();
				}else{
					Intent intent = object.getAction();
					startActivity(intent);
				}
			}
		};
		return clickListener;
    }
    
    private void getAllListFile(String path){
    	
    	arrFile = new ArrayList<Files>();
    	arrFolder = new ArrayList<Files>();
    	
    	File sdCardRoot = Environment.getExternalStorageDirectory();
    	File dir = new File(sdCardRoot,path);
    	
    	currentFile.setText(dir.getAbsolutePath());    	
    	for(File f :dir.listFiles()){
    		if(f.isFile()){
            	Intent action= new Intent(Intent.ACTION_VIEW);
    			String extend = f.getName().substring(f.getName().length()-5).toLowerCase();
    			if(extend.contains(".txt")){
        			icon = getResources().getDrawable(R.drawable.text_file);
        			action.setDataAndType(Uri.fromFile(f),"text/*");    
    			}else if(extend.contains(".flv")){
        			icon = getResources().getDrawable(R.drawable.video_file);
        			action.setDataAndType(Uri.fromFile(f),"video/*");
    			}else if(extend.contains(".doc") || extend.contains(".docx")){
        			icon = getResources().getDrawable(R.drawable.word_file);
        			action.setDataAndType(Uri.fromFile(f),"text/*");
    			}else if(extend.contains(".zip") || extend.contains(".rar")){
        			icon = getResources().getDrawable(R.drawable.rar_file);
        			action.setDataAndType(Uri.fromFile(f),"video/*");
    			}else if(extend.contains(".jpg") || extend.contains(".jpeg") || extend.contains(".png")){
        			icon = getResources().getDrawable(R.drawable.img_file);
        			action.setDataAndType(Uri.fromFile(f),"image/*");
    			}else if(extend.contains(".apk")){
        			icon = getResources().getDrawable(R.drawable.apk_file);
        			action.setDataAndType(Uri.fromFile(f),"application/vnd.android.package-archive");
    			}else{
        			icon = getResources().getDrawable(R.drawable.unknown_file);
    			}
    			Files ff = new Files();
    			ff.setIcon(icon);
    			ff.setName(f.getName());
    			ff.setFolder(false);
    			ff.setAction(action);
    			arrFile.add(ff);
    		}else if(f.isDirectory()){
    			icon = getResources().getDrawable(R.drawable.folder);
				Files ff = new Files();
				ff.setIcon(icon);
    			ff.setName(f.getName());
    			ff.setFolder(true);
    			arrFolder.add(ff);
    		}
    	}
        sort(arrFile);
        sort(arrFolder);
    	arr = new  ArrayList<Files>();
    	arr.addAll(arrFolder);
    	arr.addAll(arrFile);
    }
    
    private void sort(ArrayList<Files> lst ){
    	Collections.sort(lst, new Comparator<Files>() {
            @Override
            public int compare(Files object1, Files object2) {
                return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
            }
        });

    }
    
    protected void alertbox(String title, String mymessage)
    {
    	new AlertDialog.Builder(this)
	       .setMessage(mymessage)
	       .setTitle(title)
	       .setCancelable(true)
	       .setNeutralButton(android.R.string.cancel,
	          new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dialog, int whichButton){}
	          })
	       .show();
    }
    
}
