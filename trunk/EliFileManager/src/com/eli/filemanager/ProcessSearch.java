package com.eli.filemanager;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.eli.filemanager.pojo.Files;
import com.eli.util.Util;

public class ProcessSearch {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	ProgressDialog mProgressDialog;
	public static boolean SUB = false;
	public static boolean TYPE = false;
	
	SearchActivity activity;
	private Button search_btn;
	private Spinner directory, type_search;
	private CheckBox include_sub;
	private EditText name_search;
	String url, search_str, directory_str, fectching_str;
	public static ArrayList<Files> array;
	
	public ProcessSearch(SearchActivity activity) {
		this.activity = activity;
		directory = (Spinner)activity.findViewById(R.id.directory);
		type_search = (Spinner)activity.findViewById(R.id.type_search);
		name_search = (EditText)activity.findViewById(R.id.name_et);
		include_sub = (CheckBox)activity.findViewById(R.id.sub_cbx);
		search_btn = (Button)activity.findViewById(R.id.serch_btn);
		array = new ArrayList<Files>();
		init();
	}

	public void init(){
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(activity, R.array.directory_arr ,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directory.setAdapter(adapter1);
        directory.setSelection(0);
        directory.setOnItemSelectedListener(onItemDirectory());
        
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(activity, R.array.type_search_arr ,android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_search.setAdapter(adapter2);
        type_search.setSelection(0);
        type_search.setOnItemSelectedListener(onItemTypeSearch());
        
        search_btn.setOnClickListener(onClickSearchListener());
	}
	
	public OnItemSelectedListener onItemDirectory(){
		OnItemSelectedListener onSelect = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String name = parent.getItemAtPosition(position).toString();
				directory_str = name;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
		return onSelect;
	}
	
	public OnItemSelectedListener onItemTypeSearch(){
		OnItemSelectedListener onSelect = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					TYPE = false;
					System.out.println("a");
					break;
				case 1:
					TYPE = true;
					System.out.println("b");
					break;
				default:
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
		return onSelect;
	}
	
	public OnClickListener onClickSearchListener(){
		OnClickListener onSearchClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				search_str = name_search.getText().toString().trim();
				if(search_str == null || search_str.equals("")){
					System.out.println("ABC");
					AlertDialog.Builder builder = new AlertDialog.Builder(activity);
					builder.setTitle("Missing");
					builder.setMessage("You have to type searching name");
					builder.setPositiveButton("Ok", null);
					builder.show();
					name_search.requestFocus();
				}else{
					processOptionSearch();
				}
			}
		};
		return onSearchClick;
	}
	
