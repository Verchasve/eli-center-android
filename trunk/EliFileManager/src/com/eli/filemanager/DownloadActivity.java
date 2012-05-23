package com.eli.filemanager;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import fpt.util.FTPUtil;
import ftp.pojo.FtpConnect;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DownloadActivity extends Activity {

	public static String HOST = "www.elisoft.co.kr";
	public static int PORT = 21;
	public static String USER = "elisoft";
	public static String PASS = "7890";

	static String USERNAME = "";
	static String UPLOAD_FOLDER = "upload";
	static String DOWNLOAD_FOLDER = "dowload";
	static String MY_FILES = "myfiles";
	static FTPClient client = new FTPClient();

	////////////////////////
	String lang = "EN";
	static Bundle language;
	public int co=0;


	static FtpConnect ftp = new FtpConnect();
	Button bntDownload;
//	Button bntNew;
	Button  bntGo;
	EditText locaton;
	TextView tb,tvwaiting;
//	public static boolean flagChangeViewDownload = false;


	public String getLang() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			lang = extras.getString("lang");
		}
		return lang;
	}

	ListView listFile;
	GridView gridview;
	CheckFileAdapter adapter;
	String tempPath="/";

	static List<FTPFile> lstexit= new ArrayList<FTPFile>();
	static List<FTPFile> lstok= new ArrayList<FTPFile>();
	static long sizelstok=0;
	
	//process bar
	static ProgressDialog progressBar;
	private static int progressBarStatus = 0;
	private static Handler progressBarHandler = new Handler();
	private static long fileSizeTotal = 0;
	
	//size width, height
	int screenheight,screenwidth;

	static AlertDialog.Builder builder;
	static AlertDialog.Builder builder2;
	CheckFileAdapter adapter1;

	boolean flagsort=true;
	
	//wait
	private LinearLayout linProgressBar;
    private final Handler uiHandler=new Handler();
    private boolean isUpdateRequired=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download);

		readFile();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenheight = displaymetrics.heightPixels;
        screenwidth = displaymetrics.widthPixels;

		language = getIntent().getExtras().getBundle(getLang());
		locaton = (EditText) findViewById(R.id.locaton);
		bntGo = (Button) findViewById(R.id.buttonGo);
		tb=(TextView)findViewById(R.id.tv_tb);
//		bntGo.setText(language.getString("go"));
//		bntNew = (Button) findViewById(R.id.bntNew);
//		bntNew.setText(language.getString("newfolder"));
		bntDownload = (Button) findViewById(R.id.buttonDownload);
//		bntDownload.setText(language.getString("download"));

//		FtpClientActivity.flagChangeViewDownload = getIntent().getExtras().getBoolean("flagChangeViewDownload");
		// Intent currentIntent = this.getIntent();
		ftp = (FtpConnect) getIntent().getExtras().get("ftp");

		locaton.setWidth(screenwidth-110);
		locaton.setText(tempPath);
		
		linProgressBar = (LinearLayout) findViewById(R.id.lin_progress_bar);
		tvwaiting=(TextView)findViewById(R.id.tv_waiting);
		linProgressBar.layout(screenwidth/4,screenheight-( screenheight/3 + 80), 0, screenheight/3);
		tvwaiting.setText(language.getString("waiting")+"...");
		linProgressBar.setVisibility(View.INVISIBLE);
		
		listFile = (ListView) findViewById(R.id.lstFile);
		gridview = (GridView)findViewById(R.id.grid);
		
		int kq=screenwidth/135;
		gridview.setNumColumns(kq);
		
		loadListFiles(tempPath);
		bntDownload.setOnClickListener(downloadEvent);
		// event new folder
