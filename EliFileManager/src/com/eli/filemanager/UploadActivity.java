package com.eli.filemanager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import fpt.util.FTPUtil;
import fpt.util.FileUtil;
import ftp.pojo.CustomFile;
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
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class UploadActivity extends Activity  {

	public static String HOST = "www.elisoft.co.kr";
	public static int PORT = 21;
	public static String USER = "elisoft";
	public static String PASS = "7890";

	static String USERNAME = "";
	static String UPLOAD_FOLDER = "upload";
	static String DOWNLOAD_FOLDER = "dowload";
	static String MY_FILES = "myfiles";
	static FTPClient client = new FTPClient();
	GridView gridView;

	String lang = "EN";
	Bundle language;
	// FtpUser emp = new FtpUser();
	public int co=0;
	static FtpConnect ftp = new FtpConnect();
	//	Button  btNewFolder;
	Button bntUpload,bntGo;
	EditText locaton;
	TextView tb,tvwaitingup;
	String tempPath = Environment.getExternalStorageDirectory().toString();

	public String getLang() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			lang = extras.getString("lang");
		}
		return lang;
	}

	ListView listFile;
	UploadFileAdapter adapter;
	static List<File> lstupexit= new ArrayList<File>();
	static List<File> lstupok= new ArrayList<File>();
	static long sizelstupexit=0;
	static long sizelstupok=0;

	//process bar
	static ProgressDialog progressBar;
	private static int progressBarStatus = 0;
	private static Handler progressBarHandler = new Handler();
	private static long fileSizeTotalup = 0;


	// mutil copy
	static File keepValueCopy;
//	static public List<CustomFile> lstcopy=new ArrayList<CustomFile>();
	static public List<File> lstcopy=new ArrayList<File>();
	static String srcpath;

	//size width, height
	int screenheight,screenwidth;
	
	boolean flagsortupload=true;
	
	//wait
	private LinearLayout linProgressBarup;
    private final Handler uiHandlerup=new Handler();
    private boolean isUpdateRequiredup=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload);

		readFile();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenheight = displaymetrics.heightPixels;
        screenwidth = displaymetrics.widthPixels;

		language = getIntent().getExtras().getBundle(getLang());
		locaton = (EditText) findViewById(R.id.locaton);
		//		bntGo = (Button) findViewById(R.id.bntGo);
		//		bntGo.setText(language.getString("go"));

		bntGo = (Button)findViewById(R.id.buttonGoUpload);
		tb=(TextView)findViewById(R.id.tv_tb);
		//		btNewFolder =(Button)findViewById(R.id.bntNewFolder);
		//		btNewFolder.setText(language.getString("newfolder"));


