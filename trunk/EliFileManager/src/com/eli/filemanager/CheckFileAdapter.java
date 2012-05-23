package com.eli.filemanager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import fpt.util.FTPUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CheckFileAdapter extends BaseAdapter implements OnClickListener {
	FTPFile[] list = new FTPFile[1];
	List<FTPFile> listCheck = new ArrayList<FTPFile>();
	DownloadActivity context;
	RelativeLayout flag=null;

	boolean flagChangeView=false;
	
	Bundle language;
	String duoi="";
	
	public Bundle getLanguage() {
		return language;
	}

	public void setLanguage(Bundle language) {
		this.language = language;
	}

	public CheckFileAdapter() {
		
	}

	public FTPFile[] getList() {
		return list;
	}


	public void setList(FTPFile[] list) {
		this.list = list;
	}


	public CheckFileAdapter(FTPFile[] list, DownloadActivity context,boolean flagChangeView,Bundle language) {
		super();
		this.list = list;
		this.context = context;
		this.flagChangeView = flagChangeView;
		this.language=language;
	}

	@Override
	public int getCount() {

		return list.length;
	}

	@Override
	public Object getItem(int arg0) {

		return list[arg0];
	}

	@Override
	public long getItemId(int arg0) {

		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//if (convertView == null) {			
			if(isFlagChangeView() == false){
				LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.checkfile, null);
				
				final FTPFile file = list[position];
				final CheckBox checkBox = (CheckBox) convertView
				.findViewById(R.id.checkFile);



				final TextView txtName = (TextView) convertView
				.findViewById(R.id.txtName);
				final TextView txtSize = (TextView) convertView
				.findViewById(R.id.txtSize);
				final RelativeLayout linerFile = (RelativeLayout) convertView
				.findViewById(R.id.linerFile);
				
				txtName.setText(file.getName());
				if (file.isFile() ) {
					txtName.setTextColor(Color.parseColor("#FFFFFF"));
					txtSize.setText(file.getSize() + " bytes");
				} else if (file.isDirectory()) {
					txtName.setTextColor(Color.parseColor("#FFFF00"));
					txtSize.setText("");
				}

				// System.out.println(position + " - " + file.getName());
				int co = 0;
				for (int i = 0; i < listCheck.size(); i++) {
					if (file.getName().equals(listCheck.get(i).getName())) {
						checkBox.setChecked(true);
						co = 1;
					}
				}
				if (checkBox.isChecked()) {
					if (co == 0) {
						checkBox.setChecked(false);
					}
				}

				checkBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (checkBox.isChecked()) {
							listCheck.add(file);
						} else {
							for (int i = 0; i < listCheck.size(); i++) {
								if (file.getName().equals(listCheck.get(i).getName())) {
									listCheck.remove(i);
								}
							}

						}

					}
				});
				linerFile.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(flag!=null){
							flag.setBackgroundColor(Color.parseColor("#000000"));
						}

						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							linerFile.setBackgroundColor(Color.parseColor("#ff6600"));
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							linerFile.setBackgroundColor(Color.parseColor("#ff6600"));
						}
						flag=linerFile;
						return false;
					}
				});

				linerFile.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						context.loadLocation(file);
					}
				});

				linerFile.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {

						@SuppressWarnings("static-access")
						LayoutInflater inflater = (LayoutInflater) context
						.getApplicationContext().getSystemService(
								context.LAYOUT_INFLATER_SERVICE);
						View view = inflater.inflate(R.layout.alertlongclick, null);

//						final TextView copy = (TextView) view.findViewById(R.id.idcopy);
						final TextView rename = (TextView) view
						.findViewById(R.id.idrename);
						final TextView delete = (TextView) view
						.findViewById(R.id.iddelete);
						final TextView detail = (TextView)view.findViewById(R.id.iddetail);

//						copy.setText("Copy");
						rename.setText(language.getString("rename"));
						delete.setText(language.getString("delete"));
						detail.setText(language.getString("detail"));

						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle(language.getString("choose"));
						builder.setView(view);
						
						builder.setNegativeButton(language.getString("cancel"),
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});

						final AlertDialog alert = builder.create();
						alert.show();

						rename.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertDialog.Builder dialog = new AlertDialog.Builder(
										context);
								dialog.setTitle(language.getString("rename"));
								final EditText editText = new EditText(context
										.getWindow().getContext());