//		bntNew.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				if(co==0){
//					co=1;
//					newfolderDialog();
//				}
//				/*
//				 * String temp = locaton.getText().toString(); if
//				 * ("/".equals(temp)) FTPUtil.ftpCreateFolder(temp + "TuanAnh");
//				 * else FTPUtil.ftpCreateFolder(temp + "/TuanAnh");
//				 * loadListFiles(temp);
//				 */
//
//			}
//		});
		
		// event go
		bntGo.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("unused")
			@Override
			public void onClick(View arg0) {

				if(!locaton.getText().toString().equals("")){
					if(locaton.getText().toString().equals("/")){
						InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						tempPath=locaton.getText().toString();
						loadListFiles(tempPath);
					}else{
						String[] ss=locaton.getText().toString().split("/");
						String nstr="";
						if(ss!=null && ss.length>0){
							for(int z=0;z<ss.length;z++){
								System.out.println(z+" : "+ss[z]);
								if(z==0){
									nstr=ss[z];
								}else if(z==1){
									if(ss[z].equals("")){
										nstr="/";
										break;
									}else{
										nstr+="/"+ss[z];
									}
								}
								else{
									nstr+="/"+ss[z];
								}
							}
						}
						
						System.out.println("nstr: "+nstr);
						locaton.setText(nstr);
						tempPath=locaton.getText().toString();
						String str=tempPath;

						if(!locaton.getText().toString().equals("/")){
							String[] s=locaton.getText().toString().split("/");
							System.out.println("soluong: "+s.length);
							if(s!=null){
								if(s.length==1){
//									System.out.println("no go: "+s[0]);
									locaton.setText("/");
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									tempPath=locaton.getText().toString();
									loadListFiles(tempPath);
								}else {
									try {
										connects();
										if(client.changeWorkingDirectory(tempPath)){
											InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
											imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
											loadListFiles(tempPath);
										}else{
											int checkgo=0;
											int size=s.length;
											while(size>1 && checkgo==0){
												int lastIndex=str.lastIndexOf("/");
												str=str.substring(0,lastIndex);
												if(client.changeWorkingDirectory(str)){
													InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
													imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
													locaton.setText(str);
													tempPath=str;
													loadListFiles(str);
													checkgo=1;
												}
												size--;
											}
											if(checkgo==0){
												locaton.setText("/");
												tempPath=locaton.getText().toString();
												if(client.changeWorkingDirectory(tempPath)){
													InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
													imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
													loadListFiles(locaton.getText().toString());
//													System.out.println("do ngoc chay vao day");
												}
											}
										}
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}finally{
										closeConnection();
									}
								}
							}else{
								locaton.setText("/");
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
								tempPath=locaton.getText().toString();
								loadListFiles(tempPath);
							}
						}else{
							InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
							imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
							loadListFiles(tempPath);
						}
					}
				}else{
					locaton.setText("/");
					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
					tempPath=locaton.getText().toString();
					loadListFiles(tempPath);
				}


			}
		});

	}

	private OnClickListener downloadEvent = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(co==0){
				co=1;
				if(adapter.getListCheck()!=null && adapter.getListCheck().size()>0){
						downloadDialog();
						
				}else{
						openDialogError();
				}
			}
		}
	};
