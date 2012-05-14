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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import jcifs.smb.SmbFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Video.Thumbnails;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.eli.filemanager.R;
import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.pojo.Files;
import com.eli.util.Util;

public class ProcessFileSmb {
	ListFileAdapter fileAdapter;	
	
	Dialog dialog ;		
	
	ArrayList<String> paths;
	ArrayList<Files> list, files, folders;
	private ListActivitySmb activity;
	private EditText src;
	private Drawable icon;
	Button home_btn, back_btn;
	ListView listview,viewDetail;
	GridView gridview;
	
	String path = "";
	
	public void changeView() {
		switch (1) {
		case 1:
			gridview.setVisibility(GridView.VISIBLE);
			listview.setVisibility(ListView.GONE);
			viewDetail.setVisibility(ListView.GONE);
			fileAdapter = new ListFileAdapter(activity, R.layout.grid_detail,
					list );
			gridview.setAdapter(fileAdapter);
			gridview.setOnItemClickListener(activity.itemClick());
			break;
		case 2:
			gridview.setVisibility(GridView.GONE);
			listview.setVisibility(ListView.VISIBLE);
			viewDetail.setVisibility(ListView.GONE);
			fileAdapter = new ListFileAdapter(activity, R.layout.list_detail,
					list);
			listview.setAdapter(fileAdapter);
			listview.setOnItemClickListener(activity.itemClick());
			break;
		case 3:
			gridview.setVisibility(GridView.GONE);
			listview.setVisibility(ListView.GONE);
			viewDetail.setVisibility(ListView.VISIBLE);
			fileAdapter = new ListFileAdapter(activity, R.layout.view_detail,
					list);
			viewDetail.setAdapter(fileAdapter);
			viewDetail.setOnItemClickListener(activity.itemClick());
			break;
		default:
			gridview.setVisibility(GridView.VISIBLE);
			listview.setVisibility(ListView.GONE);
			viewDetail.setVisibility(ListView.GONE);
			fileAdapter = new ListFileAdapter(activity, R.layout.grid_detail,
					list);
			gridview = (GridView) activity.findViewById(R.id.gridViewFile);
			gridview.setAdapter(fileAdapter);
			gridview.setOnItemClickListener(activity.itemClick());
			break;
		}
	}
	