//		FtpClientActivity.flagChangeViewUpload = getIntent().getExtras().getBoolean("flagChangeViewUpload");

		// Intent currentIntent = this.getIntent();
		ftp = (FtpConnect) getIntent().getExtras().get("ftp");

		locaton.setWidth(screenwidth-110);
		locaton.setText(tempPath);
		
		linProgressBarup = (LinearLayout) findViewById(R.id.lin_progress_barup);
		tvwaitingup=(TextView)findViewById(R.id.tv_waitingup);
		linProgressBarup.layout(screenwidth/4,screenheight-( screenheight/3 + 80), 0, screenheight/3);
		tvwaitingup.setText(language.getString("waiting")+"...");
		linProgressBarup.setVisibility(View.INVISIBLE);
		
		listFile = (ListView) findViewById(R.id.lstFile);
		bntUpload = (Button)findViewById(R.id.buttonUpload);
		//		bntUpload = (Button) findViewById(R.id.bntUpload);
		//		bntUpload.setText(language.getString("upload"));
		gridView =(GridView)findViewById(R.id.gridUpload);
		int kq=screenwidth/135;
		gridView.setNumColumns(kq);

		System.out.println("tempath loi: "+tempPath);
		loadListFiles(tempPath);
		bntUpload.setOnClickListener(uploadEvent);
		//
		//		btNewFolder.setOnClickListener(newfolderupload);


		bntGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				//				 if(locaton.getText().toString().equals("") || locaton.getText().toString().equals("/mnt/sdcard/")){
				//					 locaton.setText(Environment.getExternalStorageDirectory().toString());
				//					 tempPath=locaton.getText().toString();
				//				 }
				String str=locaton.getText().toString();
				File file=new File(locaton.getText().toString());
				if(file.exists() && !file.isFile()){
					System.out.println("str: "+locaton.getText());
					String[] ss=str.split("/");
					String nstr="";
					if(ss!=null && ss.length>0){
						for(int z=0;z<ss.length;z++){
							if(z==0){
								nstr=ss[z];
							}else{
								nstr+="/"+ss[z];
							}
						}
					}
					locaton.setText(nstr);
					tempPath=locaton.getText().toString();

					if(!locaton.getText().toString().equals(Environment.getExternalStorageDirectory().toString())){
						String[] s=locaton.getText().toString().split("/");
						if(s!=null ){							
							if(s.length<3){
								locaton.setText(Environment.getExternalStorageDirectory().toString());
								tempPath=locaton.getText().toString();
								file=new File(locaton.getText().toString());
								if(file.exists() && !file.isFile()){
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									loadListFiles(locaton.getText().toString());
								}

							}else if(s.length==3){
								if(s[0].equals("") && s[1].equals("mnt") && s[2].equals("sdcard")){
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									loadListFiles(locaton.getText().toString());
									tempPath=locaton.getText().toString();
								}else{
									locaton.setText(Environment.getExternalStorageDirectory().toString());
									tempPath=locaton.getText().toString();
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									loadListFiles(locaton.getText().toString());							 
								}
							}else{
								if(s[0].equals("") && s[1].equals("mnt") && s[2].equals("sdcard")){
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									System.out.println("loi loi: "+locaton.getText().toString());
									loadListFiles(locaton.getText().toString());
									tempPath=locaton.getText().toString();
								}else{
									locaton.setText(Environment.getExternalStorageDirectory().toString());
									tempPath=locaton.getText().toString();
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									loadListFiles(locaton.getText().toString());	
								}
							}
						}else{
							locaton.setText(Environment.getExternalStorageDirectory().toString());
							tempPath=locaton.getText().toString();
							file=new File(locaton.getText().toString());
							if(file.exists() && !file.isFile()){
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
								loadListFiles(locaton.getText().toString());
							}
						}

					}else{
						InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
						imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
						loadListFiles(locaton.getText().toString());
						tempPath=locaton.getText().toString();
					}

				}else{

					String[] s=str.split("/");
					if(s!=null ){
						if(s.length<3){
							locaton.setText(Environment.getExternalStorageDirectory().toString());
							tempPath=locaton.getText().toString();
							file=new File(locaton.getText().toString());
							if(file.exists() && !file.isFile()){
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
								loadListFiles(locaton.getText().toString());
							}

						}else if(s.length==3){
							if(s[0].equals("") && s[1].equals("mnt") && s[2].equals("sdcard")){
								InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
								loadListFiles(locaton.getText().toString());
								tempPath=locaton.getText().toString();
							}else{							 
								locaton.setText(Environment.getExternalStorageDirectory().toString());
								tempPath=locaton.getText().toString();
								file=new File(locaton.getText().toString());
								if(file.exists() && !file.isFile()){
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									loadListFiles(locaton.getText().toString());
								}
							}
						}else{
							if(s[0].equals("") && s[1].equals("mnt") && s[2].equals("sdcard")){
								int checkgo=0;
								int size=s.length;
								while(size>3 && checkgo==0){
									int lastIndex=str.lastIndexOf("/");
									str=str.substring(0,lastIndex);
									file=new File(str);
									if(file.exists() && !file.isFile()){
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
									locaton.setText(Environment.getExternalStorageDirectory().toString());
									tempPath=locaton.getText().toString();
									file=new File(locaton.getText().toString());
									if(file.exists() && !file.isFile()){
										InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
										imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
										loadListFiles(locaton.getText().toString());
									}
								}
							}else{
								locaton.setText(Environment.getExternalStorageDirectory().toString());
								tempPath=locaton.getText().toString();
								file=new File(locaton.getText().toString());
								if(file.exists() && !file.isFile()){
									InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
									imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
									loadListFiles(locaton.getText().toString());
								}
							}
						}
					}else{
						locaton.setText(Environment.getExternalStorageDirectory().toString());
						tempPath=locaton.getText().toString();
						file=new File(locaton.getText().toString());
						if(file.exists() && !file.isFile()){
							InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
							imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
							loadListFiles(locaton.getText().toString());
						}
					}
				}



			}
		});



	}


	private OnClickListener newfolderupload = new  OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(co==0){
				co=1;
				newfolderDialog();
			}
		}
	};

	private OnClickListener uploadEvent = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			if(co==0){
				co=1;
		        if(adapter.getListCheck()!=null && adapter.getListCheck().size()>0){
		        	uploadDialog();
				}else{
					openDialogError();
				}
			}
		}
	};

	public void loadLocation(String path) {
		if("..|".equals(path)){
			int lastIndex=tempPath.lastIndexOf("/");
			String temp = tempPath.substring(0,lastIndex);
			loadListFiles(temp);
			locaton.setText(temp);
			tempPath = temp;
		}else{
			if ("/".equals(tempPath)) {
				String temp = tempPath + path;

				locaton.setText(temp);
				tempPath = temp;
				loadListFiles(temp);
				
			} else {
				String temp = tempPath + "/" + path;
				System.out.println("tempath loi click:"+temp);
				locaton.setText(temp);
				tempPath = temp;
				loadListFiles(temp);
				
			}
		}

	}

	// get list upload
	public void loadListFiles(String path) {
		File[] files = FileUtil.getFilesOfDirectory(path);
//		if(!(Environment.getExternalStorageDirectory().toString().equals(path))){
//			System.out.println("bfffff");
//			if(files!=null && files.length>0){
//				System.out.println("nonono");
//				File[] tempFiles=new File[files.length];
//				for (int i = 0; i < files.length; i++) {
//					tempFiles[i]=files[i];
//				}
//				files=tempFiles;
//			}
//		}

		if(files!=null){
			System.out.println("so luong: "+files.length);
			if(flagsortupload==true){
//				List<File> subfile =new ArrayList<File>();
//				List<File> subfolder =new ArrayList<File>();
//				
//				for (int i = 0; i < files.length; i++) {
//					if(files[i].isDirectory()){
//						subfolder.add(files[i]);
//					}else{
//						subfile.add(files[i]);
//					}
//				}
//				
//				if(subfolder!=null && subfile!=null && subfolder.size()>0 && subfile.size()>0){
//					for (int i = 0; i < subfile.size()-1; i++) {
//						File temp=subfile.get(i);
//						for(int k=i+1;k<subfile.size();k++){
//							if(temp.getName().toString().compareToIgnoreCase(subfile.get(k).getName().toString())>=0){
//								File a=temp;
//								temp=subfile.get(k);
//								subfile.set(k, a);
//							}
//						}
//						subfile.set(i, temp);
//					}
//					
//					for (int i = 0; i < subfolder.size()-1; i++) {
//						File temp1=subfolder.get(i);
//						for(int k=i+1;k<subfolder.size();k++){
//							if(temp1.getName().toString().compareToIgnoreCase(subfolder.get(k).getName().toString())>=0){
//								File a=temp1;
//								temp1=subfolder.get(k);
//								subfolder.set(k, a);
//							}
//						}
//						subfolder.set(i, temp1);
//					}
//					
//					
//					for(int i=0;i<subfile.size();i++){
//						files[i]=subfile.get(i);
//					}
//					for(int i=0;i<subfolder.size();i++){
//						files[subfile.size()+i]=subfolder.get(i);
//					}
//				}else{
//					for (int i = 0; i < files.length-1; i++) {
//						File temp=files[i];
//						for(int k=i+1;k<files.length;k++){
//							if(temp.getName().toString().compareToIgnoreCase(files[k].getName().toString())>=0){
//								File a=temp;
//								temp=files[k];
//								files[k]=a;	
//							}
//						}
//						files[i]=temp;
//					}
//				}
				
				int i=0;
				int sl=files.length;
				while(i<sl){
					if(files[i].isFile()){
						
						for(int j=i+1;j<sl;j++){
							if(files[j].isFile()){
								if(files[i].getName().compareToIgnoreCase(files[j].getName())>0){
									File tem=files[i];
									files[i]=files[j];
									files[j]=tem;
								}
							}
						}
						i++;
					}else if(files[i].isDirectory()){
						int vt=i;
						while(vt<sl && files[vt].isDirectory()){
							vt++;
						}
						if(vt==sl-1){
							File tem=files[i];
							files[i]=files[vt];
							files[vt]=tem;
//							i++;
						}else if(vt==sl){
							for(int k=i;k<sl-1;k++){
								for(int h=k+1;h<sl;h++){
									if(files[h].getName().compareToIgnoreCase(files[k].getName())<0){
										File tt=files[k];
										files[k]=files[h];
										files[h]=tt;
									}
								}
							}
							i=files.length;
						}else if(vt<sl-1){
							File min=files[vt];
							for(int z=vt;z<sl;z++){
								if(files[z].isFile()){
									if(files[z].getName().compareToIgnoreCase(min.getName())<0){
										min=files[z];
										vt=z;
									}
								}
							}
							files[vt]=files[i];
							files[i]=min;
//							i++;
						}
						i++;
					}
				}
				
			}else{
//				List<File> subfile =new ArrayList<File>();
//				List<File> subfolder =new ArrayList<File>();
//				
//				for (int i = 0; i < files.length; i++) {
//					if(files[i].isDirectory()){
//						subfolder.add(files[i]);
//					}else{
//						subfile.add(files[i]);
//					}
//				}
//				
//				if(subfolder!=null && subfile!=null && subfolder.size()>0 && subfile.size()>0){
//					for (int i = 0; i < subfile.size()-1; i++) {
//						File temp=subfile.get(i);
//						for(int k=i+1;k<subfile.size();k++){
//							if(temp.getName().toString().compareToIgnoreCase(subfile.get(k).getName().toString())<=0){
//								File a=temp;
//								temp=subfile.get(k);
//								subfile.set(k, a);
//							}
//						}
//						subfile.set(i, temp);
//					}
//					
//					for (int i = 0; i < subfolder.size()-1; i++) {
//						File temp1=subfolder.get(i);
//						for(int k=i+1;k<subfolder.size();k++){
//							if(temp1.getName().toString().compareToIgnoreCase(subfolder.get(k).getName().toString())<=0){
//								File a=temp1;
//								temp1=subfolder.get(k);
//								subfolder.set(k, a);
//							}
//						}
//						subfolder.set(i, temp1);
//					}
//					
//					
//					for(int i=0;i<subfile.size();i++){
//						files[i]=subfile.get(i);
//					}
//					for(int i=0;i<subfolder.size();i++){
//						files[subfile.size()+i]=subfolder.get(i);
//					}
//				}else{
//					for (int i = 0; i < files.length-1; i++) {
//						File temp=files[i];
//						for(int k=i+1;k<files.length;k++){
//							if(temp.getName().toString().compareToIgnoreCase(files[k].getName().toString())<=0){
//								File a=temp;
//								temp=files[k];
//								files[k]=a;	
//							}
//						}
//						files[i]=temp;
//					}
//				}
				
				int i=0;
				int sl=files.length;
				while(i<sl){
					if(files[i].isFile()){
						
						for(int j=i+1;j<sl;j++){
							if(files[j].isFile()){
								if(files[i].getName().compareToIgnoreCase(files[j].getName())<0){
									File tem=files[i];
									files[i]=files[j];
									files[j]=tem;
								}
							}
						}
						i++;
					}else if(files[i].isDirectory()){
						int vt=i;
						while(vt<sl && files[vt].isDirectory()){
							vt++;
						}
						if(vt==sl-1){
							File tem=files[i];
							files[i]=files[vt];
							files[vt]=tem;
//							i++;
						}else if(vt==sl){
							for(int k=i;k<sl-1;k++){
								for(int h=k+1;h<sl;h++){
									if(files[h].getName().compareToIgnoreCase(files[k].getName())>0){
										File tt=files[k];
										files[k]=files[h];
										files[h]=tt;
									}
								}
							}
							i=files.length;
						}else if(vt<sl-1){
							File min=files[vt];
							for(int z=vt;z<sl;z++){
								if(files[z].isFile()){
									if(files[z].getName().compareToIgnoreCase(min.getName())>0){
										min=files[z];
										vt=z;
									}
								}
							}
							files[vt]=files[i];
							files[i]=min;
//							i++;
						}
						i++;
					}
				}
				
			}
			
			if(files.length>0){
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

		if(FtpClientActivity.flagChangeViewUpload == false){
			adapter = new UploadFileAdapter(files,this,FtpClientActivity.flagChangeViewUpload,language);
			gridView.setVisibility(View.INVISIBLE);
			listFile.setVisibility(View.VISIBLE);
			listFile.setAdapter(adapter);
		}
		else if(FtpClientActivity.flagChangeViewUpload == true){
			adapter = new UploadFileAdapter(files, this, FtpClientActivity.flagChangeViewUpload,language);
			listFile.setVisibility(View.INVISIBLE);
			gridView.setVisibility(View.VISIBLE);
			gridView.setAdapter(adapter);
		}
	}

	private void uploadDialog() {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		.getSystemService(LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.ftplayout, null);
		final FTPLayout ftpLayout = (FTPLayout) view
		.findViewById(R.id.ftpLayout);
		// localFolders.setDir(Environment.getExternalStorageDirectory()
		// .toString());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		builder.setCancelable(false);
		builder.setPositiveButton(language.getString("save"),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {


				if (adapter.getListCheck().size() > 0) {
					lstupexit.clear();
					lstupok.clear();
					sizelstupok=0;
					fileSizeTotalup=0;

					linProgressBarup.setVisibility(View.VISIBLE);
					
					try{
			            new Thread(){
			                public void run() {
			                	uploadFTP(adapter.getListCheck(),ftpLayout.getPath());
			                    uiHandlerup.post( new Runnable(){
			                        @Override
			                        public void run() {
			                            if(isUpdateRequiredup){
			                                //TODO:
			                            }else{
			                                linProgressBarup.setVisibility(View.GONE);
			                                if(lstupexit!=null && lstupexit.size()>0){
			            						LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);          
			            						final View textEntryView = inflater.inflate(R.layout.lstuploadexit, null);

			            						final TextView tvtitleup = (TextView)textEntryView.findViewById(R.id.tvtitleupload);
			            						tvtitleup.setText(language.getString("overwritefile"));
			            						//								final CheckBox cballup= (CheckBox) textEntryView.findViewById(R.id.cballupload);
			            						//								cballup.setText(language.getString("checkall"));
			            						final ListView lvlstexitup =(ListView) textEntryView.findViewById(R.id.lstupexit);

			            						File[] list= new File[lstupexit.size()];
			            						for(int i=0;i<lstupexit.size();i++){
			            							list[i]=lstupexit.get(i);

			            						}
			            						final UploadFileAdapter adapter1 = new UploadFileAdapter(list,UploadActivity.this,FtpClientActivity.flagChangeViewUpload,language);
			            						lvlstexitup.setAdapter(adapter1);

			            						AlertDialog.Builder builder2 = new AlertDialog.Builder(UploadActivity.this);
			            						builder2.setTitle(language.getString("report"));
			            						builder2.setView(textEntryView);
			            						builder2.setPositiveButton(language.getString("nextgo"), new DialogInterface.OnClickListener() {

			            							@Override
			            							public void onClick(DialogInterface dialog, int which) {
			            								if(adapter1.getListCheck()!=null && adapter1.getListCheck().size()>0){
			            									for(int i=0;i<adapter1.getListCheck().size();i++){
			            										lstupok.add(adapter1.getListCheck().get(i));
			            									}
			            									for(int k=0;k<lstupok.size();k++){
			            										fileSizeTotalup+=getTotalSize(lstupok.get(k));
			            									}

			            									progressBar = new ProgressDialog(UploadActivity.this);
			            									progressBar.setCancelable(true);
			            									progressBar.setMessage(language.getString("fileloading"));
			            									progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			            									progressBar.setProgress(0);
			            									progressBar.setMax((int) fileSizeTotalup);
			            									progressBar.show();
			            									progressBarStatus=0;

			            									new Thread(new Runnable() {
			            										public void run() {
			            											uploadFTPOK(lstupok,ftpLayout.getPath());
			            										}
			            									}).start();
			            								}else{
			            									if(lstupok!=null && lstupok.size()>0){
			            										for(int k=0;k<lstupok.size();k++){
			            											fileSizeTotalup+=getTotalSize(lstupok.get(k));
			            										}
			            										progressBar = new ProgressDialog(UploadActivity.this);
			            										progressBar.setCancelable(true);
			            										progressBar.setMessage(language.getString("fileloading"));
			            										progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			            										progressBar.setProgress(0);
			            										progressBar.setMax((int)fileSizeTotalup);
			            										progressBar.show();
			            										progressBarStatus=0;

			            										new Thread(new Runnable() {
			            											public void run() {
			            												uploadFTPOK(lstupok,ftpLayout.getPath());
			            											}
			            										}).start();

			            									}
			            								}
			            							}
			            						});
			            						builder2.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

			            							@Override
			            							public void onClick(DialogInterface dialog, int which) {

			            							}
			            						});
			            						AlertDialog alert = builder2.create();
			            						alert.show();
			            					}else{

			            						if(lstupok!=null && lstupok.size()>0){
			            							for(int k=0;k<lstupok.size();k++){
			            								fileSizeTotalup+=getTotalSize(lstupok.get(k));
			            							}

			            							progressBar = new ProgressDialog(UploadActivity.this);
			            							progressBar.setCancelable(true);
			            							progressBar.setMessage(language.getString("fileloading"));
			            							progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			            							progressBar.setProgress(0);
			            							progressBar.setMax((int)fileSizeTotalup);
			            							progressBar.show();
			            							progressBarStatus=0;

			            							new Thread(new Runnable() {
			            								public void run() {
			            									uploadFTPOK(lstupok,ftpLayout.getPath());
			            								}
			            							}).start();

			            						}

			            					}
			                            }
			                        }
			                    } );
			                }
			                
			        }.start();
			        }catch (Exception e) {}
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

	private void newfolderDialog() {

		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
		.getSystemService(LAYOUT_INFLATER_SERVICE);

		View view = inflater.inflate(R.layout.newfolder, null);
		final EditText nameFolder = (EditText) view
		.findViewById(R.id.nameFolder);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(view);
		builder.setCancelable(false);
		builder.setPositiveButton(language.getString("save"),
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!nameFolder.getText().toString().equals("")){
					co=0;
					linProgressBarup.setVisibility(View.VISIBLE);
					 
			        try{
			            new Thread(){
			                public void run() {
			                	InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
								imm.toggleSoftInput(
										InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
								final String temp = locaton.getText().toString();
								boolean kq = false;

								if ("/".equals(temp))
									kq=FTPUtil.sdcardCreateFolder(temp+ nameFolder.getText().toString());
								else
									kq=FTPUtil.sdcardCreateFolder(temp + "/"+ nameFolder.getText().toString());
								final boolean ht=kq;
			                    uiHandlerup.post( new Runnable(){
			                        @Override
			                        public void run() {
			                            if(isUpdateRequiredup){
			                                //TODO:
			                            }else{
			                                linProgressBarup.setVisibility(View.GONE);
			                                if(ht==false){
			            						AlertDialog.Builder builder2 = new AlertDialog.Builder(UploadActivity.this);
			            						builder2.setTitle(language.getString("report"));
			            						builder2.setMessage(language.getString("folderexit"));
			            						builder2.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {

			            							@Override
			            							public void onClick(DialogInterface dialog, int which) {
			            								newfolderDialog();
			            							}
			            						});

			            						AlertDialog alert = builder2.create();
			            						alert.show();
			            					}else{
			            						loadListFiles(temp);
			            					}
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

	private void openDialogError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(language.getString("warning"));
		builder.setMessage(language.getString("notchooseupload"));
		builder.setCancelable(false);
		builder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				co=0;
				dialog.dismiss();
			}
		});
		builder.show();
	}

	// process button back on android
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		co=0;
		if(!locaton.getText().equals("") || locaton.equals(Environment.getExternalStorageDirectory().toString())){
			if(locaton.getText().toString().equals(tempPath)){
				String[] s=locaton.getText().toString().split("/");
				if(s!=null){
					System.out.println("soluong: "+s.length);
					for(int z=0;z<s.length;z++){
						System.out.println(z+" : "+s[z]);
					}
					if(s.length>3){
						int lastIndex=locaton.getText().toString().lastIndexOf("/");
						locaton.setText(locaton.getText().toString().substring(0, lastIndex));
						tempPath=locaton.getText().toString();

						try {
							writefile();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent = new Intent(UploadActivity.this, MainActivity2.class);		

						intent.putExtras(getIntent().getExtras());
						intent.putExtra("flagtab", 1);
//						intent.putExtra("flagChangeViewUpload", FtpClientActivity.flagChangeViewUpload);
						startActivity(intent);

					}else{
						locaton.setText(Environment.getExternalStorageDirectory().toString());
						tempPath=locaton.getText().toString();
						System.out.println("co vao day ko thi bao");
						Intent intent = new Intent(UploadActivity.this,FtpClientActivity.class);
						intent.putExtras(getIntent().getExtras());
						startActivity(intent);
					}
				}else{
					Intent intent = new Intent(UploadActivity.this,FtpClientActivity.class);
					intent.putExtras(getIntent().getExtras());
					startActivity(intent);
				}
			}
		}else{
			Intent intent = new Intent(UploadActivity.this,FtpClientActivity.class);
			intent.putExtras(getIntent().getExtras());
			startActivity(intent);
		}
		finish();
	}

	private void readFile(){		

		try {
			File fr = getBaseContext().getFileStreamPath("upload.txt");
			if(fr.exists()){
				Scanner scanner = new Scanner(fr);
				tempPath=scanner.next().trim();
				if(!tempPath.equals(Environment.getExternalStorageDirectory().toString())){
					String[] str=tempPath.split("/");
					if(str!=null && str.length>3 ){
						for(int i=3;i<str.length;i++){
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
							if(i==3){
								tempPath=Environment.getExternalStorageDirectory().toString()+"/"+str[i];
							}else{
								tempPath+="/"+str[i];
							}
						}
					}
				}
			}else{
				tempPath=Environment.getExternalStorageDirectory().toString();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writefile() throws Exception{
		FileOutputStream file = openFileOutput("upload.txt", Context.MODE_PRIVATE);
		String s="/mnt/sdcard";
		if(!locaton.getText().toString().equals("")){
			s=locaton.getText().toString();
			String[] str=s.split("/");
			if(str!=null){
				if(str.length>3){
					for(int i=3;i<str.length;i++){
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
						if(i==3){
							s="/mnt/sdcard/"+str[i];
						}else{
							s+="/"+str[i];
						}
					}
				}
				
			}
		}
		file.write(s.getBytes());		
		file.close();
	}

	private void deletefile(){
		FileOutputStream file;
		try {
			String s="/";
			file = openFileOutput("download.txt", Context.MODE_PRIVATE);
			file.write(s.getBytes());		
			file.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		deletefile();
	}

	//////////////////////////////////////////////////////////////////////////
	//edit upload
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

	//	public static boolean uploadFTP(List<File> filesSrc, String pathDes) {
	//		boolean kq=true;
	//		connects();
	//		for (int i = 0; i < filesSrc.size(); i++) {
	//			kq=uploadFile(filesSrc.get(i), pathDes, "/");
	//			if(kq==false){
	//				break;
	//			}
	//		}
	//		closeConnection();
	//		return kq;
	//	}

	public  void uploadFTP(List<File> filesSrc, String pathDes) {
		connects();
		for (int i = 0; i < filesSrc.size(); i++) {
			if(testExitUploadFile(filesSrc.get(i), pathDes, "/")==true){
				lstupexit.add(filesSrc.get(i));
				//				sizelstupexit +=getTotalSize(filesSrc.get(i));
			}else{
				lstupok.add(filesSrc.get(i));
				//				sizelstupok +=getTotalSize(filesSrc.get(i));
			}

		}
		closeConnection();
	}



	//	public static void uploadFile(File fileSrc, String pathDes, String temp) {
	//		try {
	//
	//			if (!fileSrc.isFile()) {
	//				temp += fileSrc.getName();
	//				//System.out.prln("directory: " + temp);
	//				//client.makeDirectory(pathDes + temp);
	//				String s=pathDes + temp;
	//				
	//				
	//				if(client.changeWorkingDirectory(pathDes + temp)){
	//					lstupexit.add(fileSrc);
	//				}else{
	//					client.makeDirectory(pathDes + temp);
	//					File[] files = fileSrc.listFiles();
	//					if (files != null) {
	//						for (int i = 0; i < files.length; i++) {
	//							uploadFile(files[i], pathDes, temp + "/");
	//						}
	//					}
	//					
	//				}
	//				
	//
	//			} else {
	//				temp += fileSrc.getName();
	//				int fg=0;
	//
	//				FTPFile[] lsf= FTPUtil.getListFile(pathDes);
	//				if(lsf!=null && lsf.length>0){
	//					
	//					for(int k=0;k<lsf.length;k++){
	//						if(lsf[k].isFile() && lsf[k].getName().equals(fileSrc.getName())){
	//							fg=1;
	//							break;
	//						}
	//					}
	//				}
	//				
	//				if(fg==1){
	//					lstupexit.add(fileSrc);
	//				}
	//				else
	//					client.storeFile(pathDes + temp, new FileInputStream(fileSrc));
	//				
	//			}
	//
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		} finally {
	//
	//		}
	//	}

	public  boolean testExitUploadFile(File fileSrc, String pathDes, String temp) {
		boolean kq=false;
		try {

			if (!fileSrc.isFile()) {
				temp += fileSrc.getName();
				if(client.changeWorkingDirectory(pathDes + temp)){
					//					sizelstupexit +=fileSrc.length();
					kq=true;
				}
			} else {
				temp += fileSrc.getName();
				int fg=0;
				FTPFile[] lsf= FTPUtil.getListFile(pathDes);
				if(lsf!=null && lsf.length>0){

					for(int k=0;k<lsf.length;k++){
						if(lsf[k].isFile() && lsf[k].getName().equals(fileSrc.getName())){
							fg=1;
							break;
						}
					}
				}

				if(fg==1){
					//					sizelstupexit +=fileSrc.length();
					kq=true;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return kq;
	}

	public static void uploadFTPOK(List<File> filesSrc, String pathDes) {
		connects();
		int ck=0;
		for (File ff: filesSrc) {
			uploadFileOK(ff, pathDes, "/");
			ck++;
			if(ck==filesSrc.size()){
				progressBarStatus=(int)fileSizeTotalup;

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


				if (progressBarStatus >= fileSizeTotalup) {
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

	public static void uploadFileOK(File fileSrc, String pathDes, String temp) {
		try {

			if (!fileSrc.isFile()) {
				temp += fileSrc.getName();

				if(pathDes.equals("/"))
					pathDes="";

				client.makeDirectory(pathDes + temp);

				File[] files = fileSrc.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						uploadFileOK(files[i], pathDes, temp + "/");
					}
				}

			} else {
				temp += fileSrc.getName();
				client.storeFile(pathDes + temp, new FileInputStream(fileSrc));
				sizelstupok += fileSrc.length();
				progressBarStatus=(int)sizelstupok;
				System.out.println("proc: "+sizelstupok);

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


				if (progressBarStatus >= fileSizeTotalup) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progressBar.dismiss();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		if(lang.equals("EN")){
			inflater.inflate(R.menu.option_menu_upload, menu);
		}else if(lang.equals("VN")){
			inflater.inflate(R.menu.option_menu_upload_vn, menu);
		}else{
			inflater.inflate(R.menu.option_menu_upload_kr, menu);
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
			case R.id.uploadvn:
				processUpload();
				return true;
			case R.id.new_folder_uploadvn:
				processNewfolderUpload();
				return true;
			case R.id.past_uploadvn:
//				processPasteUpload();
				processUploadPaste();
				return true;
			case R.id.changeview_uploadvn:
				changeView();
				return true;
			case R.id.updeletevn:
				multidelete();
				return true;
			case R.id.copy_uploadvn:
				processCopyMutil();
				return true;
			case R.id.sort_uploadvn:
				sort();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}else if(lang.equals("KR")){
			switch (item.getItemId()) {
			case R.id.uploadkr:
				processUpload();
				return true;
			case R.id.new_folder_uploadkr:
				processNewfolderUpload();
				return true;
			case R.id.past_uploadkr:
//				processPasteUpload();
				processUploadPaste();
				return true;
			case R.id.changeview_uploadkr:
				changeView();
				return true;
			case R.id.updeletekr:
				multidelete();
				return true;
			case R.id.copy_uploadkr:
				processCopyMutil();
				return true;
			case R.id.sort_uploadkr:
				sort();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}else{
			switch (item.getItemId()) {
			case R.id.upload:
				processUpload();
				return true;
			case R.id.new_folder_upload:
				processNewfolderUpload();
				return true;
			case R.id.past_upload:
//				processPasteUpload();
				processUploadPaste();
				return true;
			case R.id.changeview_upload:
				changeView();
				return true;
			case R.id.updelete:
				multidelete();
				return true;
			case R.id.copy_upload:
				processCopyMutil();
				return true;
			case R.id.sort_upload:
				sort();
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

	}

	public void processNewfolderUpload(){
		if(co==0){
			co=1;
			newfolderDialog();
		}
	}

	public void processUpload(){		
		if(co==0){
			co=1;
			if(adapter.getListCheck()!=null && adapter.getListCheck().size()>0){
				uploadDialog();
			}else{
				openDialogError();
			}
		}
	}

	private void changeView(){
		FtpClientActivity.flagChangeViewUpload =! FtpClientActivity.flagChangeViewUpload;
		loadListFiles(tempPath);
	}

	private void multidelete(){
		if(adapter.getListCheck()!=null && adapter.getListCheck().size()>0){
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(UploadActivity.this);
			alertDialog.setTitle(language.getString("delete"));
			alertDialog.setMessage(language.getString("deleteall")+" ?");
			alertDialog.setCancelable(false);
			alertDialog.setPositiveButton(language.getString("ok"),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					linProgressBarup.setVisibility(View.VISIBLE);
					 
			        try{
			            new Thread(){
			                public void run() {
			                	for(int i=0;i<adapter.getListCheck().size();i++){
									deleteRecursive(adapter.getListCheck().get(i));
								}
			                    uiHandlerup.post( new Runnable(){
			                        @Override
			                        public void run() {
			                            if(isUpdateRequiredup){
			                                //TODO:
			                            }else{
			                                linProgressBarup.setVisibility(View.GONE);
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
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(UploadActivity.this);
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


	public static  long getTotalSize(File file) {
		long result = 0;

		if(file.isFile()){
			result+=file.length();
		}else{
			File[] fileList = file.listFiles();
			for(int i = 0; i < fileList.length; i++) {
				if(fileList[i].isDirectory()) {
					result += getTotalSize(fileList [i]);
				} else {
					result += fileList[i].length();
				}
			}
		}

		return result; 
	}


	public void processUploadPaste(){
		if(lstcopy!=null && lstcopy.size() > 0){
			
			if(tempPath.equals(srcpath) ){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(language.getString("warning"));
				builder.setMessage(language.getString("copycontaint"));
				builder.setCancelable(false);
				builder.setPositiveButton(language.getString("cancel"),new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
					}
				});
				
				builder.show();
			}else{
				final File destPath = new File(tempPath);
				File[] fs = destPath.listFiles();
				if(fs!=null && fs.length>0){
					String srcsub=srcpath;					
					
					for(int i=0;i<lstcopy.size();i++){
						boolean kttrung=false;
						keepValueCopy=lstcopy.get(i);
						srcsub=srcpath+"/"+keepValueCopy.getName();
						
						if( tempPath.contains(srcsub)){
							System.out.println("sub con con: "+keepValueCopy.getName());
							AlertDialog.Builder builder = new AlertDialog.Builder(this);
							builder.setTitle(language.getString("warning"));
							builder.setMessage(keepValueCopy.getName()+": "+language.getString("copycontaint"));
							builder.setCancelable(false);
							builder.setPositiveButton(language.getString("cancel"),new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									
								}
							});
							
							builder.show();
						}else{
			
							for(int j=0;j<fs.length;j++){
								if(fs[j].getName().equals(keepValueCopy.getName())){
									kttrung=true;
								}
							}			
							
							if(kttrung==true){
								System.out.println("xu ly trung :"+keepValueCopy.getName());
								AlertDialog.Builder builder = new AlertDialog.Builder(this);
								builder.setTitle(language.getString("warning"));
								
								if(keepValueCopy.isDirectory()){
									
									builder.setMessage(keepValueCopy.getName()+": "+language.getString("warningsamefolder"));
									builder.setCancelable(false);
									builder.setPositiveButton(language.getString("ok"),new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											copyDir(keepValueCopy, destPath);
										}
									});
									builder.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											
										}
									});
									
								}else{
									builder.setMessage(keepValueCopy.getName()+": "+language.getString("warningsamefile"));
									builder.setCancelable(false);
									builder.setPositiveButton(language.getString("ok"),new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											subCopyFile(keepValueCopy, destPath);
										}
									});
									builder.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											
										}
									});
									
								}
								
								builder.show();
							}else{
								System.out.println("copy: "+keepValueCopy.getName());
								if(keepValueCopy.isDirectory()){
									copyDir(keepValueCopy, destPath);
								}else{
									subCopyFile(keepValueCopy, destPath);
								}
							}
						}
						
					}
				}else{
					System.out.println("copy het");
					linProgressBarup.setVisibility(View.VISIBLE);					
					try{
			            new Thread(){
			                public void run() {
			                	for(int i=0;i<lstcopy.size();i++){
									keepValueCopy=lstcopy.get(i);
									if(keepValueCopy.isDirectory()){
										copyDir(keepValueCopy, destPath);
									}else{
										subCopyFile(keepValueCopy, destPath);
									}
								}
			                    uiHandlerup.post( new Runnable(){
			                        @Override
			                        public void run() {
			                            if(isUpdateRequiredup){
			                                //TODO:
			                            }else{
			                                linProgressBarup.setVisibility(View.GONE);
			                            }
			                        }
			                    } );
			                }
			        }.start();
			        }catch (Exception e) {}
				}
				
				loadListFiles(tempPath);
			}
		}
	}
	
	
	public void copyMutilDir(File srcPath,File dstPath){
		dstPath.mkdirs();	
		File[] files = srcPath.listFiles();
		for( File file : files ) {
			if( file.isDirectory() ) {
				copyMutilDir( file,new File(dstPath,file.getName()));
			} else {
				copyFileInDir( file, new File( dstPath, file.getName() ) );
			}
		}
	}
	
	public void openDialogErrorPaste(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(language.getString("warning"));
		builder.setMessage(language.getString("warningDialogUpload"));
		builder.setCancelable(false);
		builder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public void copyDir(final File srcPath , File dstPath){
//		if( !srcPath.isDirectory() ) {
//			throw new IllegalArgumentException( "Source (" + srcPath.getPath() + ") must be a directory." );
//		}
//
//		if( !srcPath.exists() ) {
//			throw new IllegalArgumentException( "Source directory (" + srcPath.getPath() + ") doesn't exist." );
//		}
//		dstPath = new File(dstPath.getAbsolutePath()+"/" + srcPath.getName().toString());
//		if(dstPath.exists() ) {
//			//			throw new IllegalArgumentException( "Destination (" + dstPath.getPath() + ") exists." );
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle(language.getString("warning"));
//			builder.setMessage("sssssss:"+language.getString("warningsamefolder"));
//			final File dstPaths =  dstPath;
//			System.out.println("dstpath after overwrite ++ " + dstPaths);
//			builder.setCancelable(false);
//			builder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					adapter.deleteRecursive(dstPaths);
//					File[] files = srcPath.listFiles();
//
//					for( File file : files ) {
//						if( file.isDirectory() ) {
//							copyDir( file,new File( dstPaths.getAbsoluteFile().toString()));
//
//						} else {
//							copyFileInDir( file, new File( dstPaths, file.getName() ) );
//						}
//					}
//				}
//			});
//			builder.setNegativeButton(language.getString("cancel"), new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//			});
//			builder.show();
//		}
		dstPath = new File(dstPath.getAbsolutePath()+"/" + srcPath.getName().toString());
		if(!dstPath.exists()){
			dstPath.mkdirs();	
			File[] files = srcPath.listFiles();

			for( File file : files ) {
				if( file.isDirectory() ) {
					copyDir( file,new File( dstPath.getAbsoluteFile().toString()));

				} else {
					copyFileInDir( file, new File( dstPath, file.getName() ) );
				}
			}
		}else{
			File[] files = srcPath.listFiles();

			for( File file : files ) {
				if( file.isDirectory() ) {
					copyDir( file,new File( dstPath.getAbsoluteFile().toString()));

				} else {
					copyFileInDir( file, new File( dstPath, file.getName() ) );
				}
			}
		}

	}
	
	public void copyFileInDir(File src,File dest){
		try{
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest); 

			byte[] buffer = new byte[1024];

			int length;
			while ((length = in.read(buffer)) > 0){
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
			System.out.println("File copied from " + src + " to " + dest);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public void copyFile(final File srcPath,final File dstPath){
		File[] listFile = dstPath.listFiles();
		System.out.println("list files : " + listFile.length);
		if(listFile != null && listFile.length >0){
			for (File file:listFile) {
				System.out.println("File : " + file.getName().toString());
				if(srcPath.getName().toString().equals(file.getName().toString())){
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(language.getString("warning"));
					builder.setMessage(language.getString("warningsamefile"));
					builder.setCancelable(false);
					builder.setPositiveButton(language.getString("ok"), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//							adapter.deleteRecursive(file);
							subCopyFile(srcPath,dstPath);
						}
					});
					builder.setNegativeButton(language.getString("cancel"),new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					builder.show();
				}
				else{
					subCopyFile(srcPath,dstPath);
				}
			}
		}
		else{
			subCopyFile(srcPath, dstPath);
		}


	}

	private void subCopyFile( File srcPath, File dstPath){
		InputStream in; 
		try {
			in = new FileInputStream(srcPath);
			if(dstPath.canWrite()){
				BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(new File(dstPath, srcPath.getName().toString()))); 
				byte byt[] = new byte[1024]; 
				int i; 

				for (long l = 0L; (i = in.read(byt)) != -1; l += i ) {
					buffer.write(byt, 0, i);
					buffer.flush();
				}

				buffer.close();
				in.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void processCopyMutil(){
		if(adapter.getListCheck()!=null && adapter.getListCheck().size()>0){
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UploadActivity.this);
			alertBuilder.setTitle(language.getString("copy"));
			alertBuilder.setMessage(language.getString("questioncopy"));
			alertBuilder.setPositiveButton(language.getString("ok"),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {
					lstcopy.clear();
					lstcopy=adapter.getListCheck();
					srcpath=tempPath;
//					List<File> temps=adapter.getListCheck();
//					for (int i = 0; i < temps.size(); i++) {
//						lstcopy.add(new CustomFile(temps.get(i)));
//						System.out.println("lst: "+temps.get(i).getName());
//					}
				}
			});

			alertBuilder.setNegativeButton(language.getString("cancel"),
					new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog,
						int which) {

				}
			});
			AlertDialog alert = alertBuilder.create();
			alert.show();
		}else{
			openDialogErrorPaste();
		}
	}

	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);

	}

	private void sort(){
		flagsortupload=!flagsortupload;
		loadListFiles(tempPath);
	}
	
}