//	private String temp;
//	private List<FTPFile> names;
//	private String pathSave;

	public void loadLocation(FTPFile path) {
//		String temp = locaton.getText().toString();
//		if("../".equals(path.getName())){
//			int lastIndex=tempPath.lastIndexOf("/");
//			tempPath=tempPath.substring(0,lastIndex);
//			locaton.setText(tempPath);
//			loadListFiles(tempPath);
//		}else{
//			if ("/".equals(temp)) {
//				temp=tempPath+locaton;
//				if (!path.isFile()) {
//					tempPath+=path.getName();
//					locaton.setText(tempPath);				
//					loadListFiles(tempPath);
//				}
//
//			} else {
//				if (!path.isFile()) {
//					tempPath+="/"+path.getName();
//					locaton.setText(tempPath);
//					loadListFiles(tempPath);
//				}
//			}
//		}
		if ("/".equals(tempPath)) {
//			temp=tempPath+locaton;
			if (!path.isFile()) {
				tempPath+=path.getName();
				locaton.setText(tempPath);				
				loadListFiles(tempPath);
			}

		} else {
			if (!path.isFile()) {
				tempPath+="/"+path.getName();
				locaton.setText(tempPath);
				loadListFiles(tempPath);
			}
		}

	}

	// get list upload
	public void loadListFiles(String path) {
		
		FTPFile[] lst = FTPUtil.getListFile(path);
		if(lst!=null ){
			
			if(flagsort==true){
				int i=0;
				int sl=lst.length;
				while(i<sl){
					if(lst[i].isFile()){
						
						for(int j=i+1;j<sl;j++){
							if(lst[j].isFile()){
								if(lst[i].getName().compareToIgnoreCase(lst[j].getName())>0){
									FTPFile tem=lst[i];
									lst[i]=lst[j];
									lst[j]=tem;
								}
							}
						}
						i++;
					}else if(lst[i].isDirectory()){
						int vt=i;
						while(vt<sl && lst[vt].isDirectory()){
							vt++;
						}
						if(vt==sl-1){
							FTPFile tem=lst[i];
							lst[i]=lst[vt];
							lst[vt]=tem;
						}else if(vt==sl){
							for(int k=i;k<sl-1;k++){
								for(int h=k+1;h<sl;h++){
									if(lst[h].getName().compareToIgnoreCase(lst[k].getName())<0){
										FTPFile tt=lst[k];
										lst[k]=lst[h];
										lst[h]=tt;
									}
								}
							}
							i=lst.length;
						}else if(vt<sl-1){
							FTPFile min=lst[vt];
							for(int z=vt;z<sl;z++){
								if(lst[z].isFile()){
									if(lst[z].getName().compareToIgnoreCase(min.getName())<0){
										min=lst[z];
										vt=z;
									}
								}
							}
							lst[vt]=lst[i];
							lst[i]=min;
						}
						i++;
					}
				}
				
			}else{
				int i=0;
				int sl=lst.length;
				while(i<sl){
					if(lst[i].isFile()){
						
						for(int j=i+1;j<sl;j++){
							if(lst[j].isFile()){
								if(lst[i].getName().compareToIgnoreCase(lst[j].getName())<0){
									FTPFile tem=lst[i];
									lst[i]=lst[j];
									lst[j]=tem;
								}
							}
						}
						i++;
					}else if(lst[i].isDirectory()){
						int vt=i;
						while(vt<sl && lst[vt].isDirectory()){
							vt++;
						}
						if(vt==sl-1){
							FTPFile tem=lst[i];
							lst[i]=lst[vt];
							lst[vt]=tem;
						}else if(vt==sl){
							for(int k=i;k<sl-1;k++){
								for(int h=k+1;h<sl;h++){
									if(lst[h].getName().compareToIgnoreCase(lst[k].getName())>0){
										FTPFile tt=lst[k];
										lst[k]=lst[h];
										lst[h]=tt;
									}
								}
							}
							i=lst.length;
						}else if(vt<sl-1){
							FTPFile min=lst[vt];
							for(int z=vt;z<sl;z++){
								if(lst[z].isFile()){
									if(lst[z].getName().compareToIgnoreCase(min.getName())>0){
										min=lst[z];
										vt=z;
									}
								}
							}
							lst[vt]=lst[i];
							lst[i]=min;
						}
						i++;
					}
				}
			}
			
			if(lst.length>0){
				tb.setVisibility(View.INVISIBLE);
				tb.setText("");
			}else{
				tb.setVisibility(View.VISIBLE);
				tb.setText(language.getString("folderempty"));
			}
			
		}else{
			tb.setVisibility(View.VISIBLE);
			tb.setText(language.getString("folderempty"));
		}
	
        
        if(FtpClientActivity.flagChangeViewDownload==false){
			adapter = new CheckFileAdapter(lst,this,FtpClientActivity.flagChangeViewDownload,language);
			gridview.setVisibility(View.INVISIBLE);
			listFile.setVisibility(View.VISIBLE);			
			listFile.setAdapter(adapter);
		}else{
			adapter = new CheckFileAdapter(lst,this,FtpClientActivity.flagChangeViewDownload,language);
			listFile.setVisibility(View.INVISIBLE);
			gridview.setVisibility(View.VISIBLE);
			gridview.setAdapter(adapter);
		}
		
	}

	//	public void initloadListFiles(String path) {	
	////		FTPFile back=new FTPFile();
	////		back.setName("../");
	//		FTPFile[] lst = FTPUtil.getListFile(path);
	//		if(lst!=null && tempPath.length()>1){
	////			FTPFile[] temFiles=new FTPFile[lst.length+1];
	////			temFiles[0]=back;
	////			for (int i = 0; i < lst.length; i++) {
	////				temFiles[i+1]=lst[i];
	////			}
	////			lst=temFiles;
	//			
	//			FTPFile[] temFiles=new FTPFile[lst.length];
	//
	//			for (int i = 0; i < lst.length; i++) {
	//				temFiles[i]=lst[i];
	//			}
	//			lst=temFiles;
	//			
	//			
	//			adapter = new CheckFileAdapter(lst,this);
	//
	//			listFile.setAdapter(adapter);
	//		}else{
	//			
	//			bntGo.setOnClickListener(new OnClickListener() {
	//				@Override
	//				public void onClick(View arg0) {
	//					locaton.setText("/");
	//					InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
	//					imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	//					tempPath=locaton.getText().toString();
	//					initloadListFiles(tempPath);
	//
	//				}
	//			});
	//		}
	//
	//		
	//	}

	private void newfolderDialog() {

		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		.getSystemService(LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.newfolder, null);
		final EditText nameFolder = (EditText) view
		.findViewById(R.id.nameFolder);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		
		builder.setPositiveButton(language.getString("save"),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!nameFolder.getText().toString().equals("")){
					co=0;
					linProgressBar.setVisibility(View.VISIBLE);
					
					 try{
				            new Thread(){
				                public void run() {
				                	InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(
											InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									final String temp = locaton.getText().toString();

									if ("/".equals(temp))
										FTPUtil.ftpCreateFolder(temp+ nameFolder.getText().toString());
									else
										FTPUtil.ftpCreateFolder(temp + "/"+ nameFolder.getText().toString());
				                    uiHandler.post( new Runnable(){
				                        @Override
				                        public void run() {
				                            if(isUpdateRequired){
				                                //TODO:
				                            }else{
				                                linProgressBar.setVisibility(View.GONE);
				                                loadListFiles(temp);
				                            }
				                        }
				                    } );
				                }
				        }.start();
				        }catch (Exception e) {}
					
					
					
				}else{
					newfolderDialog();
				}
			}

		});
		builder.setNegativeButton(language.getString("cancel"),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				co=0;
				InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(
						InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void downloadDialog() {

		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		.getSystemService(LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.folders, null);
		final FolderLayout localFolders = (FolderLayout) view.findViewById(R.id.localfolders);
		localFolders.setDir(Environment.getExternalStorageDirectory().toString());

//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder = new AlertDialog.Builder(this);
		builder.setView(view);
		builder.setCancelable(false);
		builder.setPositiveButton(language.getString("save"),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (adapter.getListCheck().size() > 0) {
					lstexit.clear();
					lstok.clear();
					sizelstok=0;
					fileSizeTotal=0;
					
					
			        linProgressBar.setVisibility(View.VISIBLE);
			        try{
			            new Thread(){
			                public void run() {
			                	download(localFolders.getMyPath().getText().toString(), adapter.getListCheck(),tempPath);
			                    uiHandler.post( new Runnable(){
			                        @Override
			                        public void run() {
			                            if(isUpdateRequired){
			                                //TODO:
			                            }else{
			                                linProgressBar.setVisibility(View.GONE);
//			                                Intent intent = new Intent(DownloadActivity.this,DownloadActivity.class);
//			        						intent.putExtras(getIntent().getExtras());
//			                                startActivity( intent );
//			                                finish();
			                            	
			                            	if(lstexit!=null && lstexit.size()>0){
			            						LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);          
			            						final View textEntryView = inflater.inflate(R.layout.lstdownloadexit, null);
			            						final TextView tvtitle = (TextView)textEntryView.findViewById(R.id.tvtitledownload);
			            						tvtitle.setText(language.getString("overwritefile"));
			            						//								final CheckBox cball= (CheckBox) textEntryView.findViewById(R.id.cball);
			            						//								cball.setText(language.getString("checkall"));
			            						final ListView lvlstexit =(ListView) textEntryView.findViewById(R.id.lstdownexit);

			            						FTPFile[] list= new FTPFile[lstexit.size()];
			            						for(int i=0;i<lstexit.size();i++){
			            							list[i]=lstexit.get(i);
			            						}
			            						adapter1 = new CheckFileAdapter(list, DownloadActivity.this,FtpClientActivity.flagChangeViewDownload,language);
			            						lvlstexit.setAdapter(adapter1);

			            						builder2 = new AlertDialog.Builder(DownloadActivity.this);
			            						builder2.setTitle(language.getString("report"));
			            						builder2.setView(textEntryView);
			            						
			            						builder2.setPositiveButton(language.getString("nextgo"), new DialogInterface.OnClickListener() {

			            							@Override
			            							public void onClick(DialogInterface dialog, int which) {
			            								if(adapter1.getListCheck()!=null && adapter1.getListCheck().size()>0){
			            									for(int i=0;i<adapter1.getListCheck().size();i++){										
			            										lstok.add(adapter1.getListCheck().get(i));
			            									}
			            									
			            									connects();
			            									for(int k=0;k<lstok.size();k++){
			            										fileSizeTotal+=totalSize(lstok.get(k), tempPath);
			            									}
			            									closeConnection();
			            									
			            									String storageDirectory = Environment.getExternalStorageDirectory().toString();
			            								    StatFs stat = new StatFs(storageDirectory);
			            								    final long freesize= stat.getAvailableBlocks() * stat.getBlockSize();
			            								    
			            								    System.out.println("free: "+freesize+" - down: "+fileSizeTotal);
			            								    if(freesize>=fileSizeTotal){
			            								    	progressBar = new ProgressDialog(DownloadActivity.this);
			            										progressBar.setCancelable(true);
			            										progressBar.setMessage(language.getString("fileloading"));
			            										progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			            										progressBar.setProgress(0);
			            										progressBar.setMax((int) fileSizeTotal);
			            										progressBar.show();
			            										progressBarStatus=0;
			            										
			            										new Thread(new Runnable() {
			            											public void run() {
			            												downloadOK(localFolders.getMyPath().getText().toString(), lstok,tempPath);
			            											}
			            										}).start();
			            								    }else{
			            								    	System.out.println("qua dung luong");
			            								    	AlertDialog.Builder buildercheck = new AlertDialog.Builder(DownloadActivity.this);
			            								    	buildercheck.setTitle(language.getString("warning"));
			            								    	buildercheck.setMessage(language.getString("fullsize"));
			            								    	buildercheck.setCancelable(false);
			            								    	buildercheck.setPositiveButton(language.getString("ok"),new DialogInterface.OnClickListener() {

			            											@Override
			            											public void onClick(DialogInterface dialog, int which) {
			            												
			            											}
			            										});
			            										
			            								    	buildercheck.show();
			            								    }
			            								    
			            								}else{
			            									
			            									if(lstok!=null && lstok.size()>0){
			            										connects();
			            										for(int k=0;k<lstok.size();k++){
			            											fileSizeTotal+=totalSize(lstok.get(k), tempPath);
			            										}
			            										closeConnection();
			            										
			            										String storageDirectory = Environment.getExternalStorageDirectory().toString();
			            									    StatFs stat = new StatFs(storageDirectory);
			            									    final long freesize= stat.getAvailableBlocks() * stat.getBlockSize();
			            									    System.out.println("free: "+freesize+" - down: "+fileSizeTotal);
			            									    if(freesize>=fileSizeTotal){
			            									    	progressBar = new ProgressDialog(DownloadActivity.this);
			            											progressBar.setCancelable(true);
			            											progressBar.setMessage(language.getString("fileloading"));
			            											progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			            											progressBar.setProgress(0);
			            											progressBar.setMax((int) fileSizeTotal);
			            											progressBar.show();
			            											progressBarStatus=0;
			            											
			            											new Thread(new Runnable() {
			            												public void run() {
			            													downloadOK(localFolders.getMyPath().getText().toString(), lstok,tempPath);
			            												}
			            											}).start();
			            									    }else{
			            									    	System.out.println("qua dung luong");
			            									    	AlertDialog.Builder buildercheck = new AlertDialog.Builder(DownloadActivity.this);
			            									    	buildercheck.setTitle(language.getString("warning"));
			            									    	buildercheck.setMessage(language.getString("fullsize"));
			            									    	buildercheck.setCancelable(false);
			            									    	buildercheck.setPositiveButton(language.getString("ok"),new DialogInterface.OnClickListener() {

			            												@Override
			            												public void onClick(DialogInterface dialog, int which) {
			            													
			            												}
			            											});
			            											
			            									    	buildercheck.show();
			            									    }			            										
			            									}
			            									
			            								}

			            							}
			            						});
			            						builder2.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

			            							@Override
			            							public void onClick(DialogInterface dialog, int which) {

			            							}
			            						});
			            						AlertDialog alert2 = builder2.create();
			            						alert2.show();
			            					}else{
			            						if(lstok!=null && lstok.size()>0){
			            							connects();
			            							for(int k=0;k<lstok.size();k++){
			            								fileSizeTotal+=totalSize(lstok.get(k), tempPath);
			            							}
			            							closeConnection();
			            							
			            							String storageDirectory = Environment.getExternalStorageDirectory().toString();
			            						    StatFs stat = new StatFs(storageDirectory);
			            						    final long freesize= stat.getAvailableBlocks() * stat.getBlockSize();
			            						    System.out.println("free: "+freesize+" - down: "+fileSizeTotal);
			            						    if(freesize>=fileSizeTotal){
			            						    	progressBar = new ProgressDialog(DownloadActivity.this);
			            								progressBar.setCancelable(true);
			            								progressBar.setMessage(language.getString("fileloading"));
			            								progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			            								progressBar.setProgress(0);
			            								progressBar.setMax((int) fileSizeTotal);
			            								progressBar.show();
			            								progressBarStatus=0;
			            								
			            								new Thread(new Runnable() {
			            									public void run() {
			            										downloadOK(localFolders.getMyPath().getText().toString(), lstok,tempPath);
			            									}
			            								}).start();
			            						    }else{
			            						    	System.out.println("qua dung luong");
			            						    	AlertDialog.Builder buildercheck = new AlertDialog.Builder(DownloadActivity.this);
			            						    	buildercheck.setTitle(language.getString("warning"));
			            						    	buildercheck.setMessage(language.getString("fullsize"));
			            						    	buildercheck.setCancelable(false);
			            						    	buildercheck.setPositiveButton(language.getString("ok"),new DialogInterface.OnClickListener() {

			            									@Override
			            									public void onClick(DialogInterface dialog, int which) {
			            										
			            									}
			            								});
			            								
			            						    	buildercheck.show();
			            						    }
			            						}
			            					}
			                            	
//			                            	finish();
			                            }
			                        }
			                    } );
			                }
			        }.start();
			        }catch (Exception e) {}
			        
			        
					
//					download(localFolders.getMyPath().getText().toString(), adapter.getListCheck(),tempPath);
					
			        
					
				}
				co=0;
			}

		});
		builder.setNegativeButton(language.getString("cancel"),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				co=0;
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		
	}

	private void openDialogError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(language.getString("warning"));
		builder.setMessage(language.getString("notchoosedownload"));
		builder.setCancelable(false);
		
		builder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				co=0;
				dialog.dismiss();
			}
		});
		builder.show();
	}


	///////////////////////////////////////////////////////////////
	//edit download

	public static boolean connects() {
		boolean rs = false;
		try {
//			client.connect(HOST, PORT);
//			client.setAutodetectUTF8(true);
//			rs = client.login(USER, PASS);
//			client.setFileType(FTP.BINARY_FILE_TYPE);
//			client.enterLocalPassiveMode();
			client.connect(ftp.getHostname(), Integer.parseInt(ftp.getPort()));
			client.setAutodetectUTF8(true);
			rs = client.login(ftp.getUsername(), ftp.getPassword());
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static void closeConnection() {
		try {
			client.logout();
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public void download(String pathSave,List<FTPFile> names,String temp) {
//
//		//boolean kq=true;
//		connects();
//		
//		for (FTPFile name : names) {
//			tests(name, pathSave,temp);
//			progressBarStatus ++;
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//
//			
//			progressBarHandler.post(new Runnable() {
//				public void run() {
//					progressBar.setProgress(progressBarStatus);
//				}
//			});
//		}
//		
//		closeConnection();
//		//return kq;
//	}

	public void download(String pathSave,List<FTPFile> names,String temp) {

		//boolean kq=true;
		connects();
		
		for (FTPFile name : names) {
			try {
				if(testsexit(name, pathSave,temp)==true){
					lstexit.add(name);
				}else{
					lstok.add(name);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		closeConnection();
		//return kq;
	}
	
	static public  boolean testsexit(FTPFile fileRemotePath, String pathSave,String ftpPath) throws IOException {
		boolean kq=false;
//		String tempRemode=ftpPath+"/"+fileRemotePath.getName();
		String tempAndroid=pathSave +"/"+ fileRemotePath.getName();
		if (!fileRemotePath.isFile()) {
			File file = new File(tempAndroid);
			if(file.isDirectory()){
//					lstexit.add(fileRemotePath);
				kq=true;
			}
		} else {
			File f=new File(tempAndroid);
			if(f.isFile()){
//					lstexit.add(fileRemotePath);
				kq=true;
			}
		}
		return kq;
	}

	public static void downloadOK(String pathSave, List<FTPFile> names,String temp) {
		connects();
		int ck=0;
		for (FTPFile name : names) {
			System.out.println("name: "+name.getName());
			testsOK(name, pathSave,temp);
			ck++;
			if(ck==names.size()){
				progressBarStatus = (int) fileSizeTotal;
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				progressBarHandler.post(new Runnable() {
					public void run() {
						progressBar.setProgress(progressBarStatus);
					}
				});
				
				if (progressBarStatus >= fileSizeTotal) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progressBar.dismiss();
				}
			}
		}
		closeConnection();
	}

	public static void testsOK(FTPFile fileRemotePath, String pathSave,String ftpPath) {

		try {

			String tempRemode=ftpPath+"/"+fileRemotePath.getName();
			String tempAndroid=pathSave +"/"+ fileRemotePath.getName();
			if (!fileRemotePath.isFile()) {
				File file = new File(tempAndroid);

				file.mkdir();
				FTPFile[] ftpFiles = client.listFiles(tempRemode);
				if (ftpFiles != null) {
					for (int i = 0; i < ftpFiles.length; i++) {
						testsOK(ftpFiles[i], tempAndroid, tempRemode);
					}
				}
			} else {
				client.retrieveFile(tempRemode, new FileOutputStream(tempAndroid));		
				
				sizelstok+=fileRemotePath.getSize();
				progressBarStatus = (int) sizelstok;
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				progressBarHandler.post(new Runnable() {
					public void run() {
						progressBar.setProgress(progressBarStatus);
					}
				});
				
				if (progressBarStatus >= fileSizeTotal) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progressBar.dismiss();
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

	}

	

	// process button back on android
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		co=0;
		if(!locaton.getText().equals("")){
			if(locaton.getText().toString().equals(tempPath)){
				String[] s=locaton.getText().toString().split("/");
				if(s!=null){
					for(int i=0;i<s.length;i++){
						System.out.println(i+" : "+s[i]);
					}
					if(s.length>1){
						int lastIndex=tempPath.lastIndexOf("/");
						tempPath=tempPath.substring(0,lastIndex);
						locaton.setText(tempPath);
						System.out.println("temp: "+tempPath);
						try {
							writefile();
						} catch (Exception e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(DownloadActivity.this, MainActivity2.class);		

						intent.putExtras(getIntent().getExtras());
						intent.putExtra("flagtab", 0);
//						intent.putExtra("flagChangeViewDownload",FtpClientActivity.flagChangeViewDownload);
						startActivity(intent);

					}else{
						Intent intent = new Intent(DownloadActivity.this,FtpClientActivity.class);
						intent.putExtras(getIntent().getExtras());
						startActivity(intent);
					}
				}else{
					Intent intent = new Intent(DownloadActivity.this,FtpClientActivity.class);
					intent.putExtras(getIntent().getExtras());
					startActivity(intent);
				}
			}

		}else{
			Intent intent = new Intent(DownloadActivity.this,FtpClientActivity.class);
			intent.putExtras(getIntent().getExtras());
			startActivity(intent);
		}
		finish();
	}


	private void writefile() throws Exception{
		FileOutputStream file = openFileOutput("download.txt", Context.MODE_PRIVATE);
		String s="/";
		if(!locaton.getText().toString().equals("")){
			s=locaton.getText().toString();
			String[] str=s.split("/");
			if(str!=null){
				if(str.length>1){
					for(int i=1;i<str.length;i++){
						String[] sub=str[i].split("");
						if(sub!=null && sub.length>1){
							for(int j=0;j<sub.length;j++){
								System.out.println(j+"-- "+sub[j]);
								if(j==0){
									if(sub[j].equals(" ")){
										sub[j]="%";
									}
									str[i]=sub[j];
								}else{
									if(sub[j].equals(" ")){
										sub[j]="%";
									}
									str[i]+=sub[j];
								}
							}
						}
						if(i==1){
							s="/"+str[i];
						}else{
							s+="/"+str[i];
						}
					}
				}
				
			}
		}
		System.out.println("s= "+s);
		file.write(s.getBytes());		
		file.close();
	}

	private void readFile(){		
		try {
			File fr = getBaseContext().getFileStreamPath("download.txt");
			if(fr.exists()){
				Scanner scanner = new Scanner(fr);
				tempPath=scanner.next().trim();
				if(!tempPath.equals("/")){
					String[] str=tempPath.split("/");
					if(str!=null && str.length>1 ){
						for(int i=1;i<str.length;i++){
							String[] sub=str[i].split("");
							if(sub!=null && sub.length>1){
								for(int j=0;j<sub.length;j++){
									if(j==0){
										if(sub[j].equals("%")){
											sub[j]=" ";
										}
										str[i]=sub[j];
									}else{
										if(sub[j].equals("%")){
											sub[j]=" ";
										}
										str[i]+=sub[j];
									}
								}
							}
							if(i==1){
								tempPath="/"+str[i];
							}else{
								tempPath+="/"+str[i];
							}
						}
					}
				}
			}else{
				tempPath="/";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		writefileUpload();
	}

	private void writefileUpload(){
		FileOutputStream file;
		try {
			String s=Environment.getExternalStorageDirectory().toString();
			file = openFileOutput("upload.txt", Context.MODE_PRIVATE);
			file.write(s.getBytes());		
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		if(lang.equals("EN")){
			inflater.inflate(R.menu.option_menu, menu);
		}else if(lang.equals("VN")){
			inflater.inflate(R.menu.option_menuvn, menu);
		}else{
			inflater.inflate(R.menu.option_menukr, menu);
		}
		
		setMenuBackground();
		return true;
		
	}

	protected void setMenuBackground(){
		Log.d("", "Enterting setMenuBackGround");  
		getLayoutInflater().setFactory( new Factory() {  

			@Override
			public View onCreateView(String name, Context context,
					AttributeSet attrs) {
				if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {  

					try { 
						LayoutInflater f = getLayoutInflater();  
						final View view = f.createView( name, null, attrs );  
						new Handler().post( new Runnable() {  
							public void run () {  
								view.setBackgroundResource( R.drawable.bg);  
							}  
						} );  
						return view;  
					}  
					catch ( InflateException e ) {}  
					catch ( ClassNotFoundException e ) {}  
				}  
				return null;  

			}  
		});  
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(lang.equals("VN")){
			switch (item.getItemId()) {
			case R.id.downloadvn:
				processDownload();
				return true;
			case R.id.new_foldervn:
				processNewfolder();
				return true;
			case R.id.changeviewvn:
				changeView();
				return true;
			case R.id.deletevn:
				multidelete();
				return true;
			case R.id.sortvn:
				sort();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}else if(lang.equals("KR")){
			switch (item.getItemId()) {
			case R.id.downloadkr:
				processDownload();
				return true;
			case R.id.new_folderkr:
				processNewfolder();
				return true;
			case R.id.changeviewkr:
				changeView();
				return true;
			case R.id.deletekr:
				multidelete();
				return true;
			case R.id.sortkr:
				sort();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}else{
			switch (item.getItemId()) {
			case R.id.download:
				processDownload();
				return true;
			case R.id.new_folder:
				processNewfolder();
				return true;
			case R.id.changeview:
				changeView();
				return true;
			case R.id.delete:
				multidelete();
				return true;
			case R.id.sort:
				sort();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}
		
	}

	private void processNewfolder(){
		if(co==0){
			co=1;
			newfolderDialog();
		}
	}

	private void processDownload(){
		if(co==0){
			co=1;
			if(adapter.getListCheck()!=null && adapter.getListCheck().size()>0){
					downloadDialog();
			}else{
					openDialogError();
			}
		}
	}
	

	private void changeView(){
		
		FtpClientActivity.flagChangeViewDownload =!FtpClientActivity.flagChangeViewDownload;
    	loadListFiles(tempPath);
	}
	
	
	private void multidelete(){
		if(adapter.getListCheck()!=null && adapter.getListCheck().size()>0){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(DownloadActivity.this);
			alertDialog.setTitle(language.getString("delete"));
			alertDialog.setMessage(language.getString("deleteall")+" ?");
			alertDialog.setPositiveButton(language.getString("ok"),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {

					linProgressBar.setVisibility(View.VISIBLE);
					
					 try{
				            new Thread(){
				                public void run() {
				                	for(int i=0;i<adapter.getListCheck().size();i++){
										FTPUtil.deletFolder(adapter.getListCheck().get(i),tempPath);
									}
				                    uiHandler.post( new Runnable(){
				                        @Override
				                        public void run() {
				                            if(isUpdateRequired){
				                                //TODO:
				                            }else{
				                                linProgressBar.setVisibility(View.GONE);
				                                loadListFiles(tempPath);
				                            }
				                        }
				                    } );
				                }
				        }.start();
				        }catch (Exception e) {}
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
			
		}else{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(DownloadActivity.this);
			alertDialog.setTitle(language.getString("delete"));
			alertDialog.setMessage(language.getString("notcheckdelete"));
			alertDialog.setPositiveButton(language.getString("ok"),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {

					
				}
			});
			
			AlertDialog alert3 = alertDialog.create();
			alert3.show();
		}
	}
	
	

//	public static long totalSize(FTPFile ftpFile,String path){
//		long result = 0;
//		connects();
//			result=totalSizeProcess(ftpFile,path);
//		closeConnection();
//		return result;
//	}

	public static long totalSize(FTPFile ftpFile,String path){
		long result = 0;
		try {
			
			String pathcha= path+"/" + ftpFile.getName();
			FTPFile[] ftpFiles = client.listFiles(pathcha);
			if(ftpFiles!=null && ftpFiles.length>0){
				for (int i = 0; i < ftpFiles.length; i++) {
					if(ftpFiles[i].isDirectory()){
						result += totalSize(ftpFiles[i], pathcha );
					}else{
						result += ftpFiles[i].getSize();
					}
				}
			}else{
				result +=ftpFile.getSize();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			 
		}
		return result;
	}
	
	private void sort(){
		flagsort=!flagsort;
		loadListFiles(tempPath);
	}
	

}