//								editText.setText(file.getName().toString());
								
								String[] str1;
								String ten=file.getName().toString();


								if(file.isFile()){
									str1=file.getName().toString().split("\\.");
									if(str1.length>0){
										duoi=str1[str1.length-1];
										ten=file.getName().toString().substring(0, file.getName().toString().length()-(duoi.length()+1));
										editText.setText(ten);
									}else{
										editText.setText(file.getName().toString());
									}

								}else{
									editText.setText(file.getName().toString());
								}

								dialog.setView(editText);

								dialog.setPositiveButton(language.getString("ok"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
//										FTPUtil.renameFolder(file.getName()
//												.toString(), editText.getText()
//												.toString(), context.tempPath);
//										if(file.isFile()){
//											FTPUtil.renameFolder(file.getName()
//												.toString(), editText.getText()
//												.toString()+"."+duoi, context.tempPath);
//										}
//										else{
//											FTPUtil.renameFolder(file.getName()
//													.toString(), editText.getText()
//													.toString(), context.tempPath);	
//										}
										
										if(file.isFile()){
											boolean flagEqual=false;
											for (int i = 0; i < list.length; i++) {
												if((editText.getText().toString()+"."+duoi).equals(list[i].getName().toString())){
													AlertDialog.Builder builder = new AlertDialog.Builder(context);
													builder.setTitle(language.getString("warning"));
													builder.setMessage(language.getString("existfile"));
													builder.setPositiveButton(language.getString("cancel"), new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
														}
													});
													AlertDialog dialoga = builder.create();
													dialoga.show();
													
													flagEqual=true;
													break;
												}
											}
											if(!flagEqual){
												FTPUtil.renameFolder(file.getName()
														.toString(), editText.getText()
														.toString()+"."+duoi, context.tempPath);
											}
										}else{
											boolean flagEqual=false;
											for (int i = 0; i < list.length; i++) {
												if((editText.getText().toString()).equals(list[i].getName().toString())){
													AlertDialog.Builder builder = new AlertDialog.Builder(context);
													builder.setTitle(language.getString("warning"));
													builder.setMessage(language.getString("existfolder"));
													builder.setCancelable(false);
													builder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.dismiss();
														}
													});
													builder.show();
													flagEqual=true;
													break;
												}
											}
											if(!flagEqual){
												FTPUtil.renameFolder(file.getName()
														.toString(), editText.getText()
														.toString(), context.tempPath);	
											}
										}
										
										alert.cancel();
										context.loadListFiles(context.tempPath);
									}
								});

								dialog.setNegativeButton(language.getString("cancel"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

								AlertDialog alert2 = dialog.create();
								alert2.show();
							}
						});

						delete.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
										context);
								alertDialog.setTitle(language.getString("delete"));
								if (file.isFile()) {
									alertDialog
									.setMessage(language.getString("areyousuredeletefile")+" "+ file.getName().toString()+"?");
								} else if (file.isDirectory()) {
									alertDialog
									.setMessage(language.getString("areyousuredeletefolder")+" "+ file.getName().toString()+"?");
								}
								alertDialog.setPositiveButton(language.getString("ok"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										FTPUtil.deletFolder(file, context.tempPath);
										
										alert.cancel();
										context.loadListFiles(context.tempPath);
									}
								});

								alertDialog.setNegativeButton(language.getString("cancel"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
								AlertDialog alert3 = alertDialog.create();
								alert3.show();
							}
						});

						detail.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								LayoutInflater inflater = (LayoutInflater) context
								.getApplicationContext().getSystemService(
										context.LAYOUT_INFLATER_SERVICE);
								View viewDelete = inflater.inflate(R.layout.deletedetail, null);

								final TextView fname = (TextView)viewDelete.findViewById(R.id.idfilename);
								final TextView ftype = (TextView)viewDelete.findViewById(R.id.idtypefile);
								final TextView fsize = (TextView)viewDelete.findViewById(R.id.idsize);
								final TextView fmodify = (TextView)viewDelete.findViewById(R.id.idmodifydate);

								fname.setText(language.getString("filename")+":");
								ftype.setText(language.getString("filetype")+":");
								fsize.setText(language.getString("filesize")+":");
								fmodify.setText(language.getString("lastmodify")+":");

								final TextView vfname = (TextView)viewDelete.findViewById(R.id.valuenamefile);
								final TextView vftype = (TextView)viewDelete.findViewById(R.id.valuetypefile);
								final TextView vfSize = (TextView)viewDelete.findViewById(R.id.valuesize);
								final TextView vfModify = (TextView)viewDelete.findViewById(R.id.valuemodifydate);

								vfname.setText(file.getName().toString());
								if(file.isDirectory()){
									vftype.setText(language.getString("folder"));
								}else if(file.isFile()){
									vftype.setText(language.getString("file"));
								}

								if(file.isFile()){
									vfSize.setText(file.getSize() + " bytes");
								}else if(file.isDirectory()){
									vfSize.setText(FTPUtil.totalSize(file,context.tempPath) + " bytes");
								}

								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								for (FTPFile file1 : list) {
									file1 = list[position];
									Date date = file1.getTimestamp().getTime();
									String s = sdf.format(date);
									vfModify.setText(s);
								}

								AlertDialog.Builder builderDetail = new AlertDialog.Builder(context);
								builderDetail.setTitle(file.getName().toString()+" "+language.getString("properties"));
								builderDetail.setView(viewDelete);

								builderDetail.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {

									}
								});
								AlertDialog al = builderDetail.create();
								al.show();
							}
						});

						

						return false;
					}
				});
			}else if(isFlagChangeView() == true){
				
				LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.adapterchangeviewdownload,null);
				final FTPFile file = list[position];
				final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageFile);
				final CheckBox checkBox = (CheckBox) convertView
				.findViewById(R.id.itemCheckBox);
				final TextView txtName = (TextView) convertView
				.findViewById(R.id.namefile1);
				final RelativeLayout linerFile = (RelativeLayout) convertView
				.findViewById(R.id.linerFile);
				
				if(file.isFile()){
//					imageView.setImageResource(R.drawable.file);
					String[] str1;

					str1=file.getName().toString().split("\\.");
					if(str1.length>0){
						duoi=str1[str1.length-1];
					}
					if(duoi.equalsIgnoreCase("png") || duoi.equalsIgnoreCase("jpg") || duoi.equalsIgnoreCase("gif") || duoi.equalsIgnoreCase("bmp")){
						imageView.setImageResource(R.drawable.last_images);
					}else if(duoi.equalsIgnoreCase("exe")){
						imageView.setImageResource(R.drawable.last_unknow);
					}
					else if(duoi.equalsIgnoreCase("apk")){
						imageView.setImageResource(R.drawable.last_setup);
					}else if(duoi.equalsIgnoreCase("mp3")){
						imageView.setImageResource(R.drawable.last_mp3);
					}else if(duoi.equalsIgnoreCase("xml")){
						imageView.setImageResource(R.drawable.last_xml);
					}else if(duoi.equalsIgnoreCase("html")){
						imageView.setImageResource(R.drawable.last_html);
					}else if(duoi.equalsIgnoreCase("zip") || duoi.equalsIgnoreCase("rar")){
						imageView.setImageResource(R.drawable.last_winrar);
					}else if(duoi.equalsIgnoreCase("doc") || duoi.equalsIgnoreCase("docx")){
						imageView.setImageResource(R.drawable.last_word);
					}else if(duoi.equalsIgnoreCase("pdf")){
						imageView.setImageResource(R.drawable.last_pdf);
					}
					else if(duoi.equalsIgnoreCase("txt")){
						imageView.setImageResource(R.drawable.last_txt);
					}
					else if(duoi.equalsIgnoreCase("swf")){
						imageView.setImageResource(R.drawable.last_swf);
					}else if(duoi.equalsIgnoreCase("ppt") || duoi.equalsIgnoreCase("pptx")){
						imageView.setImageResource(R.drawable.last_powerpoint);
					}else if(duoi.equalsIgnoreCase("xls") || duoi.equalsIgnoreCase("xlsx")){
						imageView.setImageResource(R.drawable.last_excel);
					}else if(duoi.equalsIgnoreCase("accdb")){
						imageView.setImageResource(R.drawable.last_access);
					}
					else if(duoi.equalsIgnoreCase("avi")){
						imageView.setImageResource(R.drawable.last_video);
					}
					else{
						imageView.setImageResource(R.drawable.last_unknow);	
					}
				}else if(file.isDirectory()){
					imageView.setImageResource(R.drawable.last_folder2);
				}
				
				txtName.setText(file.getName());
				if (file.isFile()) {
					txtName.setTextColor(Color.parseColor("#FFFFFF"));
				} else if (file.isDirectory()) {
					txtName.setTextColor(Color.parseColor("#FFFF00"));
				}

				// System.out.println(position + " - " + file.getName());
				int co = 0;
				for (int i = 0; i < listCheck.size(); i++) {
					if (file.getName().equals(listCheck.get(i).getName())) {
						checkBox.setChecked(true);
						co = 1;
					}
				}
				if (checkBox.isChecked()) {
					if (co == 0) {
						checkBox.setChecked(false);
					}
				}
				
				checkBox.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (checkBox.isChecked()) {
							listCheck.add(file);
						} else {
							for (int i = 0; i < listCheck.size(); i++) {
								if (file.getName().equals(listCheck.get(i).getName())) {
									listCheck.remove(i);
								}
							}

						}

					}
				});
				
				linerFile.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(flag!=null){
							flag.setBackgroundColor(Color.parseColor("#000000"));
						}

						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							linerFile.setBackgroundColor(Color.parseColor("#ff6600"));
						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							linerFile.setBackgroundColor(Color.parseColor("#ff6600"));
						}
						flag=linerFile;
						return false;
					}
				});

				linerFile.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						context.loadLocation(file);
					}
				});
				
				linerFile.setOnLongClickListener(new View.OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {

						@SuppressWarnings("static-access")
						LayoutInflater inflater = (LayoutInflater) context
						.getApplicationContext().getSystemService(
								context.LAYOUT_INFLATER_SERVICE);
						View view = inflater.inflate(R.layout.alertlongclick, null);

//						final TextView copy = (TextView) view.findViewById(R.id.idcopy);
						final TextView rename = (TextView) view
						.findViewById(R.id.idrename);
						final TextView delete = (TextView) view
						.findViewById(R.id.iddelete);
						final TextView detail = (TextView)view.findViewById(R.id.iddetail);

//						copy.setText("Copy");
						rename.setText(language.getString("rename"));
						delete.setText(language.getString("delete"));
						detail.setText(language.getString("detail"));

						AlertDialog.Builder builder = new AlertDialog.Builder(context);
						builder.setTitle(language.getString("choose"));
						builder.setView(view);
						
						// dialog parent
						builder.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
							}
						});

						final AlertDialog alert = builder.create();
						alert.show();

						// rename
						rename.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								AlertDialog.Builder dialog = new AlertDialog.Builder(
										context);
								dialog.setTitle(language.getString("rename"));
								final EditText editText = new EditText(context
										.getWindow().getContext());

