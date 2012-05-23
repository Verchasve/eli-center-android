package com.eli.filemanager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import fpt.util.FTPUtil;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChangeViewAdapter extends BaseAdapter{
	
	FTPFile[] list = new FTPFile[1];
	List<FTPFile> listCheck = new ArrayList<FTPFile>();
	DownloadActivity context;
	RelativeLayout flag=null;
	

	public ChangeViewAdapter() {

	}

	public ChangeViewAdapter(FTPFile[] list, DownloadActivity context) {
		super();
		this.list = list;
		this.context = context;
	}
	
	@Override
	public int getCount() {
		return list.length;
	}

	@Override
	public Object getItem(int position) {
		return list[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.adapterchangeviewdownload, null);
		}
		final FTPFile file = list[position];
		final ImageView imageView = (ImageView)convertView.findViewById(R.id.imageFile);
		final CheckBox checkBox = (CheckBox) convertView
		.findViewById(R.id.itemCheckBox);
		final TextView txtName = (TextView) convertView
		.findViewById(R.id.namefile1);
		final RelativeLayout linerFile = (RelativeLayout) convertView
		.findViewById(R.id.linerFile);
		
		txtName.setText(file.getName());
		if (file.isFile() && !(file.getName().toString().equals("../"))) {
			checkBox.setVisibility(View.VISIBLE);
			txtName.setTextColor(Color.parseColor("#FFFFFF"));
		} else if (file.isDirectory()
				|| (file.getName().toString().equals("../"))) {
			if ((file.getName().toString().equals("../"))) {
				checkBox.setVisibility(View.INVISIBLE);
				checkBox.setWidth(0);
			} else {
				checkBox.setVisibility(View.VISIBLE);
			}
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
				// v.setBackgroundColor(Color.argb(0x80, 0x20, 0xa0, 0x40));


//				context.loadLocationChangeView(file);
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

//				final TextView copy = (TextView) view.findViewById(R.id.idcopy);
				final TextView rename = (TextView) view
				.findViewById(R.id.idrename);
				final TextView delete = (TextView) view
				.findViewById(R.id.iddelete);
				final TextView detail = (TextView)view.findViewById(R.id.iddetail);

//				copy.setText("Copy");
				rename.setText("Rename");
				delete.setText("Delete");
				detail.setText("Detail");

				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setTitle("Choose");
				builder.setView(view);

				// rename
				rename.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(
								context);
						dialog.setTitle("Rename");
						final EditText editText = new EditText(context
								.getWindow().getContext());

						editText.setText(file.getName().toString());
						dialog.setView(editText);

						dialog.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								FTPUtil.renameFolder(file.getName()
										.toString(), editText.getText()
										.toString(), context.tempPath);
								context.loadListFiles(context.tempPath);
							}
						});

						dialog.setNegativeButton("Cancel",
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
						alertDialog.setTitle("Delete");
						if (file.isFile()) {
							alertDialog
							.setMessage("Are you sure you want to delete this "
									+ file.getName().toString()
									+ " file?");
						} else if (file.isDirectory()) {
							alertDialog
							.setMessage("Are you sure you want to delete this "
									+ file.getName().toString()
									+ " folder?");
						}
						alertDialog.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String fileNameDelete = file.getName()
								.toString();
								//
								//										if (file.isFile()) {
								//											FTPUtil.ftpDeleteFile(fileNameDelete);
								//											context.loadListFiles(context.tempPath);
								//
								//										} else if (file.isDirectory()) {
								//											FTPUtil.ftpDeleteFolder(fileNameDelete);
								//											context.loadListFiles(context.tempPath);
								//										}

//								FTPUtil.deletFolder(fileNameDelete, context.tempPath);
								context.loadListFiles(context.tempPath);
							}
						});

						alertDialog.setNegativeButton("Cancel",
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

						fname.setText("File name:");
						ftype.setText("Type of file:");
						fsize.setText("Size:");
						fmodify.setText("Last modified:");

						final TextView vfname = (TextView)viewDelete.findViewById(R.id.valuenamefile);
						final TextView vftype = (TextView)viewDelete.findViewById(R.id.valuetypefile);
						final TextView vfSize = (TextView)viewDelete.findViewById(R.id.valuesize);
						final TextView vfModify = (TextView)viewDelete.findViewById(R.id.valuemodifydate);

						vfname.setText(file.getName().toString());
						if(file.isDirectory()){
							vftype.setText("Folder");
						}else if(file.isFile()){
							vftype.setText("File");
						}

						if(file.isFile()){
							vfSize.setText(file.getSize() + " bytes");
						}else if(file.isDirectory()){
							//	vfSize.setText(file.getSize() + "bytes");
//							float count=0;
//							for(FTPFile f:list){
//
//								if (f.getType() == FTPFile.FILE_TYPE) {
//									count = f.getSize();
//									count ++;
//									vfSize.setText(count + " bytes");
//								}
//							}
							
//							vfSize.setText(dirSize(file, position) + " bytes");
						}

						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						for (FTPFile file1 : list) {
							file1 = list[position];
							Date date = file1.getTimestamp().getTime();
							String s = sdf.format(date);
							vfModify.setText(s);
						}

						AlertDialog.Builder builderDetail = new AlertDialog.Builder(context);
						builderDetail.setTitle(file.getName().toString()+" properties");
						builderDetail.setView(viewDelete);

						builderDetail.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						});
						AlertDialog al = builderDetail.create();
						al.show();						
					}
				});

				// dialog parent
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});

				AlertDialog alert = builder.create();
				alert.show();

				return false;
			}
		});
		
		return convertView;
	}

	public List<FTPFile> getListCheck() {
		return listCheck;
	}

	public void setListCheck(List<FTPFile> listCheck) {
		this.listCheck = listCheck;
	}
	
	public void delete(String path) {
		FTPClient client = new FTPClient();
		try {
			client.deleteFile(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private  long dirSize(FTPFile dir,int position) {
	    long result = 0;
//	    FTPFile fileList = list[position];

	    for(FTPFile file : list) {
	        // Recursive call if it's a directory
	        if(file.isDirectory()) {
	            result += dirSize(file,position);
	        } else {
	            // Sum the file size in bytes
	            result += file.getSize();
	        }
	    }
	    return result; // return the file size
	}
}