	public void processOptionSearch(){
		try{
			if(include_sub.isChecked()){
				System.out.println("sub");
				SUB = true; 
			}else{
				SUB = false;
			}
			System.out.println("directory : " + directory_str);
			System.out.println("type : " + TYPE);
			System.out.println("search : " + search_str);
			System.out.println("sub : " + SUB);
			
			searching();
			System.out.println("Finish");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void searching(){
		new AsyncTask<String, String, Void>(){
			
			@Override
		    protected void onPreExecute() {
				mProgressDialog = ProgressDialog.show(activity, "", "Loading...", true);
		    }
			
			protected void onProgressUpdate(String... progress) {
		         mProgressDialog.setMessage(progress[0]);
		    }
			
			@Override
			protected Void doInBackground(String... params) {
				array.clear();
				File file = new File(directory_str);
				File[] child = file.listFiles();
				if(child.length == 0){
					return null;
				}
				Files files = null;
				for (int i = 0; i < child.length; i++) {
					if(!file.isHidden()){
						if(SUB){
							searchInSubFolder(child[i]);
						}else{
							if(!TYPE){//search folder and file
								if(child[i].getName().toString().toUpperCase().indexOf(search_str.toUpperCase()) >= 0){
									files = new Files();
									files.setName(child[i].getName());
									if(child[i].isDirectory()){
										files.setFolder(true);
									}
									files.setChildFile(child[i].getAbsolutePath());
									array.add(files);
								}
							}else{// search extendsion file (.*)
								boolean flag = Util.checkExtendFile(child[i].getName().toString().toUpperCase(), search_str.toUpperCase());
								if(flag){
									files = new Files();
									files.setName(child[i].getName());
									files.setChildFile(child[i].getAbsolutePath());
									array.add(files);
								}
							}
						}
						fectching_str = child[i].getAbsoluteFile().toString();
						publishProgress(fectching_str);
					}
				}
				
				
				return null;
			}
			
			public void searchInSubFolder(File file){
				try {
					if(!file.isHidden()){
						fectching_str = file.getAbsoluteFile().toString();
						publishProgress(fectching_str);
						Files files = null;
						if(file.isDirectory()){
							if(!TYPE){
								if(file.getName().toString().toUpperCase().indexOf(search_str.toUpperCase()) >= 0){
									System.out.println("folder");
									files = new Files();
									files.setName(file.getName());
									files.setFolder(true);
									files.setChildFile(file.getAbsolutePath());
									array.add(files);
								}
							}
							File[] child = file.listFiles();
							if(child == null || child.length == 0){
								return;
							}
							for (int i = 0; i < child.length; i++) {
								fectching_str = child[i].getAbsoluteFile().toString();
								publishProgress(fectching_str);
								searchInSubFolder(new File(file, child[i].getName()));
							}
							
						}else if(file.isFile()){
							if(!TYPE){
								if(file.getName().toString().toUpperCase().indexOf(search_str.toUpperCase()) >= 0){
									files = new Files();
									files.setName(file.getName());
									files.setChildFile(file.getAbsolutePath());
									array.add(files);
									fectching_str = file.getAbsoluteFile().toString();
									publishProgress(fectching_str);
								}
							}else{
								boolean flag = Util.checkExtendFile(file.getName().toString().toUpperCase(), search_str.toUpperCase());
								if(flag){
									files = new Files();
									files.setName(file.getName());
									files.setChildFile(file.getAbsolutePath());
									array.add(files);
									fectching_str = file.getAbsoluteFile().toString();
									publishProgress(fectching_str);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			protected void onPostExecute(Void arg) {
			    if (mProgressDialog.isShowing()){
			    	mProgressDialog.dismiss();
			    	if(array.size() > 0){
			    		initListSearch();
				    	Intent intent = new Intent(activity,ListSearchActivity.class);
				    	activity.startActivity(intent);
			    	}else{
				    	System.out.println("File not found");
				    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				    	builder.setTitle("Alert");
				    	builder.setMessage("File not found");
				    	builder.setPositiveButton("Ok", null);
				    	builder.show();
				    }
			    }
			}
		}.execute("");
	}
	

	public void initListSearch(){
		Files f = null;
		for (int i = 0; i < array.size(); i++) {
			f = array.get(i);
			if (!f.isFolder()) {
				Intent action = new Intent(Intent.ACTION_VIEW);
				Bitmap bitmap;
				if (Util.checkExtendFile(f.getName(), ".txt")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.text_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".flv")
						|| Util.checkExtendFile(f.getName(), ".3gp")
						|| Util.checkExtendFile(f.getName(), ".avi")) {
					bitmap = ThumbnailUtils.createVideoThumbnail(
							f.getChildFile(), Thumbnails.MICRO_KIND);
					f.setIcon(new BitmapDrawable(bitmap));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "video/*");
				} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.mp3_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "audio/*");
				} else if (Util.checkExtendFile(f.getName(), ".doc")
						|| Util.checkExtendFile(f.getName(), ".docx")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.word_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".ppt")
						|| Util.checkExtendFile(f.getName(), ".pptx")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.pptx_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".xls")
						|| Util.checkExtendFile(f.getName(), ".xlsx")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.xlsx_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".zip")
						|| Util.checkExtendFile(f.getName(), ".rar")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.rar_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "video/*");
				} else if (Util.checkExtendFile(f.getName(), ".jpg")
						|| Util.checkExtendFile(f.getName(), ".jpeg")
						|| Util.checkExtendFile(f.getName(), ".png")
						|| Util.checkExtendFile(f.getName(), ".bmp")
						|| Util.checkExtendFile(f.getName(), ".gif")) {
					bitmap = BitmapFactory.decodeFile(f.getChildFile());
					f.setIcon(new BitmapDrawable(bitmap));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())), "image/*");
				} else if (Util.checkExtendFile(f.getName(), ".apk")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.apk_file));
					action.setDataAndType(Uri.fromFile(new File(f.getChildFile())),
							"application/vnd.android.package-archive");
				} else if (Util.checkExtendFile(f.getName(), ".exe")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.exe_file));
				} else {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.unknown_file));
				}
				f.setAction(action);
			} else if (f.isFolder()) {
				f.setIcon(activity.getResources().getDrawable(
						R.drawable.folder));
			}
		}
	}
}
