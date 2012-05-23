package com.eli.filemanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.net.ftp.FTPFile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StatFs;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class UploadFileAdapter extends BaseAdapter {
	File[] files = new File[1];
	List<File> listCheck = new ArrayList<File>();
	UploadActivity context;
	RelativeLayout flag=null;
	public static String saveFilePath;
	boolean flagChangeView=false;
	Bundle language;

	public Bundle getLanguage() {
		return language;
	}

	public void setLanguage(Bundle language) {
		this.language = language;
	}

	public UploadFileAdapter() {
	}

	String duoi="";
	 String ten ="";
	public UploadFileAdapter(File[] files, UploadActivity context,boolean flagChangeView,Bundle language) {
		super();
		this.files = files;
		this.context = context;
		this.flagChangeView = flagChangeView;
		this.language=language;
	}

	@Override
	public int getCount() {
		int kq=0;
		if(files!=null){
			kq=files.length;
		}
		return kq;
	}

	@Override
	public Object getItem(int arg0) {

		return files[arg0];
	}

	@Override
	public long getItemId(int arg0) {

		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//		if (convertView == null) {
		//			LayoutInflater inflater = (LayoutInflater) context
		//					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//			convertView = inflater.inflate(R.layout.checkfile, null);
		//		}

		if(isFlagChangeView()==false){
			LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.checkfile, null);
			final File file = files[position];


			final CheckBox checkBox = (CheckBox) convertView
			.findViewById(R.id.checkFile);
			final TextView txtName = (TextView) convertView
			.findViewById(R.id.txtName);
			final TextView txtSize = (TextView) convertView
			.findViewById(R.id.txtSize);
			final RelativeLayout linerFile = (RelativeLayout) convertView
			.findViewById(R.id.linerFile);

			if(file.getName().equals("..|") ){
				txtName.setText("../");
			}else{
				txtName.setText(file.getName());
			}

			if(file.isFile() && !(file.getName().toString().equals("..|"))){
				checkBox.setVisibility(View.VISIBLE);
				txtName.setTextColor(Color.parseColor("#FFFFFF"));
				txtSize.setText(file.length()+" bytes");			
			}else if(file.isDirectory() || (file.getName().toString().equals("..|"))){
				if((file.getName().toString().equals("..|"))){
					checkBox.setVisibility(View.INVISIBLE);
				}else{
					checkBox.setVisibility(View.VISIBLE);
				}
				txtName.setTextColor(Color.parseColor("#FFFF00"));
				txtSize.setText("");
			}
			int co=0;
			for(int i=0;i<listCheck.size();i++){
				if(file.getName().equals(listCheck.get(i).getName())){
					checkBox.setChecked(true);
					co=1;
				}
			}
			if(checkBox.isChecked()){
				if(co==0){
					checkBox.setChecked(false);
				}
			}
			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (checkBox.isChecked()) {
						listCheck.add(file);
					} else {
						System.out.println(file.getName());
						for(int i=0;i<listCheck.size();i++){
							if(file.getName().equals(listCheck.get(i).getName())){
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
				public void onClick(View arg0) {
					if (!file.isFile()) {
						context.loadLocation(file.getName());					

					}
				}
			});

			linerFile.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					@SuppressWarnings("static-access")
					LayoutInflater inflater = (LayoutInflater) context
					.getApplicationContext().getSystemService(
							context.LAYOUT_INFLATER_SERVICE);
					View view = inflater.inflate(R.layout.alertlongclickupload, null);

					//					final TextView copy = (TextView) view.findViewById(R.id.iduploadcopy);
					final TextView rename = (TextView) view
					.findViewById(R.id.idrenameCopy);
					final TextView delete = (TextView) view
					.findViewById(R.id.iddeleteCopy);
					final TextView detail = (TextView)view.findViewById(R.id.iddetailCopy);

					//					copy.setText(language.getString("copy"));
					rename.setText(language.getString("rename"));
					delete.setText(language.getString("delete"));
					detail.setText(language.getString("detail"));

					final AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle(language.getString("choose"));
					builder.setView(view);



					builder.setNegativeButton(language.getString("cancel"),
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {

						}
					});

					final AlertDialog aletbuilder = builder.create();
					aletbuilder.show();
					// rename
					rename.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							AlertDialog.Builder dialogRename = new AlertDialog.Builder(
									context);
							dialogRename.setTitle(language.getString("rename"));
							final EditText editText = new EditText(context
									.getWindow().getContext());
							String[] str1;
							ten=file.getName().toString();


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

							dialogRename.setView(editText);
							
							

							dialogRename.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									File from = new File(context.tempPath, file.getName().toString());
//									File to = new File(context.tempPath,editText.getText().toString());
									File to = null;
									if(file.isFile()){
										boolean flagEqual=false;
										for (int i = 0; i < files.length; i++) {
											if((editText.getText().toString()+"."+duoi).equals(files[i].getName().toString())){
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
											to = new File(context.tempPath,editText.getText().toString()+"."+duoi);
											from.renameTo(to);
										}
										
									}else{
										boolean flagEqual=false;
										for (int i = 0; i < files.length; i++) {
											if((editText.getText().toString()).equals(files[i].getName().toString())){
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
											to = new File(context.tempPath,editText.getText().toString());
											from.renameTo(to);
										}
									}
//									from.renameTo(to);
									aletbuilder.cancel();
									context.loadListFiles(context.tempPath);
								}
							});
							dialogRename.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});

							AlertDialog alertRename = dialogRename.create();
							alertRename.show();

						}
					});

					// delete
					delete.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
							dialogBuilder.setTitle(language.getString("delete"));
							if (file.isFile()) {
								dialogBuilder
								.setMessage(language.getString("areyousuredeletefile")+" "+ file.getName().toString()+"?");
							} else if (file.isDirectory()) {
								dialogBuilder
								.setMessage(language.getString("areyousuredeletefolder")+" "+ file.getName().toString()+"?");
							}

							dialogBuilder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									deleteRecursive(file);
									aletbuilder.cancel();
									context.loadListFiles(context.tempPath);

								}
							});
							dialogBuilder.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							});

							AlertDialog al = dialogBuilder.create();
							al.show();
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
								vfSize.setText(file.length() + " bytes") ;
							}else{
								vfSize.setText(getTotalSize(file) + " bytes");
							}


							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							for(File file : files){
								file = files[position];
								Date date = new Date(file.lastModified());
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
					//end detail

					// start copy


					//					copy.setOnClickListener(new View.OnClickListener() {
					//
					//						@Override
					//						public void onClick(View v) {
					//							saveFilePath = context.tempPath +"/" + file.getName();
					//							System.out.println("Save file path : " + saveFilePath);
					//							aletbuilder.cancel();
					//						}
					//					});
					// end copy

					return false;
				}
			});
		}
		else if(isFlagChangeView()==true){
			LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.adapterchangeviewdownload,null);
			final File file = files[position];
			final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageFile);
			final CheckBox checkBox = (CheckBox) convertView
			.findViewById(R.id.itemCheckBox);
			final TextView txtName = (TextView) convertView
			.findViewById(R.id.namefile1);
			final RelativeLayout linerFile = (RelativeLayout) convertView
			.findViewById(R.id.linerFile);

			if(file.isFile()){
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
				imageView.setImageResource(R.drawable.last_folder);
			}

			if(file.getName().equals("..|") ){
				txtName.setText("../");
			}else{
				txtName.setText(file.getName());
			}


			if(file.isFile() && !(file.getName().toString().equals("..|"))){
				checkBox.setVisibility(View.VISIBLE);
				txtName.setTextColor(Color.parseColor("#FFFFFF"));
			}else if(file.isDirectory() || (file.getName().toString().equals("..|"))){
				if((file.getName().toString().equals("..|"))){
					checkBox.setVisibility(View.INVISIBLE);
				}else{
					checkBox.setVisibility(View.VISIBLE);
				}
				txtName.setTextColor(Color.parseColor("#FFFF00"));
			}
			int co=0;
			for(int i=0;i<listCheck.size();i++){
				if(file.getName().equals(listCheck.get(i).getName())){
					checkBox.setChecked(true);
					co=1;
				}
			}
			if(checkBox.isChecked()){
				if(co==0){
					checkBox.setChecked(false);
				}
			}
			checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// System.out.prln("checkadapter");
					if (checkBox.isChecked()) {
						listCheck.add(file);
					} else {
						System.out.println(file.getName());
						for(int i=0;i<listCheck.size();i++){
							if(file.getName().equals(listCheck.get(i).getName())){
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
				public void onClick(View arg0) {
					// System.out.prln("file.getName(): "+file.getName());
					if (!file.isFile()) {
						context.loadLocation(file.getName());					

					}
				}
			});

			linerFile.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					@SuppressWarnings("static-access")
					LayoutInflater inflater = (LayoutInflater) context
					.getApplicationContext().getSystemService(
							context.LAYOUT_INFLATER_SERVICE);
					View view = inflater.inflate(R.layout.alertlongclickupload, null);

					//					final TextView copy = (TextView) view.findViewById(R.id.iduploadcopy);
					final TextView rename = (TextView) view
					.findViewById(R.id.idrenameCopy);
					final TextView delete = (TextView) view
					.findViewById(R.id.iddeleteCopy);
					final TextView detail = (TextView)view.findViewById(R.id.iddetailCopy);

					//					copy.setText(language.getString("copy"));
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
					final AlertDialog aletbuilder = builder.create();
					aletbuilder.show();

					// rename
					rename.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							AlertDialog.Builder dialog = new AlertDialog.Builder(
									context);
							dialog.setTitle(language.getString("rename"));
							final EditText editText = new EditText(context
									.getWindow().getContext());

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


							//							editText.setText(file.getName().toString());
							dialog.setView(editText);

							dialog.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {

//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									//									File from = new File(context.tempPath, file.getName().toString());
//									//									File to = new File(context.tempPath,editText.getText().toString());
//									//
//									//									from.renameTo(to);
//									//									aletbuilder.cancel();
//									//									context.loadListFiles(context.tempPath);
//									File from = new File(context.tempPath, file.getName().toString());
//
//									File to = new File(context.tempPath,editText.getText().toString());
//									if(file.isFile()){
//										to = new File(context.tempPath,editText.getText().toString()+"."+duoi);
//									}
//									from.renameTo(to);
//									aletbuilder.cancel();
//									context.loadListFiles(context.tempPath);
//								}
								@Override
								public void onClick(DialogInterface dialog, int which) {
									File from = new File(context.tempPath, file.getName().toString());
//									File to = new File(context.tempPath,editText.getText().toString());
									File to = null;
									if(file.isFile()){
										boolean flagEqual=false;
										for (int i = 0; i < files.length; i++) {
											if((editText.getText().toString()+"."+duoi).equals(files[i].getName().toString())){
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
											to = new File(context.tempPath,editText.getText().toString()+"."+duoi);
											from.renameTo(to);
										}
										
									}else{
										boolean flagEqual=false;
										for (int i = 0; i < files.length; i++) {
											if((editText.getText().toString()).equals(files[i].getName().toString())){
												AlertDialog.Builder builder = new AlertDialog.Builder(context);
												builder.setTitle(language.getString("warning"));
												builder.setMessage(language.getString("existFolder"));
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
											to = new File(context.tempPath,editText.getText().toString());
											from.renameTo(to);
										}
									}
//									from.renameTo(to);
									aletbuilder.cancel();
									context.loadListFiles(context.tempPath);
								}
							});
							dialog.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							});

							AlertDialog alertRename = dialog.create();
							alertRename.show();
						}
					});

					// delete
					delete.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
							dialogBuilder.setTitle(language.getString("delete"));
							if (file.isFile()) {
								dialogBuilder
								.setMessage(language.getString("areyousuredeletefile")+" "+ file.getName().toString()+"?");
							} else if (file.isDirectory()) {
								dialogBuilder
								.setMessage(language.getString("areyousuredeletefolder")+" "+ file.getName().toString()+"?");
							}

							dialogBuilder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

									deleteRecursive(file);
									aletbuilder.cancel();
									context.loadListFiles(context.tempPath);
								}
							});
							dialogBuilder.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							});

							AlertDialog al = dialogBuilder.create();
							al.show();
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
								vfSize.setText(file.length() + " bytes") ;
							}else{
								vfSize.setText(getTotalSize(file) + " bytes");
							}


							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							for(File file : files){
								file = files[position];
								Date date = new Date(file.lastModified());
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
					//end detail



					return false;
				}
			});
		}
		return convertView;
	}

	public List<File> getListCheck() {
		return listCheck;
	}

	public void setListCheck(List<File> listCheck) {
		this.listCheck = listCheck;
	}

	public long getTotalSize(File file) {
		//		StatFs stat = new StatFs(file.getPath());
		//		long blockSize = stat.getBlockSize();
		//		long totalBlocks = stat.getBlockCount();
		//		return totalBlocks * blockSize;

		long result = 0;
		File[] fileList = file.listFiles();

		for(int i = 0; i < fileList.length; i++) {
			if(fileList[i].isDirectory()) {
				result += getTotalSize(fileList [i]);
			} else {
				result += fileList[i].length();
			}
		}
		return result; 
	}


	public void deleteRecursive(File dir)
	{
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) 
			{
				File temp =  new File(dir, children[i]);
				if(temp.isDirectory())
				{
					deleteRecursive(temp);
				}
				else
				{
					boolean b = temp.delete();
					if(b == false)
					{
						Log.d("DeleteRecursive", "DELETE FAIL");
					}
				}
			}

			dir.delete();
		}
		else if(dir.isFile()){
			dir.delete();
		}
	}

	public boolean isFlagChangeView() {
		return flagChangeView;
	}

	public void setFlagChangeView(boolean flagChangeView) {
		this.flagChangeView = flagChangeView;
	}

	public String getSaveFilePath() {
		return saveFilePath;
	}

	public void setSaveFilePath(String saveFilePath) {
		this.saveFilePath = saveFilePath;
	}


}
