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

import android.app.AlertDialog;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.pojo.Files;
import com.eli.filemanager.pojo.Users;
import com.eli.util.Util;

public class ProcessFile {
	ListFileAdapter fileAdapter;
	
	ArrayList<String> paths;
	ArrayList<Files> list, files, folders;
	private ListActivity activity;
	private EditText src;
	private Drawable icon;
	Button home_btn, back_btn;
	private ListView listview;
	private GridView gridview;
	boolean flag_change = true;
	ImageView nofileImg;

	public ProcessFile(ListActivity activity) {
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
		gridview = (GridView) activity.findViewById(R.id.gridViewFile);
		nofileImg = (ImageView) activity.findViewById(R.id.ivNoFile);
		
		src.setOnKeyListener(onAddressKey());
		getAllListFile("/mnt/sdcard");
		
		changeView();
		
	}
	

	public void onChangeSetting(Context context){
		LoadSetting.load(context);
	}

	// switch listview and gridview
	public void changeView() {
		flag_change = !flag_change;
		if (flag_change) {
			gridview.setVisibility(GridView.VISIBLE);
			listview.setVisibility(ListView.GONE);
			fileAdapter = new ListFileAdapter(activity, R.layout.grid_detail,
					list);
			gridview = (GridView) activity.findViewById(R.id.gridViewFile);
			gridview.setAdapter(fileAdapter);
			gridview.setOnItemClickListener(activity.itemClick());
			gridview.setOnItemLongClickListener(activity.itemLongClick());
		} else {
			gridview.setVisibility(GridView.GONE);
			listview.setVisibility(ListView.VISIBLE);
			fileAdapter = new ListFileAdapter(activity, R.layout.list_detail,
					list);
			listview = (ListView) activity.findViewById(R.id.lvFile);
			listview.setAdapter(fileAdapter);
			listview.setOnItemClickListener(activity.itemClick());
			listview.setOnItemLongClickListener(activity.itemLongClick());
		}
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
		files = new ArrayList<Files>();
		folders = new ArrayList<Files>();
		File dir = new File(path);
		src.setText(dir.getAbsolutePath());
		try {
			for (File f : dir.listFiles()) {
				if (f.isFile()) {
					Intent action = new Intent(Intent.ACTION_VIEW);
					Bitmap bitmap;
					if (Util.checkExtendFile(f.getName(), ".txt")) {
						icon = activity.getResources().getDrawable(
								R.drawable.text_file);
						action.setDataAndType(Uri.fromFile(f), "text/*");
					} else if (Util.checkExtendFile(f.getName(), ".flv")
							|| Util.checkExtendFile(f.getName(), ".3gp")
							|| Util.checkExtendFile(f.getName(), ".avi")) {
						bitmap = ThumbnailUtils.createVideoThumbnail(
								f.getAbsolutePath(), Thumbnails.MICRO_KIND);
						icon = new BitmapDrawable(bitmap);
						action.setDataAndType(Uri.fromFile(f), "video/*");
					} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
						icon = activity.getResources().getDrawable(
								R.drawable.mp3_file);
						action.setDataAndType(Uri.fromFile(f), "audio/*");
					} else if (Util.checkExtendFile(f.getName(), ".doc")
							|| Util.checkExtendFile(f.getName(), ".docx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.word_file);
						action.setDataAndType(Uri.fromFile(f), "text/*");
					} else if (Util.checkExtendFile(f.getName(), ".ppt")
							|| Util.checkExtendFile(f.getName(), ".pptx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.pptx_file);
						action.setDataAndType(Uri.fromFile(f), "text/*");
					} else if (Util.checkExtendFile(f.getName(), ".xls")
							|| Util.checkExtendFile(f.getName(), ".xlsx")) {
						icon = activity.getResources().getDrawable(
								R.drawable.xlsx_file);
						action.setDataAndType(Uri.fromFile(f), "text/*");
					} else if (Util.checkExtendFile(f.getName(), ".zip")
							|| Util.checkExtendFile(f.getName(), ".rar")) {
						icon = activity.getResources().getDrawable(
								R.drawable.rar_file);
						action.setDataAndType(Uri.fromFile(f), "video/*");
					} else if (Util.checkExtendFile(f.getName(), ".jpg")
							|| Util.checkExtendFile(f.getName(), ".jpeg")
							|| Util.checkExtendFile(f.getName(), ".png")
							|| Util.checkExtendFile(f.getName(), ".bmp")
							|| Util.checkExtendFile(f.getName(), ".gif")) {
						bitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
						icon = new BitmapDrawable(bitmap);
						action.setDataAndType(Uri.fromFile(f), "image/*");
					} else if (Util.checkExtendFile(f.getName(), ".apk")) {
						icon = activity.getResources().getDrawable(
								R.drawable.apk_file);
						action.setDataAndType(Uri.fromFile(f),
								"application/vnd.android.package-archive");
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
					files.add(ff);
				} else if (f.isDirectory()) {
					icon = activity.getResources().getDrawable(
							R.drawable.folder);
					Files ff = new Files();
					ff.setIcon(icon);
					ff.setName(f.getName());
					ff.setFolder(true);
					ff.setChildFile(getChildFile(f.getAbsolutePath()));
					folders.add(ff);
				}
			}
			sort(files);
			sort(folders);
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
		File dir = new File(path);
		try {
			for (File file : dir.listFiles()) {
				if (file.isDirectory()) {
					fd++;
				} else {
					f++;
				}
			}
		} catch (NullPointerException e) {
			return fd + " Folder | " + f + " File";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fd + " Folder | " + f + " File";
	}

	// sort a-z
	private void sort(ArrayList<Files> lst) {
		Collections.sort(lst, new Comparator<Files>() {
			@Override
			public int compare(Files object1, Files object2) {
				return object1.getName().toLowerCase()
						.compareTo(object2.getName().toLowerCase());
			}
		});
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

	// paste files and folder to destination
	File fileCopy, fileMove;
	String fileName;

	public void paste() throws IOException {
		String path = "";
		for (int i = 0; i < paths.size(); i++) {
			path += File.separator + paths.get(i);
		}
		path += File.separator + fileName;
		final File file = new File(path);
		if (fileCopy == null && fileMove == null) {
			new AlertDialog.Builder(activity)
					.setTitle("No file")
					.setMessage("There is no file to paste!")
					.setNeutralButton("Close",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dlg,
										int sumthin) {
								}
							}).show();
		} else {
			if (fileCopy == null) {
				if (fileMove.isDirectory()) {
					if (file.exists()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						builder.setTitle("Folder is exist!");
						builder.setMessage("You want to replace folder?");
						builder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
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
					if (file.exists()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						builder.setTitle("File is exist!");
						builder.setMessage("You want to replace file?");
						builder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
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
				if (fileCopy.isDirectory()) {
					if (file.exists()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						builder.setTitle("Folder is exist!");
						builder.setMessage("You want to replace folder?");
						builder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
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
					if (file.exists()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						builder.setTitle("Folder is exist!");
						builder.setMessage("You want to replace folder?");
						builder.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
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
			activity.refresh();
		}
	}

	// copy directory
	public void copyDirectory(File sourceLocation, File targetLocation)
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

	// copy files
	public boolean copyFile(File source, File dest) {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(source));
			bos = new BufferedOutputStream(new FileOutputStream(dest, false));
			byte[] buf = new byte[1024];
			bis.read(buf);
			do {
				bos.write(buf);
			} while (bis.read(buf) != -1);
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	// alert box
	protected void alertbox(String title, String mymessage) {
		new AlertDialog.Builder(activity)
				.setMessage(mymessage)
				.setTitle(title)
				.setCancelable(true)
				.setNeutralButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	// delete derictory
	public boolean deleteDirectory(File path) {
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

	// copy filde and folder
	public void copy(String name) {
		String path = "";
		for (int i = 0; i < paths.size(); i++) {
			path += File.separator + paths.get(i);
		}
		path += File.separator + name;
		fileCopy = new File(path);
		fileMove = null;
		fileName = name;
	}

	// move filde and folder
	public void move(String name) {
		String path = "";
		for (int i = 0; i < paths.size(); i++) {
			path += File.separator + paths.get(i);
		}
		path += File.separator + name;
		fileMove = new File(path);
		fileCopy = null;
		fileName = name;
	}

	// remove files and folders
	public void remove(final String name) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Delete");
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String path = "";
							for (int i = 0; i < paths.size(); i++) {
								path += File.separator + paths.get(i);
							}
							path += File.separator + name;
							File file = new File(path);
							processRemove(file);
							activity.refresh();
						}
					});
			builder.setNegativeButton("Cancel", null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	// process remove sub folders and files
	public boolean processRemove(File file) {
		try {
			if (file.isDirectory()) {
				String[] child = file.list();
				for (int i = 0; i < child.length; i++) {
					boolean success = processRemove(new File(file, child[i]));
					if (!success) {
						return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.delete();
	}

	// rename files or folder
	public void rename(String name) {
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			final EditText input = new EditText(activity);
			int index = name.lastIndexOf(".");
			String type = "";
			if (index > 0 && index <= name.length() - 2) {
				String temp = name;
				name = temp.substring(0, index);
				type = temp.substring(index, temp.length());
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
			builder.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String newname = input.getText().toString();
							if (newname == null && newname.equals(""))
								return;
							String path = "";
							String despath;
							for (int i = 0; i < paths.size(); i++) {
								path += File.separator + paths.get(i);
							}
							despath = path;
							path += File.separator + tempName + tempType;
							despath += File.separator + newname + tempType;
							File file = new File(path);
							File des = new File(despath);
							file.renameTo(des);
							activity.refresh();
						}
					});
			builder.setNegativeButton("Cancel", null);
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// view details files and folders
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
			info += "Name : " + file.getName() + "\n";
			long size = file.length() / 1024;
			last_modified.setTime(file.lastModified());
			info += "Size : " + size + " KB\n";
			info += "Last modified : " + format.format(last_modified) + "\n";

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