	public ProcessFileSmb(ListActivitySmb activity) {
		this.activity = activity;		
		paths = new ArrayList<String>();
		paths.add("mnt");
		paths.add("sdcard");
		src = (EditText) activity.findViewById(R.id.tvCurrentFile);
		back_btn = (Button) activity.findViewById(R.id.btBack);
		back_btn.setOnClickListener(onBackClick());
		home_btn = (Button) activity.findViewById(R.id.btHome);
		home_btn.setOnClickListener(onHomeClick());
		listview = (ListView) activity.findViewById(R.id.lvFile);
		viewDetail = (ListView) activity.findViewById(R.id.detailListView);
		gridview = (GridView) activity.findViewById(R.id.gridViewFile);
		
		src.setOnKeyListener(onAddressKey());
		src.clearFocus();
		getAllListFile("/mnt/sdcard");		
		changeView();
	}
	private OnKeyListener onAddressKey() {
		OnKeyListener onAddressKey = new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER){
					String[] temp = src.getText().toString().split("/");
					paths.clear();
					for(String a:temp){
						paths.add(a);
					}
					activity.refresh();
				}
				return false;
			}
		};
		return onAddressKey;
	}
	
	// event back click
	private OnClickListener onBackClick() {
		OnClickListener onBackClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (paths.size() != 0) {
					paths.remove(paths.size() - 1);
				}
				activity.refresh();
			}
		};
		return onBackClick;
	}

	private OnClickListener onHomeClick() {
		OnClickListener onHomeClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				paths.clear();
				paths.add("mnt");
				paths.add("sdcard");
				activity.refresh();
			}
		};
		return onHomeClick;
	}
	

	// get files and folders of folder
	public void getAllListFile(String path) {
		try {
		files = new ArrayList<Files>();
		folders = new ArrayList<Files>();
		SmbFile dir = new SmbFile("smb://192.168.1.113/");
		src.setText(dir.getCanonicalPath());
			for (SmbFile f : dir.listFiles()) {
				System.out.println("FILE: "+f.getName());
				if (f.isFile()) {
					Intent action = new Intent(Intent.ACTION_VIEW);
					Bitmap bitmap;
					if (Util.checkExtendFile(f.getName(), ".txt")
							|| Util.checkExtendFile(f.getName(), ".csv")) {
						icon = activity.getResources().getDrawable(
								R.drawable.text_file);
					}else if (Util.checkExtendFile(f.getName(), ".xml")) {
						icon = activity.getResources().getDrawable(
								R.drawable.xml_file);
					} else if (Util.checkExtendFile(f.getName(), ".flv")
							|| Util.checkExtendFile(f.getName(), ".3gp")
							|| Util.checkExtendFile(f.getName(), ".avi")) {
						bitmap = ThumbnailUtils.createVideoThumbnail(
								f.getCanonicalPath(), Thumbnails.MICRO_KIND);
						icon = new BitmapDrawable(bitmap);
					} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
						icon = activity.getResources().getDrawable(
								R.drawable.mp3_file);
					} else if (Util.checkExtendFile(f.getName(), ".doc")
							|| Util.checkExtendFile(f.getName(), ".docx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.word_file);
					} else if (Util.checkExtendFile(f.getName(), ".ppt")
							|| Util.checkExtendFile(f.getName(), ".pptx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.pptx_file);
					} else if (Util.checkExtendFile(f.getName(), ".xls")
							|| Util.checkExtendFile(f.getName(), ".xlsx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.xlsx_file);
					} else if (Util.checkExtendFile(f.getName(), ".zip")
							|| Util.checkExtendFile(f.getName(), ".rar")) {
						icon = activity.getResources().getDrawable(
								R.drawable.rar_file);
					} else if (Util.checkExtendFile(f.getName(), ".jpg")
							|| Util.checkExtendFile(f.getName(), ".jpeg")
							|| Util.checkExtendFile(f.getName(), ".png")
							|| Util.checkExtendFile(f.getName(), ".bmp")
							|| Util.checkExtendFile(f.getName(), ".gif")) {
						bitmap = BitmapFactory.decodeFile(f.getCanonicalPath());
						icon = new BitmapDrawable(bitmap);
					} else if (Util.checkExtendFile(f.getName(), ".apk")) {
						icon = activity.getResources().getDrawable(
								R.drawable.apk_file);
					} else if (Util.checkExtendFile(f.getName(), ".exe")) {
						icon = activity.getResources().getDrawable(
								R.drawable.exe_file);
					} else {
						icon = activity.getResources().getDrawable(
								R.drawable.unknown_file);
					}
					Files ff = new Files();
					ff.setIcon(icon);
					ff.setName(f.getName());
					ff.setFolder(false);
					ff.setAction(action);
					ff.setSize(f.length());
					ff.setModified(f.lastModified());
					ff.setChildFile(ff.getSize()/(1024*1024)+" MB | "+ Util.format1.format(ff.getModified()));
					ff.setPath(f.getCanonicalPath());
					files.add(ff);
				} else if (f.isDirectory()) {
					if(LoadSetting.users.getIcon()==0)
						icon = activity.getResources().getDrawable(
							R.drawable.folder_blue);
					else if(LoadSetting.users.getIcon()==1)
						icon = activity.getResources().getDrawable(
								R.drawable.folder_blue2);
					else if(LoadSetting.users.getIcon()==2)
						icon = activity.getResources().getDrawable(
								R.drawable.folder_yellow);
					else if(LoadSetting.users.getIcon()==3)
						icon = activity.getResources().getDrawable(
								R.drawable.folder_yellow2);
					Files ff = new Files();
					ff.setIcon(icon);
					ff.setName(f.getName());
					ff.setFolder(true);
					ff.setChildFile(getChildFile(f.getCanonicalPath()));
					ff.setPath(f.getCanonicalPath());
					folders.add(ff);
				}
			}
			sort(files,0);
			sort(folders,0);
			list = new ArrayList<Files>();
			list.addAll(folders);
			list.addAll(files);
		} catch (NullPointerException e) {
			list.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// get sub folders and files
	private String getChildFile(String path) {
		int f = 0, fd = 0;
		try {
		SmbFile dir = new SmbFile(path);
			for (SmbFile file : dir.listFiles()) {
				if (file.isDirectory()) {
					fd++;
				} else {
					f++;
				}
			}
		} catch (NullPointerException e) {
			return fd + " " + activity.getString(R.string.folder) + " | " + f + " " + activity.getString(R.string.file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fd + " " + activity.getString(R.string.folder) + " | " + f + " " + activity.getString(R.string.file);
	}

	private void sort(ArrayList<Files> lst,int sort) {

		switch (sort){					
			case 0:
				Collections.sort(lst, new Comparator<Files>() {
						@Override
						public int compare(Files object1, Files object2) {
							return object1.getName().toLowerCase()
									.compareTo(object2.getName().toLowerCase());
						}
				});
			break;
			case 1:
				Collections.sort(lst, new Comparator<Files>() {
					@Override
					public int compare(Files object1, Files object2) {
						return object1.getSize().compareTo(object2.getSize());
					}
				});
				break;
			case 2:
				Collections.sort(lst, new Comparator<Files>() {
					@Override
					public int compare(Files object1, Files object2) {
						return object1.getModified().compareTo(object2.getModified());
					}
				});
				break;
		}
	}

	// create folder
	public void createFolder() {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			final EditText input = new EditText(activity);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);			
			input.setLayoutParams(lp);			
			input.setLines(1);
			input.setSingleLine(true);
			builder.setView(input);
			builder.setTitle("Folder's name");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String folder = input.getText().toString();
							if (folder == null && folder.equals("")) {
								return;
							}
							String path = "";
							if (paths.size() > 0) {
								for (int i = 0; i < paths.size(); i++) {
									path += File.separator + paths.get(i);
								}
							}
							path += File.separator + folder;
							File file = new File(path);
							System.out.println(file.getAbsolutePath());
							if (file.exists()) {
								AlertDialog.Builder builder = new AlertDialog.Builder(
										activity);
								builder.setTitle("Duplicate");
								builder.setMessage("Folder is exist");
								builder.setPositiveButton("Ok", null);
								builder.show();
								return;
							}
							file.mkdir();
							activity.refresh();
						}
					});
			builder.setNegativeButton("Cancel", null);
			builder.show();

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}


	public void details(String name) {
		try {
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
			Date last_modified = new Date();
			String info = "";
			String path = "";
			for (int i = 0; i < paths.size(); i++) {
				path += File.separator + paths.get(i);
			}
			path += File.separator + name;
			File file = new File(path);
			info += activity.getString(R.string.name) + " : " + file.getName() + "\n";
			long size = file.length() / 1024;
			last_modified.setTime(file.lastModified());
			if(file.isFile()){
				info += activity.getString(R.string.size) + " : " + size + " KB\n";
			}
			info += activity.getString(R.string.lastmodified) + " : " + format.format(last_modified) + "\n";

			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Details");
			builder.setMessage(info);
			builder.setPositiveButton("Ok", null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	//search files and folders
	public void search(){
		try{
			Intent intent = new Intent("com.eli.filemanager.SEARCH");
			activity.startActivity(intent);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setting(){
		try{
			Intent intent = new Intent("com.eli.filemanager.SETTING");
			activity.startActivity(intent);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