//								editText.setText(file.getName().toString());

								String[] str1;
								String ten=file.getName().toString();
								
								if(file.isFile()){
									str1=file.getName().toString().split("\\.");
									if(str1.length>0){
										duoi=str1[str1.length-1];
										ten=file.getName().toString().substring(0, file.getName().toString().length()-(duoi.length()+1));
										editText.setText(ten);
									}else{
										editText.setText(file.getName().toString());
									}

								}else{
									editText.setText(file.getName().toString());
								}
								dialog.setView(editText);

								dialog.setPositiveButton(language.getString("ok"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
//										if(file.isFile()){
//											FTPUtil.renameFolder(file.getName()
//												.toString(), editText.getText()
//												.toString()+"."+duoi, context.tempPath);
//										}
//										else{
//											FTPUtil.renameFolder(file.getName()
//													.toString(), editText.getText()
//													.toString(), context.tempPath);	
//										}
										if(file.isFile()){
											boolean flagEqual=false;
											for (int i = 0; i < list.length; i++) {
												if((editText.getText().toString()+"."+duoi).equals(list[i].getName().toString())){
													AlertDialog.Builder builder = new AlertDialog.Builder(context);
													builder.setTitle(language.getString("warning"));
													builder.setMessage(language.getString("existfile"));
													builder.setPositiveButton(language.getString("cancel"), new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
														}
													});
													AlertDialog dialoga = builder.create();
													dialoga.show();
													
													flagEqual=true;
													break;
												}
											}
											if(!flagEqual){
												FTPUtil.renameFolder(file.getName()
														.toString(), editText.getText()
														.toString()+"."+duoi, context.tempPath);
											}
										}else{
											boolean flagEqual=false;
											for (int i = 0; i < list.length; i++) {
												if((editText.getText().toString()).equals(list[i].getName().toString())){
													AlertDialog.Builder builder = new AlertDialog.Builder(context);
													builder.setTitle(language.getString("warning"));
													builder.setMessage(language.getString("existfolder"));
													builder.setCancelable(false);
													builder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {
														public void onClick(DialogInterface dialog, int which) {
															dialog.dismiss();
														}
													});
													builder.show();
													flagEqual=true;
													break;
												}
											}
											if(!flagEqual){
												FTPUtil.renameFolder(file.getName()
														.toString(), editText.getText()
														.toString(), context.tempPath);	
											}
										}
										
										alert.cancel();
										context.loadListFiles(context.tempPath);
									}
								});

								dialog.setNegativeButton(language.getString("cancel"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});

								AlertDialog alert2 = dialog.create();
								alert2.show();
							}
						});
						
						// delete 
						delete.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(
										context);
								alertDialog.setTitle(language.getString("delete"));
								if (file.isFile()) {
									alertDialog
									.setMessage(language.getString("areyousuredeletefile")+" "+ file.getName().toString()+"?");
								} else if (file.isDirectory()) {
									alertDialog
									.setMessage(language.getString("areyousuredeletefolder")+" "+ file.getName().toString()+"?");
								}
								alertDialog.setPositiveButton(language.getString("ok"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										FTPUtil.deletFolder(file, context.tempPath);
										
										alert.cancel();
										context.loadListFiles(context.tempPath);
									}
								});

								alertDialog.setNegativeButton(language.getString("cancel"),
										new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
								AlertDialog alert3 = alertDialog.create();
								alert3.show();
							}
						});
						
						// detail
						detail.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								LayoutInflater inflater = (LayoutInflater) context
								.getApplicationContext().getSystemService(
										context.LAYOUT_INFLATER_SERVICE);
								View viewDelete = inflater.inflate(R.layout.deletedetail, null);

								final TextView fname = (TextView)viewDelete.findViewById(R.id.idfilename);
								final TextView ftype = (TextView)viewDelete.findViewById(R.id.idtypefile);
								final TextView fsize = (TextView)viewDelete.findViewById(R.id.idsize);
								final TextView fmodify = (TextView)viewDelete.findViewById(R.id.idmodifydate);

								fname.setText(language.getString("filename")+":");
								ftype.setText(language.getString("filetype")+":");
								fsize.setText(language.getString("filesize")+":");
								fmodify.setText(language.getString("lastmodify")+":");

								final TextView vfname = (TextView)viewDelete.findViewById(R.id.valuenamefile);
								final TextView vftype = (TextView)viewDelete.findViewById(R.id.valuetypefile);
								final TextView vfSize = (TextView)viewDelete.findViewById(R.id.valuesize);
								final TextView vfModify = (TextView)viewDelete.findViewById(R.id.valuemodifydate);

								vfname.setText(file.getName().toString());
								if(file.isDirectory()){
									vftype.setText(language.getString("folder"));
								}else if(file.isFile()){
									vftype.setText(language.getString("file"));
								}

								if(file.isFile()){
									vfSize.setText(file.getSize() + " bytes");
								}else if(file.isDirectory()){
									vfSize.setText(FTPUtil.totalSize(file,context.tempPath) + " bytes");
								}

								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								for (FTPFile file1 : list) {
									file1 = list[position];
									Date date = file1.getTimestamp().getTime();
									String s = sdf.format(date);
									vfModify.setText(s);
								}

								AlertDialog.Builder builderDetail = new AlertDialog.Builder(context);
								builderDetail.setTitle(file.getName().toString()+" "+language.getString("properties"));
								builderDetail.setView(viewDelete);

								builderDetail.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								AlertDialog al = builderDetail.create();
								al.show();						
							}
						});

						

						return false;
					}
				});
			//}
		}

		return convertView;
	}

	public List<FTPFile> getListCheck() {
		return listCheck;
	}

	public void setListCheck(List<FTPFile> listCheck) {
		this.listCheck = listCheck;
	}

	public boolean isFlagChangeView() {
		
		return flagChangeView;
	}


	public void setFlagChangeView(boolean flagChangeView) {
		this.flagChangeView = flagChangeView;
	}


	@Override
	public void onClick(View v) {
	}

	public void delete(String path) {
		FTPClient client = new FTPClient();
		try {
			client.deleteFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
