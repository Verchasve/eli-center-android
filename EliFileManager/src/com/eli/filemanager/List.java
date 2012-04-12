package com.eli.filemanager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
	File fileCopy;
	File fileMove;
	String fileName;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        pathArr = new ArrayList();
        btBack = (Button)findViewById(R.id.btBack);
        btBack.setOnClickListener(onBackClick());
        listFile = (ListView) findViewById(R.id.lvFile);
        currentFile = (TextView) findViewById(R.id.tvCurrentFile);
        getAllListFile("/");
        listFileAdapter = new ListFileAdapter(this, R.layout.list_detail,arr);
        listFile.setAdapter(listFileAdapter);
        nameFolder = (EditText) findViewById(R.id.nameFolder);
        btCreatFolder = (Button) findViewById(R.id.btCreateFolder);
        listFile.setOnItemClickListener(itemClick());
        listFile.setOnItemLongClickListener(itemLongClick());
    }
    
    public void refresh(){
    	try{
    		String src = "";
			System.out.println("size : " + pathArr.size());
			for (int i = 0; i < pathArr.size(); i++) {
				src += File.separator + pathArr.get(i);
			}
			getAllListFile(src);
			listFileAdapter.clear();
			for (int i = 0; i < arr.size(); i++) {
				listFileAdapter.add(arr.get(i));
			}
			listFileAdapter.notifyDataSetChanged();
    	}catch (Exception e) {
    		e.printStackTrace(System.out);
		}
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {          
    		if(pathArr.size()==0){
    			finish();
    			return true;
    		}else{
    			btBack.performClick();
    			return true;
    		}
        }
    	return super.onKeyDown(keyCode, event);
    }
    
    public OnClickListener onBackClick(){
    	OnClickListener onBackClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String src = "";
				if(pathArr.size()==0){return;}
				pathArr.remove(pathArr.size()- 1);
				for (int i = 0; i < pathArr.size(); i++) {
					src += File.separator +pathArr.get(i);
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
		 	case R.id.newFolder:
		 		createFolder();
	            return true;
	        case R.id.paste:
				try {
					paste();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	return true;
		}
		return true;
	}
	
	public void createFolder(){
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
			final EditText input = new EditText(this);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			        LinearLayout.LayoutParams.FILL_PARENT,
			        LinearLayout.LayoutParams.WRAP_CONTENT);
			input.setLayoutParams(lp);
			builder.setView(input);
			builder.setTitle("Folder's name");
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
					
					String folder = input.getText().toString();
					if(folder == null && folder.equals("")){return;}
					if(pathArr.size() > 0){
						for (int i = 0; i < pathArr.size(); i++) {
							path += File.separator + pathArr.get(i);
						}
					}
					path += File.separator + folder;
					File file = new File(path);
					System.out.println(file.getAbsolutePath());
					if(file.exists()){
						AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
						builder.setTitle("Duplicate");
						builder.setMessage("Folder is exist");
						builder.setPositiveButton("Ok", null);
						builder.show();
						return;
					}
					file.mkdir();
					refresh();
				}
			});
			builder.setNegativeButton("Cancel", null);
			builder.show();
			
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public OnItemLongClickListener itemLongClick(){
    	OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Files object = (Files)parent.getItemAtPosition(position);
				if(object.isFolder()){
					openOptionDialog(object.getName());
					System.out.println("Folder");
				}else{
					openOptionDialog(object.getName());
					System.out.println("File");
				}
				return true;
			}
		};
		return longClickListener;
    }
	
	public void openOptionDialog(final String name){
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
			builder.setTitle("Option");
			builder.setItems(R.array.option_arr, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						copy(name);
						System.out.println("Copy");
						break;
					case 1:
						move(name);
						System.out.println("move");
						break;
					case 2:
						remove(name);
						System.out.println("remove");
						break;
					case 3:
						rename(name);
						System.out.println("rename");
						break;
					case 4:
						details(name);
						System.out.println("detail");
						break;
					default:
						break;
					}
				}

			});
			builder.setPositiveButton("Cancel", null);
			builder.show();
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	private void copy(String name) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		for(int i = 0; i < pathArr.size(); i++){
			path += File.separator + pathArr.get(i);
		}
		path += File.separator + name;
		fileCopy = new File(path);
		fileMove = null;
		fileName = name;
		System.out.println("File copy path: " + path + "\n File name: " + name);
	}
	
	private void move(String name){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		for(int i = 0; i < pathArr.size(); i++){
			path += File.separator + pathArr.get(i);
		}
		path += File.separator + name;
		fileMove = new File(path);
		fileCopy = null;
		fileName = name;
		System.out.println("File copy path: " + path + "\n File name: " + name);
	}
	
	private void paste() throws IOException{
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		for(int i = 0; i < pathArr.size(); i++){
			path += File.separator + pathArr.get(i);
		}
		path += File.separator + fileName;
		final File file = new File(path);
		System.out.println("File copy path: " + path + "\n File name: " + fileName);
		if(fileCopy == null && fileMove == null){
			new AlertDialog.Builder(this)
	        .setTitle("No file")
	        .setMessage("There is no file to paste!")
	        .setNeutralButton("Close", new DialogInterface.OnClickListener() {
	          public void onClick(DialogInterface dlg, int sumthin) {
	            // do nothing -- it will close on its own
	          }
	        })
	        .show();
		} else {
			if(fileCopy == null){
				if(fileMove.isDirectory()){
					if(file.exists()){
						AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
						builder.setTitle("Folder is exist!");
						builder.setMessage("You want to replace folder?");
						builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									copyDirectory(fileMove, file);
									deleteDirectory(fileMove);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						builder.setNegativeButton("Cancel", null);
						builder.show();
					} else {
						copyDirectory(fileMove, file);
						deleteDirectory(fileMove);
					}
				} else {
					if(file.exists()){
						AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
						builder.setTitle("File is exist!");
						builder.setMessage("You want to replace file?");
						builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								copyFile(fileMove, file);
								fileMove.delete();
							}
						});
						builder.setNegativeButton("Cancel", null);
						builder.show();
					} else {
						copyFile(fileMove, file);
						fileMove.delete();
					}
				}
			} else {
				if(fileCopy.isDirectory()){
					if(file.exists()){
						AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
						builder.setTitle("Folder is exist!");
						builder.setMessage("You want to replace folder?");
						builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								try {
									copyDirectory(fileCopy, file);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
						builder.setNegativeButton("Cancel", null);
						builder.show();
					} else {
						copyDirectory(fileCopy, file);
					}
				} else {
					if(file.exists()){
						AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
						builder.setTitle("Folder is exist!");
						builder.setMessage("You want to replace folder?");
						builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								copyFile(fileCopy, file);
							}
						});
						builder.setNegativeButton("Cancel", null);
						builder.show();
					} else {
						copyFile(fileCopy, file);
					}
				}
			}
			refresh();
		}
	}
	
	public void remove(final String name){
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
			builder.setTitle("Remove");
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
					for(int i = 0; i < pathArr.size(); i++){
						path += File.separator + pathArr.get(i);
					}
					path += File.separator + name;
					File file = new File(path);
					processRemove(file);
					refresh();
				}
			});
			builder.setNegativeButton("Cancel", null);
			builder.show();
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public boolean processRemove(File file){
		try{
			if(file.isDirectory()){
				String[] child = file.list();
				System.out.println("path : " + file.getAbsolutePath());
				System.out.println("length " + child.length);
				for (int i = 0; i < child.length; i++) {
					System.out.println(i);
					boolean success = processRemove(new File(file,child[i]));
					if(!success){
						System.out.println("cannot");
						return false;
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return file.delete();
	}
	
	public void rename(String name){
		try{
			AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
			final EditText input = new EditText(this);
			int index = name.lastIndexOf(".");
			String type = "";
			if(index > 0 && index <= name.length() - 2 ){
				String temp = name;
				name = temp.substring(0,index);
				type = temp.substring(index,temp.length());
		    }  
			input.setText(name);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
			        LinearLayout.LayoutParams.FILL_PARENT,
			        LinearLayout.LayoutParams.WRAP_CONTENT);
			input.setLayoutParams(lp);
			builder.setView(input);
			builder.setTitle("New Name");
			final String tempName = name;
			final String tempType = type;
			builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newname = input.getText().toString();
					if(newname == null && newname.equals(""))return;
					String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
					String despath;
					for(int i = 0; i < pathArr.size(); i++){
						path += File.separator + pathArr.get(i);
					}
					despath = path;
					path += File.separator + tempName + tempType;
					despath += File.separator + newname + tempType;
					File file = new File(path);
					File des = new File(despath);
					file.renameTo(des);
					refresh();
				}
			});
			builder.setNegativeButton("Cancel", null);
			builder.show();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void details(String name){
		try{
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date last_modified = new Date();
			String info = "";
			String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
			for(int i = 0; i < pathArr.size(); i++){
				path += File.separator + pathArr.get(i);
			}
			path += File.separator + name;
			File file = new File(path);
			info += "Name : " + file.getName() + "\n";
			long size = file.length()/1024;
			last_modified.setTime(file.lastModified());
			info += "Size : " + size + " KB\n";
			info += "Last modified : " + format.format(last_modified) + "\n";
			
			AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
			builder.setTitle("Details");
			builder.setMessage(info);
			builder.setPositiveButton("Ok", null);
			builder.show();
		}catch (Exception e) {
			e.printStackTrace(System.out);
		}
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
						src += File.separator+ pathArr.get(i);
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
    
    private String getChildFile(String path){
    	int f=0,fd=0;
    	File dir = new File(path);
    	try {
    		for(File file :dir.listFiles()){
    			if(file.isDirectory())
    				fd++;
    			else
    				f++;
    		}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    	return fd+" Folder | "+f+" File";
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
    			Bitmap bitmap ;
    			if(extend.contains(".txt")){
        			icon = getResources().getDrawable(R.drawable.text_file);
        			action.setDataAndType(Uri.fromFile(f),"text/*");    
    			}else if(extend.contains(".flv") || extend.contains(".3gp") || extend.contains(".avi")){
    				bitmap = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), Thumbnails.MICRO_KIND);    		
        			icon = new BitmapDrawable(bitmap);
        			action.setDataAndType(Uri.fromFile(f),"video/*");
    			}else if(extend.contains(".mp3")){
    				icon = getResources().getDrawable(R.drawable.mp3_file);
        			action.setDataAndType(Uri.fromFile(f),"audio/*");
    			}else if(extend.contains(".doc") || extend.contains(".docx")){
        			icon = getResources().getDrawable(R.drawable.word_file);
        			action.setDataAndType(Uri.fromFile(f),"text/*");
    			}else if(extend.contains(".ppt") || extend.contains(".pptx")){
        			icon = getResources().getDrawable(R.drawable.pptx_file);
        			action.setDataAndType(Uri.fromFile(f),"text/*");
    			}else if(extend.contains(".xls") || extend.contains(".xlsx")){
        			icon = getResources().getDrawable(R.drawable.xlsx_file);
        			action.setDataAndType(Uri.fromFile(f),"text/*");
    			}else if(extend.contains(".zip") || extend.contains(".rar")){
        			icon = getResources().getDrawable(R.drawable.rar_file);
        			action.setDataAndType(Uri.fromFile(f),"video/*");
    			}else if(extend.contains(".jpg") || extend.contains(".jpeg") || extend.contains(".png") || extend.contains(".bmp") || extend.contains(".gif")){
    				bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
        			icon = new BitmapDrawable(bitmap);
        			action.setDataAndType(Uri.fromFile(f),"image/*");
    			}else if(extend.contains(".apk")){
        			icon = getResources().getDrawable(R.drawable.apk_file);
        			action.setDataAndType(Uri.fromFile(f),"application/vnd.android.package-archive");
    			}else if(extend.contains(".exe")){
        			icon = getResources().getDrawable(R.drawable.exe_file);
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
    			ff.setChildFile(getChildFile(f.getAbsolutePath()));
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
    
    public static boolean copyFile(File source, File dest) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        
        try {
          bis = new BufferedInputStream(new FileInputStream(source));
          bos = new BufferedOutputStream(new FileOutputStream(dest, false));
          
          byte[] buf = new byte[1024];
          bis.read(buf);
          
          do {
            bos.write(buf);
          } while(bis.read(buf) != -1);
        } catch (IOException e) {
          return false;
        } finally {
          try {
            if (bis != null) bis.close();
            if (bos != null) bos.close();
          } catch (IOException e) {
            return false;
          }
        }
        
        return true;
      }
    public static void copyDirectory(File sourceLocation, File targetLocation)
    	throws IOException {
    	if (sourceLocation.isDirectory()) {
    		if (!targetLocation.exists()) {
    			targetLocation.mkdirs();
    		}

    		String[] children = sourceLocation.list();
    		for (int i = 0; i < children.length; i++) {
    			copyDirectory(new File(sourceLocation, children[i]), new File(
    					targetLocation, children[i]));
    		}
    	} else {

    		copyFile(sourceLocation, targetLocation);
    	}
    }
    static public boolean deleteDirectory(File path) {
    	if (path.exists()) {
    		File[] files = path.listFiles();
    		for (int i = 0; i < files.length; i++) {
    			if (files[i].isDirectory()) {
    				deleteDirectory(files[i]);
    			} else {
    				files[i].delete();
    			}
    		}
    	}
    	return (path.delete());
    }
}
