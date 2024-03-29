package com.eli.filemanager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.eli.filemanager.dao.LoadSetting;
import com.eli.filemanager.pojo.Files;
import com.eli.util.Util;

public class ProcessSearch {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	ProgressDialog mProgressDialog;
	public static boolean SUB = false;
	public static boolean TYPE = false;
	public boolean running = true;
	SearchActivity activity;
	private Button search_btn;
	private Spinner directory, type_search, day_spiner, size_spiner;
	private CheckBox include_sub;
	private EditText name_search;
	long sizeFile;
	String url, search_str, directory_str, fectching_str;
	public static ArrayList<Files> array;

	public ProcessSearch(SearchActivity activity) {
		this.activity = activity;
		directory = (Spinner) activity.findViewById(R.id.directory);
		type_search = (Spinner) activity.findViewById(R.id.type_search);
		name_search = (EditText) activity.findViewById(R.id.name_et);
		size_spiner = (Spinner) activity.findViewById(R.id.size_spinner);
		day_spiner = (Spinner) activity.findViewById(R.id.day_spinner);
		include_sub = (CheckBox) activity.findViewById(R.id.sub_cbx);
		search_btn = (Button) activity.findViewById(R.id.serch_btn);
		array = new ArrayList<Files>();
		init();
	}

	public void init() {
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(
				activity, R.array.directory_arr,
				android.R.layout.simple_spinner_item);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		directory.setAdapter(adapter1);
		directory.setSelection(0);
		directory.setOnItemSelectedListener(onItemDirectory());

		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
				activity, R.array.type_search_arr,
				android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		type_search.setAdapter(adapter2);
		type_search.setSelection(0);
		type_search.setOnItemSelectedListener(onItemTypeSearch());

		ArrayAdapter<CharSequence> adapter3 = ArrayAdapter
				.createFromResource(activity, R.array.day_arr,
						android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		day_spiner.setAdapter(adapter3);
		day_spiner.setSelection(0);

		ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(
				activity, R.array.size_arr,
				android.R.layout.simple_spinner_item);
		adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		size_spiner.setAdapter(adapter4);
		size_spiner.setSelection(0);

		search_btn.setOnClickListener(onClickSearchListener());
	}

	public OnItemSelectedListener onItemDirectory() {
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

	public OnItemSelectedListener onItemTypeSearch() {
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

	public OnClickListener onClickSearchListener() {
		OnClickListener onSearchClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				search_str = name_search.getText().toString().trim();
				if (search_str == null || search_str.equals("")) {
					System.out.println("ABC");
					AlertDialog.Builder builder = new AlertDialog.Builder(
							activity);
					builder.setTitle("Missing");
					builder.setMessage("You have to type searching name");
					builder.setPositiveButton("Ok", null);
					builder.show();
					name_search.requestFocus();
				} else {
					processOptionSearch();
				}
			}
		};
		return onSearchClick;
	}

	public void processOptionSearch() {
		try {
			if (include_sub.isChecked()) {
				System.out.println("sub");
				SUB = true;
			} else {
				SUB = false;
			}

			System.out.println("directory : " + directory_str);
			System.out.println("type : " + TYPE);
			System.out.println("search : " + search_str);
			System.out.println("sub : " + SUB);

			searching();
			System.out.println("Finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void searching() {
		running = true;
		new AsyncTask<String, String, Void>() {

			@Override
			protected void onPreExecute() {
				mProgressDialog = ProgressDialog.show(activity, "",
						"Loading...", true);
				mProgressDialog.setCancelable(true);
				mProgressDialog.setOnCancelListener(new OnCancelListener() {
		            @Override
		            public void onCancel(DialogInterface dialog) {
		                running = false;
		            }
		        });
			}

			protected void onProgressUpdate(String... progress) {
				mProgressDialog.setMessage(progress[0]);
			}

			@Override
			protected Void doInBackground(String... params) {
				if(running){
					array.clear();
					File file = new File(directory_str);
					File[] child = file.listFiles();
					if (child.length == 0) {
						return null;
					}
					Files files = null;
					for (int i = 0; i < child.length; i++) {
						if (!file.isHidden()) {
							if (SUB) {
								searchInSubFolder(child[i]);
							} else {
								if (!TYPE) {// search folder and file
									if (child[i].isFile()) {
										String temp = child[i].getName().toString()
												.toUpperCase();
										int index = temp.lastIndexOf(".");
										temp = temp.substring(0, index);
										if (temp.indexOf(search_str.toUpperCase()) >= 0) {
											files = new Files();
											files.setName(child[i].getName());
											if (child[i].isDirectory()) {
												files.setFolder(true);
											}
											files.setChildFile(child[i]
													.getAbsolutePath());
											files.setSize(child[i].length() / 1024);
											files.setModified(child[i]
													.lastModified());
											array.add(files);
										}
									} else {
										if (child[i].getName().toString()
												.toUpperCase()
												.indexOf(search_str.toUpperCase()) >= 0) {
											files = new Files();
											files.setName(child[i].getName());
											if (child[i].isDirectory()) {
												files.setFolder(true);
											}
											files.setChildFile(child[i]
													.getAbsolutePath());
											files.setModified(child[i]
													.lastModified());
											array.add(files);
										}
									}
								} else {// search extendsion file (.*)
									boolean flag = Util.checkExtendFile(child[i]
											.getName().toString().toUpperCase(),
											search_str.toUpperCase());
									if (flag) {
										files = new Files();
										files.setName(child[i].getName());
										files.setChildFile(child[i]
												.getAbsolutePath());
										files.setSize(child[i].length() / 1024);
										files.setModified(child[i].lastModified());
										array.add(files);
									}
								}
							}
							fectching_str = child[i].getAbsoluteFile().toString();
							publishProgress(fectching_str);
						}
					}
					 if(array.size() > 0){
						 checkDay();
						 checkSize();
					 }
				}
				return null;
			}

			public void checkDay() {
				int itemSelect = day_spiner.getSelectedItemPosition();
				int day = 0;
				switch (itemSelect) {
				case 0:
					day = 0;
					break;
				case 1:
					day = 7;
					break;
				case 2:
					day = 14;
					break;
				case 3:
					day = 21;
					break;
				case 4:
					day = 30;
					break;
				case 5:
					day = 90;
					break;
				case 6:
					day = 180;
					break;
				case 7:
					day = 270;
					break;
				case 8:
					day = 365;
					break;
				default:
					day = 0;
					break;
				}
				if (day != 0) {
					Calendar current = Calendar.getInstance();
					int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
					long result = current.getTimeInMillis() - (day*MILLIS_IN_DAY);
					DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
					Files files;
					for (int i = 0; i < array.size(); i++) {
						files = array.get(i);
						if (!files.isFolder()) {
							if (files.getModified() < result) {
								array.remove(i);
								i--;
							}
						}
					}
				}
			}

			public void checkSize() {
				String tempSize = size_spiner.getSelectedItem().toString()
						.trim();
				if (!tempSize.equals("")) {
					long size = Long.parseLong(tempSize);
					Files files;
					for (int i = 0; i < array.size(); i++) {
						files = array.get(i);
						if (!files.isFolder()) {
							if (files.getSize() > size) {
								array.remove(i);
								i--;
							}
						}
					}
				}
			}

			public void searchInSubFolder(File file) {
				try {
					if (!file.isHidden()) {
						fectching_str = file.getAbsoluteFile().toString();
						publishProgress(fectching_str);
						Files files = null;
						if (file.isDirectory()) {
							if (!TYPE) {
								if (file.getName().toString().toUpperCase()
										.indexOf(search_str.toUpperCase()) >= 0) {
									System.out.println("folder");
									files = new Files();
									files.setName(file.getName());
									files.setFolder(true);
									files.setChildFile(file.getAbsolutePath());
									files.setModified(file.lastModified());
									array.add(files);
								}
							}
							File[] child = file.listFiles();
							if (child == null || child.length == 0) {
								return;
							}
							for (int i = 0; i < child.length; i++) {
								fectching_str = child[i].getAbsoluteFile()
										.toString();
								publishProgress(fectching_str);
								searchInSubFolder(new File(file,
										child[i].getName()));
							}

						} else if (file.isFile()) {
							if (!TYPE) {
								String temp = file.getName().toString()
										.toUpperCase();
								int index = temp.lastIndexOf(".");
								if (index > 0) {
									temp = temp.substring(0, index);
								}
								if (temp.indexOf(search_str.toUpperCase()) >= 0) {
									files = new Files();
									files.setName(file.getName());
									files.setChildFile(file.getAbsolutePath());
									files.setSize(file.length() / 1024);
									files.setModified(file.lastModified());
									array.add(files);
									fectching_str = file.getAbsoluteFile()
											.toString();
									publishProgress(fectching_str);
								}
							} else {
								boolean flag = Util.checkExtendFile(file
										.getName().toString().toUpperCase(),
										search_str.toUpperCase());
								if (flag) {
									files = new Files();
									files.setName(file.getName());
									files.setChildFile(file.getAbsolutePath());
									files.setSize(file.length() / 1024);
									files.setModified(file.lastModified());
									array.add(files);
									fectching_str = file.getAbsoluteFile()
											.toString();
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
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
					if (array.size() > 0) {
						initListSearch();
						Intent intent = new Intent(activity,
								ListSearchActivity.class);
						activity.startActivity(intent);
					} else {
						System.out.println("File not found");
						AlertDialog.Builder builder = new AlertDialog.Builder(
								activity);
						builder.setTitle("Alert");
						builder.setMessage("File not found");
						builder.setPositiveButton("Ok", null);
						builder.show();
					}
				}
			}
		}.execute("");
	}

	public void initListSearch() {
		Files f = null;
		for (int i = 0; i < array.size(); i++) {
			f = array.get(i);
			if (!f.isFolder()) {
				Intent action = new Intent(Intent.ACTION_VIEW);
				Bitmap bitmap;
				if (Util.checkExtendFile(f.getName(), ".txt")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.text_file));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".flv")
						|| Util.checkExtendFile(f.getName(), ".3gp")
						|| Util.checkExtendFile(f.getName(), ".avi")) {
					bitmap = ThumbnailUtils.createVideoThumbnail(
							f.getChildFile(), Thumbnails.MICRO_KIND);
					f.setIcon(new BitmapDrawable(bitmap));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "video/*");
				} else if (Util.checkExtendFile(f.getName(), ".mp3")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.mp3_file));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "audio/*");
				} else if (Util.checkExtendFile(f.getName(), ".doc")
						|| Util.checkExtendFile(f.getName(), ".docx")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.word_file));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".ppt")
						|| Util.checkExtendFile(f.getName(), ".pptx")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.pptx_file));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".xls")
						|| Util.checkExtendFile(f.getName(), ".xlsx")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.xlsx_file));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "text/*");
				} else if (Util.checkExtendFile(f.getName(), ".zip")
						|| Util.checkExtendFile(f.getName(), ".rar")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.rar_file));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "video/*");
				} else if (Util.checkExtendFile(f.getName(), ".jpg")
						|| Util.checkExtendFile(f.getName(), ".jpeg")
						|| Util.checkExtendFile(f.getName(), ".png")
						|| Util.checkExtendFile(f.getName(), ".bmp")
						|| Util.checkExtendFile(f.getName(), ".gif")) {
					bitmap = BitmapFactory.decodeFile(f.getChildFile());
					f.setIcon(new BitmapDrawable(bitmap));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())), "image/*");
				} else if (Util.checkExtendFile(f.getName(), ".apk")) {
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.apk_file));
					action.setDataAndType(
							Uri.fromFile(new File(f.getChildFile())),
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
				if (LoadSetting.users.getIcon() == 0)
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.folder));
				else
					f.setIcon(activity.getResources().getDrawable(
							R.drawable.folder_yellow));
			}
		}
	}
}
