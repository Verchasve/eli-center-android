package com.eli.filemanager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fpt.dao.FtpConfiglangDAO;
import fpt.dao.FtpConnectDAO;
import fpt.dao.FtpConnectSQLITE;
import fpt.dao.FtpUserDAO;
import fpt.util.FTPUtil;
import fpt.util.UtilInitLang;
import ftp.pojo.FtpConfiglang;
import ftp.pojo.FtpConnect;
import ftp.pojo.FtpUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class LogoutActivity extends Activity {

	class PInfo {
		private String appname = "";
		private String pname = "";
		private String versionName = "";
		private int versionCode = 0;
		//private Drawable icon;
		/*private void prettyPrint() {
            //Log.v(appname + "\t" + pname + "\t" + versionName + "\t" + versionCode);
        }*/
	}
	public int co=0;
	EditText username,pass,port;
	private Bundle alllangs = new Bundle();
	String typeLang = "EN";
	UtilInitLang utilang = new UtilInitLang();
	private boolean read = true;
	ImageView imageVN,imageEN,imageKR;
	FtpUser emp = new FtpUser();
	public FTPClient mFTPClient = null;
	private String userName,password;
	FtpConnect ftp=new FtpConnect();
	Button btLogin;
	String host="";

	//auto update
	public int VersionCode;
	public String VersionName="";
	public String ApkName ;
	public String AppName ;
	public String BuildVersionPath="";
	public String urlpath ;
	public String PackageName;
	public String InstallAppPackageName;
	public String Text="";
	Bundle lang=new Bundle();
	
	// TODO hongmophi
	private Spinner lstSpinnerHost;
	private Button bttAdd,bttEdit,bttDelete;
	private List<FtpConnect> lstConnect = new ArrayList<FtpConnect>();
	private FtpConnectDAO connectDAO = new FtpConnectDAO();

	private FtpConnectSQLITE ftpConnectSQLITE;
	
	public  String ahostname="";
	public  String aport="";
	public  String ausername="";
	public  String apassword="";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       

		Text= "New".toString();


		ApkName = "FtpClient.apk";//"Test1.apk";// //"DownLoadOnSDcard_01.apk"; //      
		AppName="FtpClient";
		BuildVersionPath = "http://14.63.215.15/ftp_version.txt".toString();
		PackageName = "package:com.SelfInstall01".toString(); //"package:com.Test1".toString();
		urlpath = "http://14.63.215.15/" + ApkName.toString();


		GetVersionFromServer(BuildVersionPath); 
		
		 readFile();
	     lang = this.getAlllangs().getBundle(typeLang);

		if(checkInstalledApp(AppName.toString()) == true)
		{   
			Toast.makeText(getApplicationContext(), lang.getString("appfound")+" " + AppName.toString(), Toast.LENGTH_SHORT).show();


		}else{
			Toast.makeText(getApplicationContext(), lang.getString("appnotfound")+" "+ AppName.toString(), Toast.LENGTH_SHORT).show();          

		}
        
        setContentView(R.layout.main);
        
        
       // typeLang =getIntent().getExtras().getString("langexit");
        
        ftpConnectSQLITE = new FtpConnectSQLITE(this);
		ftpConnectSQLITE.open();
		
		username =(EditText)findViewById(R.id.userName);
		pass =(EditText)findViewById(R.id.pass);

		initAttribute();        

		imageVN = (ImageView)findViewById(R.id.imageViewVN);
		imageEN = (ImageView)findViewById(R.id.imageViewUK);
		imageKR = (ImageView)findViewById(R.id.imageViewKR);

		imageVN.setOnClickListener(processImageVN);
		imageEN.setOnClickListener(processImageEN);
		imageKR.setOnClickListener(processImageKR);


						

		//		if(!username.getText().toString().equals("") && !pass.getText().toString().equals("") ){
		//				emp = checkLogin(username.getText().toString(), pass.getText().toString());
		//				if (emp != null) {
		//					startActivity();
		//				} 
		//				else {
		//					openDialogError();
		//				}
		//		}
		//		else{
		btLogin.setOnClickListener(loginbtn);
		//		}

		//		final CheckBox rememberMeCbx = (CheckBox)findViewById(R.id.checkRemember);
		//		rememberMeCbx.setOnClickListener(new OnClickListener() {
		//			
		//			
		//			public void onClick(View v) {
		//				 if (((CheckBox) v).isChecked()) {
		//					 //System.out.prln(" boolean check : " + ((CheckBox) v).isChecked());
		//					 try {
		//					 userName = username.getText().toString();
		//					 password = pass.getText().toString();
		//					 if(!username.equals("") && !password.equals("")){
		//						 //System.out.prln("Last username :" + userName);
		//						 //System.out.prln("Last password : " + password);
		////						 	File f = new File("user.txt");
		//							FileOutputStream file = openFileOutput("user.txt", Context.MODE_PRIVATE);
		//							file.write(userName.getBytes());
		//							file.write("-".getBytes());
		//							MessageDigest ms = MessageDigest.getInstance("MD5");
		//							byte[] enpas =ms.digest(password.getBytes());
		//							file.write(password.getBytes());
		//							file.close();
		//					 }
		//						} catch (NoSuchAlgorithmException e) {
		//							e.printStackTrace();
		//						} catch (IOException e) {
		//							e.printStackTrace();
		//						}
		//					 
		//				 }else{
		//					 //System.out.prln("Not check remember ++++++++++++++++++++++++++++++");
		//				 }
		//			}
		//		});



		// buttom add
		bttAdd = (Button)findViewById(R.id.buttonAdd);
		//		bttAdd.setOnClickListener(processBttAdd);
		bttAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(co==0){
					co=1;
//					showpopupAddHost();
					showpopupaddhostFromAndroid();
				}
			}
		});

		bttEdit = (Button)findViewById(R.id.buttonEdit);
		//		bttEdit.setOnClickListener(processBttedit);
		bttEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(co==0){
					co=1;
					showpopupEditHost();
					showpopupedithostfromAdroid();
				}
			}
		});

		bttDelete = (Button)findViewById(R.id.buttonDelete);
		bttDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showPopupDelete();
			}
		});

       
    }
    
    private OnClickListener loginbtn = new OnClickListener() {


		public void onClick(View v) {
			// TODO Auto-generated method stub
			//emp = checkLogin(username.getText().toString(), pass.getText().toString());
			try{
				FTPUtil.HOST=host;

				FTPUtil.PORT=Integer.parseInt(port.getText().toString());

				FTPUtil.USER=username.getText().toString();
				FTPUtil.PASS=pass.getText().toString();

				//System.out.prln("host === " + host);
				//System.out.prln("port === " + Integer.parseInt(port.getText().toString()));
				//System.out.prln("user name === " + username.getText().toString());
				//System.out.prln("password  === " + pass.getText().toString());
				if (FTPUtil.connects()) {

					ftp.setHostname(host);
					ftp.setPort(port.getText().toString());
					ftp.setPassword(pass.getText().toString());
					ftp.setUsername(username.getText().toString());					
					startActivity();
				} else {
					openDialogError();
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} 
		}

	}; 

	private void openDialogError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		Bundle lang = this.getAlllangs().getBundle(typeLang);
		builder.setTitle(lang.getString("warning"));
		builder.setMessage(lang.getString("rploginfail"));
		builder.setCancelable(false);
		builder.setPositiveButton(lang.getString("ok"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	private void startActivity() {
		//    	FtpConnect ftp= FtpConnectDAO.getFTP();
		//    	if(ftp.getHostname()!=null){
		//    		FTPUtil.HOST=ftp.getHostname();
		//    		FTPUtil.PORT=ftp.getPort();
		//    		FTPUtil.USER=ftp.getUsername();
		//    		FTPUtil.PASS=ftp.getPassword();
		//    		FTPUtil.connects();
		//    	}else{
		//    		FTPUtil.connects();
		//    	}
		//    	FTPUtil.closeConnection();

		Intent ii = new Intent(this, MainActivity2.class);
		ii.putExtra("lang", typeLang);
		//ii.putExtra("em",aa);
		ii.putExtras(alllangs);		
		ii.putExtra("ftp", ftp);
		startActivity(ii);
	}

	public FtpUser checkLogin(String username, String pass) {
		FtpUserDAO lg = new FtpUserDAO();
		FtpUser emp = lg.checkLogin(username, pass);
		return emp;
	}
	public void initLangue(){
		
//		Bundle lang = this.getAlllangs().getBundle(typeLang);
		TextView thost=(TextView)findViewById(R.id.host);
		thost.setText(lang.getString("hostname"));

		TextView tport=(TextView)findViewById(R.id.Port);
		tport.setText(lang.getString("port"));		

		TextView tendn = (TextView) findViewById(R.id.tendn);
		tendn.setText(lang.getString("username"));

		TextView pw = (TextView) findViewById(R.id.matkhau);
		pw.setText(lang.getString("password"));
		
		btLogin.setText(lang.getString("connect"));
		bttAdd.setText(lang.getString("add"));
		bttEdit.setText(lang.getString("edit"));
		bttDelete.setText(lang.getString("delete"));

		
	}
	public void initAttribute() {
		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth()*3/10;
		
		
		TextView thost=(TextView)findViewById(R.id.host);
		thost.setText(lang.getString("hostname"));

		TextView tport=(TextView)findViewById(R.id.Port);
		tport.setText(lang.getString("port"));

		port = (EditText) findViewById(R.id.txtPort);

		TextView t = (TextView) findViewById(R.id.tendn);
		t.setText(lang.getString("username"));

		TextView t2 = (TextView) findViewById(R.id.matkhau);
		t2.setText(lang.getString("password"));

		btLogin = (Button) findViewById(R.id.btLogin);
		btLogin.setText(lang.getString("connect"));

		bttAdd = (Button)findViewById(R.id.buttonAdd);
		bttEdit = (Button)findViewById(R.id.buttonEdit);
		bttDelete = (Button)findViewById(R.id.buttonDelete);

		System.out.println("width: "+width);
		bttAdd.setWidth(width);
		bttEdit.setWidth(width);
		bttDelete.setWidth(width);

		bttAdd.setText(lang.getString("add"));
		bttEdit.setText(lang.getString("edit"));
		bttDelete.setText(lang.getString("delete"));

		lstSpinnerHost = (Spinner)findViewById(R.id.spinnerhost);
//		lstConnect = connectDAO.getAllConnectFtpHost();
		lstConnect = ftpConnectSQLITE.getAllConnect();
		ArrayAdapter<FtpConnect> adapter = new ArrayAdapter<FtpConnect>(this,
				android.R.layout.simple_spinner_dropdown_item,lstConnect);
		lstSpinnerHost.setAdapter(adapter);
		lstSpinnerHost.setOnItemSelectedListener(processSpinner);

		//		CheckBox remempass =(CheckBox)findViewById(R.id.checkRemember);
		//		remempass.setText(lang.getString("rememberpass"));
	}

//	private void readFile(){		
//		try {
//			FileInputStream fr = openFileInput("user.txt");
//			Scanner scanner = new Scanner(fr);
//			scanner.useDelimiter("-");
//			username.setText(scanner.next().trim());
//			pass.setText(scanner.next().trim());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	private void readFile(){		
		typeLang="EN";
		try {

			FileInputStream fr = openFileInput("user.txt");
			Scanner scanner = new Scanner(fr);
			typeLang=scanner.next().trim();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Bundle getAlllangs() {
		if (isRead()) {
			FtpConfiglangDAO lang = new FtpConfiglangDAO();
			Bundle resultVN = new Bundle();
			Bundle resultEN = new Bundle();
			Bundle resultKR = new Bundle();
			try {
				ArrayList<FtpConfiglang> arr = lang.allLang();
				for (int i = 0; i < arr.size(); i++) {
					resultVN.putString(arr.get(i).getIdkey(), arr.get(i).getVn());
					resultEN.putString(arr.get(i).getIdkey(), arr.get(i).getEn());
					resultKR.putString(arr.get(i).getIdkey(), arr.get(i).getKr());
				};
			} catch (Exception E) {
				E.printStackTrace();
			}
			alllangs.putBundle("VN", resultVN);
			alllangs.putBundle("EN", resultEN);
			alllangs.putBundle("KR", resultKR);
		}
		setRead(false);
		return alllangs;
	}

	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}

	public OnClickListener processImageVN = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			typeLang = "VN";
			initLangue();
		}
	};

	public OnClickListener processImageEN = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			typeLang = "EN";
			initLangue();
		}
	};

	public OnClickListener processImageKR = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			typeLang = "KR";
			initLangue();
		}
	};

	private OnClickListener processBttAdd = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent(LogoutActivity.this,AddHostActivity.class);
			intent.putExtra("lang", typeLang);
			intent.putExtras(alllangs);
			//if(lstConnect.size()==0)
			//	CreateXMLFile.CreateXMLFile();
			startActivity(intent);
		}
	};

	public OnItemSelectedListener processSpinner = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			ftp = (FtpConnect)lstSpinnerHost.getItemAtPosition(arg2);
			host = ftp.getHostname();
//			port.setText(String.valueOf(ftp.getPort()));
			port.setText(ftp.getPort());
//			host=ftp.getHostname();
			username.setText(ftp.getUsername());
			pass.setText(ftp.getPassword());
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			Log.d(getApplicationContext().getPackageName(), "Select Item :"
					+ "Nothing");
			typeLang = "EN";			
		}
	};

	public OnClickListener processBttedit = new OnClickListener() {

		@Override
		public void onClick(View v) {

			Intent intent = new Intent(LogoutActivity.this, EditHostActivity.class);
			intent.putExtra("lang", typeLang);
			intent.putExtra("ftp", ftp);
			intent.putExtras(alllangs);
			startActivity(intent);
		}
	};

	protected void showPopupDelete() {
		AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
//		Bundle lang = this.getAlllangs().getBundle(typeLang);
		helpBuilder.setTitle(lang.getString("titledelete"));
		helpBuilder.setMessage(lang.getString("deleteerror"));
		helpBuilder.setPositiveButton(lang.getString("ok"),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//							connectDAO.deleteFtpConnect(ftp);
//				deleteHost();
				ftpConnectSQLITE.deleteFtp(ftp);
				Intent intent = new Intent(
						LogoutActivity.this,
						LogoutActivity.class);

				startActivity(intent);
				finish();
			}
		});

		helpBuilder.setNegativeButton(lang.getString("cancel"),
				new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// Do nothing
			}
		});

		// Remember, create doesn't show the dialog
		AlertDialog helpDialog = helpBuilder.create();
		helpDialog.show();
	}

	public void deleteHost(){
		try {
			File file = new File(Environment.getExternalStorageDirectory()+"/ftp_connect.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			//	
			int v=0;
			NodeList listftpconnect= doc.getElementsByTagName("ftpconnect");

			for(int s=0; s<listftpconnect.getLength(); s++){
				Node nodeftpconnect = listftpconnect.item(s);

				NodeList ftp = nodeftpconnect.getChildNodes();
				for(int i=0; i<ftp.getLength(); i++){

					Node node = ftp.item(i);
					//System.out.println(node.getNodeName());
					if("ftpname".equals(node.getNodeName())){
						if(node.getTextContent().equals(host)){
							v=s;
						}
					}
				}
			}

			Element element = (Element)doc.getElementsByTagName("ftpconnect").item(v);
			//  Remove the node
			element.getParentNode().removeChild(element);
			//  Normalize the DOM tree to combine all adjacent nodes
			doc.normalize();

			TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
			Transformer transformer = transformerFactory.newTransformer(); 
			DOMSource source = new DOMSource(doc); 
			StreamResult result = new StreamResult(file); 
			transformer.transform(source, result); 


		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}

	// show popup addhost
	protected void showpopupAddHost(){
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);          
		final View textEntryView = inflater.inflate(R.layout.addhost, null);

//		Bundle lang = this.getAlllangs().getBundle(typeLang);

		final EditText txtHostname = (EditText) textEntryView.findViewById(R.id.txtAddHost);
		txtHostname.setText(ahostname);
		final EditText txtPort = (EditText) textEntryView.findViewById(R.id.txtAddPort);
		txtPort.setText(aport);
		final EditText txtUserName = (EditText) textEntryView.findViewById(R.id.txtUserName);
		txtUserName.setText(ausername);
		final EditText txtPass = (EditText) textEntryView.findViewById(R.id.txtPass);
		txtPass.setText(apassword);
		final TextView hostname = (TextView) textEntryView.findViewById(R.id.addhostName);
		final TextView vport = (TextView) textEntryView.findViewById(R.id.aaPort);
		final TextView vus = (TextView) textEntryView.findViewById(R.id.addUsername);
		final TextView vps = (TextView) textEntryView.findViewById(R.id.addPass);

		hostname.setText(lang.getString("hostname"));
		vport.setText(lang.getString("port"));
		vus.setText(lang.getString("username"));
		vps.setText(lang.getString("password"));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(lang.getString("addhost"));
		builder.setView(textEntryView);
		co=0;
		builder.setPositiveButton(lang.getString("ok"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					
					

					DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

					// root elements
					Document doc = docBuilder.newDocument();
					Element rootElement = doc.createElement("ftpconnects");
					doc.appendChild(rootElement);

					// staff elements
					Element staff = doc.createElement("ftpconnect");
					rootElement.appendChild(staff);

					// firstname elements
					Element firstname = doc.createElement("ftpname");
					System.out.println(txtHostname.getText().toString());
					firstname.appendChild(doc.createTextNode(txtHostname.getText().toString()));
					staff.appendChild(firstname);
					System.out.println(txtHostname.getText().toString());

					// lastname elements
					Element port = doc.createElement("port");
					port.appendChild(doc.createTextNode(txtPort.getText().toString()));
					staff.appendChild(port);	
					//					}

					// nickname elements
					Element username = doc.createElement("username");
					username.appendChild(doc.createTextNode(txtUserName.getText().toString()));
					staff.appendChild(username);	
					//					}

					// salary elements
					Element password = doc.createElement("password");
					password.appendChild(doc.createTextNode(txtPass.getText().toString()));
					staff.appendChild(password);	

					// write the content into xml file
					TransformerFactory transformerFactory = TransformerFactory.newInstance();
					Transformer transformer = transformerFactory.newTransformer();
					DOMSource source = new DOMSource(doc);
					String fileName = Environment.getExternalStorageDirectory()+"/ftp_connect.xml";
					File file = new File(fileName);
					boolean success;
					try {
						success = file.createNewFile();
						StreamResult result = new StreamResult(file);
						if(success &&  !txtHostname.getText().toString().equals("") && !txtPort.getText().toString().equals("")
								&& !txtUserName.getText().toString().equals("") && !txtPass.getText().toString().equals("")){
//							ahostname=txtHostname.getText().toString();
//							aport=txtPort.getText().toString();
//							ausername=txtUserName.getText().toString();
//							apassword=txtPass.getText().toString();
							transformer.transform(source, result);
							System.out.println("File saved!");
							Intent intent = new Intent(LogoutActivity.this, LogoutActivity.class);
							startActivity(intent);
							
						}else if(success &&  (txtHostname.getText().toString().equals("") || txtPort.getText().toString().equals("")
								|| txtUserName.getText().toString().equals("") || txtPass.getText().toString().equals(""))){

							ahostname=txtHostname.getText().toString();
							aport=txtPort.getText().toString();
							ausername=txtUserName.getText().toString();
							apassword=txtPass.getText().toString();
							
							Toast.makeText(getApplicationContext(), lang.getString("isempty"), Toast.LENGTH_LONG).show();
							showpopupAddHost();
						}
						else if(!success && !txtHostname.getText().toString().equals("") && !txtPort.getText().toString().equals("")
								&& !txtUserName.getText().toString().equals("") && !txtPass.getText().toString().equals("")){
							try {
								ahostname="";
								aport="";
								ausername="";
								apassword="";
								File file2 = new File(Environment.getExternalStorageDirectory()+"/ftp_connect.xml");
								DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
								DocumentBuilder docBuilder2 = factory.newDocumentBuilder();
								Document doc2 = docBuilder2.parse(file2);

								Element rootElement2 = doc2.getDocumentElement();
								Element staff2 = doc2.createElement("ftpconnect");
								rootElement2.appendChild(staff2);

								Element firstname2 = doc2.createElement("ftpname");
								firstname2.appendChild(doc2.createTextNode(txtHostname.getText().toString()));
								staff2.appendChild(firstname2);	

								Element port2 = doc2.createElement("port");
								port2.appendChild(doc2.createTextNode(txtPort.getText().toString()));
								staff2.appendChild(port2);	

								Element username2 = doc2.createElement("username");
								username2.appendChild(doc2.createTextNode(txtUserName.getText().toString()));
								staff2.appendChild(username2);	

								Element salary2 = doc2.createElement("password");
								salary2.appendChild(doc2.createTextNode(txtPass.getText().toString()));
								staff2.appendChild(salary2);	

								TransformerFactory transfac = TransformerFactory.newInstance();
								Transformer trans = transfac.newTransformer();

								StringWriter sw = new StringWriter();
								StreamResult result2 = new StreamResult(sw);
								DOMSource source2 = new DOMSource(doc2);
								trans.transform(source2, result2);
								String xmlString = sw.toString();

								OutputStream f0;
								byte buf[] = xmlString.getBytes();
								f0 = new FileOutputStream(Environment.getExternalStorageDirectory()+"/ftp_connect.xml");
								for(int i=0;i<buf .length;i++) {
									f0.write(buf[i]);
								}
								f0.close();
								buf = null;
							}
							catch(SAXException e) {
								e.printStackTrace();
							}
							catch(IOException e) {
								e.printStackTrace();
							}
							catch(ParserConfigurationException e) {
								e.printStackTrace();
							}
							catch(TransformerConfigurationException e) {
								e.printStackTrace();
							}
							catch(TransformerException e) {
								e.printStackTrace();
							}
							System.out.println("new node have been saved!");
							Intent intent = new Intent(LogoutActivity.this, LogoutActivity.class);
							startActivity(intent);
						}else if(!success &&  (txtHostname.getText().toString().equals("") || txtPort.getText().toString().equals("")
								|| txtUserName.getText().toString().equals("") || txtPass.getText().toString().equals(""))){
							Toast.makeText(getApplicationContext(), lang.getString("isempty"), Toast.LENGTH_LONG).show();
							ahostname=txtHostname.getText().toString();
							aport=txtPort.getText().toString();
							ausername=txtUserName.getText().toString();
							apassword=txtPass.getText().toString();
							showpopupAddHost();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (ParserConfigurationException pce) {
					pce.printStackTrace();
				} catch (TransformerException tfe) {
					tfe.printStackTrace();
				}
			}
		});

		builder.setNegativeButton(lang.getString("cancel"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}


	protected void showpopupEditHost(){
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);          
		final View textEntryView = inflater.inflate(R.layout.edithost, null);

//		final Bundle lang = this.getAlllangs().getBundle(typeLang);

		final EditText txtHostname = (EditText) textEntryView.findViewById(R.id.txteditHost);
		txtHostname.setText(ahostname);
		final EditText txtPort = (EditText) textEntryView.findViewById(R.id.txteditPort);
		txtPort.setText(aport);
		final EditText txtUserName = (EditText) textEntryView.findViewById(R.id.txteditUserName);
		txtUserName.setText(ausername);
		final EditText txtPass = (EditText) textEntryView.findViewById(R.id.txteditPass);
		txtPass.setText(apassword);

		txtHostname.setText(ftp.getHostname());
		txtPort.setText(String.valueOf(ftp.getPort()));
		txtUserName.setText(ftp.getUsername());
		txtPass.setText(ftp.getPassword());

		final TextView hostname = (TextView) textEntryView.findViewById(R.id.edithostName);
		final TextView vport = (TextView) textEntryView.findViewById(R.id.editPort);
		final TextView vus = (TextView) textEntryView.findViewById(R.id.editUsername);
		final TextView vps = (TextView) textEntryView.findViewById(R.id.editPass);

		hostname.setText(lang.getString("hostname"));
		vport.setText(lang.getString("port"));
		vus.setText(lang.getString("username"));
		vps.setText(lang.getString("password"));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(lang.getString("edithost"));
		builder.setView(textEntryView);
		co=0;
		builder.setPositiveButton(lang.getString("ok"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try{
					File file = new File(Environment.getExternalStorageDirectory()+"/ftp_connect.xml");
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder docBuilder = factory.newDocumentBuilder();
					Document doc = docBuilder.parse(file);

					int v=0;
					NodeList listftpconnect= doc.getElementsByTagName("ftpconnect");

					for(int s=0; s<listftpconnect.getLength(); s++){
						Node nodeftpconnect = listftpconnect.item(s);

						NodeList ftp1 = nodeftpconnect.getChildNodes();
						for(int i=0; i<ftp1.getLength(); i++){

							Node node = ftp1.item(i);
							if("ftpname".equals(node.getNodeName())){

								if(node.getTextContent().equals(ftp.getHostname())){
									v=s;
								}
							}
						}
					}

					Node staff = doc.getElementsByTagName("ftpconnect").item(v); 
					NodeList list = staff.getChildNodes(); 
					for (int i =0; i<list.getLength();i++){ 
						Node node = list.item(i); 
//						if("ftpname".equals(node.getNodeName())){
//							node.setTextContent(txtHostname.getText().toString()); 
//							System.out.println("ftp name ++++ " + txtHostname.getText().toString());
//						}
//						if("port".equals(node.getNodeName())){
//							node.setTextContent(txtPort.getText().toString()); 
//						}
//						if("username".equals(node.getNodeName())){
//							node.setTextContent(txtUserName.getText().toString()); 
//						}
//						if("password".equals(node.getNodeName())){
//							node.setTextContent(txtPass.getText().toString()); 
//						}
						
						if( !txtHostname.getText().toString().equals("") && !txtPort.getText().toString().equals("")
								&& !txtUserName.getText().toString().equals("") && !txtPass.getText().toString().equals("")){
							ahostname ="";
							aport = "";
							ausername = "";
							apassword = "";
							
							if("ftpname".equals(node.getNodeName())){
								node.setTextContent(txtHostname.getText().toString()); 
								System.out.println("ftp name ++++ " + txtHostname.getText().toString());
							}
							if("port".equals(node.getNodeName())){
								node.setTextContent(txtPort.getText().toString()); 
							}
							if("username".equals(node.getNodeName())){
								node.setTextContent(txtUserName.getText().toString()); 
							}
							if("password".equals(node.getNodeName())){
								node.setTextContent(txtPass.getText().toString()); 
							} 
							
							
							TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
							Transformer transformer = transformerFactory.newTransformer(); 
							DOMSource source = new DOMSource(doc); 
							StreamResult result = new StreamResult(file); 
							transformer.transform(source, result);
							
							Intent intent = new Intent(LogoutActivity.this, LogoutActivity.class);
							startActivity(intent);
						}
						else if(txtHostname.getText().toString().equals("") || txtPort.getText().toString().equals("")
								|| txtUserName.getText().toString().equals("") || txtPass.getText().toString().equals("")){
							ahostname = txtHostname.getText().toString();
							aport = txtPort.getText().toString();
							ausername = txtUserName.getText().toString();
							apassword = txtPass.getText().toString();
							
							Toast.makeText(getApplicationContext(),lang.getString("isempty"), Toast.LENGTH_LONG).show();
							showpopupEditHost();
						}
					} 

					

				} catch (ParserConfigurationException pce) {
					pce.printStackTrace();
				} catch (TransformerException tfe) {
					tfe.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 

				
			}
		});

		builder.setNegativeButton(lang.getString("cancel"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		AlertDialog alert = builder.create();
		alert.show();	 
	}
	private Boolean checkInstalledApp(String appName){
		return getPackages(appName);    
	}

	// Get Information about Only Specific application which is Install on Device.
	public String getInstallPackageVersionInfo(String appName) 
	{
		String InstallVersion = "";     
		ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
		final int max = apps.size();
		for (int i=0; i<max; i++) 
		{
			//apps.get(i).prettyPrint();        
			if(apps.get(i).appname.toString().equals(appName.toString()))
			{
				InstallVersion = lang.getString("installversioncode")+": "+ apps.get(i).versionCode+
				" "+lang.getString("versionname")+": "+ apps.get(i).versionName.toString();
				break;
			}
		}

		return InstallVersion.toString();
	}
	private Boolean getPackages(String appName) 
	{
		Boolean isInstalled = false;
		ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
		final int max = apps.size();
		for (int i=0; i<max; i++) 
		{
			//apps.get(i).prettyPrint();

			if(apps.get(i).appname.toString().equals(appName.toString()))
			{
				/*if(apps.get(i).versionName.toString().contains(VersionName.toString()) == true &&
                        VersionCode == apps.get(i).versionCode)
                {
                    isInstalled = true;
                    Toast.makeText(getApplicationContext(),
                            "Code Match", Toast.LENGTH_SHORT).show(); 
                    openMyDialog();
                }*/
				if(VersionCode <= apps.get(i).versionCode)
				{
					isInstalled = false;

					/*Toast.makeText(getApplicationContext(),
                            "Install Code is Less.!", Toast.LENGTH_SHORT).show();*/

					/*DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which)
							{
							case DialogInterface.BUTTON_POSITIVE:
								//Yes button clicked
								//SelfInstall01Activity.this.finish(); Close The App.

								DownloadOnSDcard();
								InstallApplication();
								UnInstallApplication(PackageName.toString());

								break;

							case DialogInterface.BUTTON_NEGATIVE:
								//No button clicked

								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("New Apk Available..").setPositiveButton("Yes Proceed", dialogClickListener)
					.setNegativeButton("No.", dialogClickListener).show();*/

				}    
				if(VersionCode > apps.get(i).versionCode)
				{
					isInstalled = true;
					/*Toast.makeText(getApplicationContext(),
                            "Install Code is better.!", Toast.LENGTH_SHORT).show();*/

					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() 
					{
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which)
							{
							case DialogInterface.BUTTON_POSITIVE:
								//Yes button clicked
								//SelfInstall01Activity.this.finish(); Close The App.

								DownloadOnSDcard();
								InstallApplication();
								UnInstallApplication(PackageName.toString());

								break;

							case DialogInterface.BUTTON_NEGATIVE:
								//No button clicked

								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(lang.getString("youhavenewversion")).setPositiveButton(lang.getString("install"), dialogClickListener)
					.setNegativeButton(lang.getString("cancel"), dialogClickListener).show();              
				}
			}
		}

		return isInstalled;
	}
	private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) 
	{       
		ArrayList<PInfo> res = new ArrayList<PInfo>();        
		List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);

		for(int i=0;i<packs.size();i++) 
		{
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue ;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			//newInfo.icon = p.applicationInfo.loadIcon(getPackageManager());
			res.add(newInfo);
		}
		return res; 
	}


	public void UnInstallApplication(String packageName)// Specific package Name Uninstall.
	{
		//Uri packageURI = Uri.parse("package:com.CheckInstallApp");
		Uri packageURI = Uri.parse(packageName.toString());
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		startActivity(uninstallIntent); 
	}
	public void InstallApplication()
	{   
		Uri packageURI = Uri.parse(PackageName.toString());
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, packageURI);

		//      Intent intent = new Intent(android.content.Intent.ACTION_VIEW);

		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//intent.setFlags(Intent.ACTION_PACKAGE_REPLACED);

		//intent.setAction(Settings. ACTION_APPLICATION_SETTINGS);

		intent.setDataAndType
		(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/download/"  + ApkName.toString())), "application/vnd.android.package-archive");

		// Not open this Below Line Bcuz...
		////intent.setClass(this, Project02Activity.class); // This Line Call Activity Recursively its dangerous.

		startActivity(intent);  
	}
	public void GetVersionFromServer(String BuildVersionPath)
	{
		//this is the file you want to download from the remote server          
		//path ="http://10.0.2.2:82/Version.txt";
		//this is the name of the local file you will create
		// version.txt contain Version Code = 2; \n Version name = 2.1;             
		URL u;
		try {
			u = new URL(BuildVersionPath.toString());

			HttpURLConnection c = (HttpURLConnection) u.openConnection();           
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect();

			//Toast.makeText(getApplicationContext(), "HttpURLConnection Complete.!", Toast.LENGTH_SHORT).show();  

			InputStream in = c.getInputStream();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			byte[] buffer = new byte[1024]; //that stops the reading after 1024 chars..
			//in.read(buffer); //  Read from Buffer.
			//baos.write(buffer); // Write Into Buffer.

			int len1 = 0;
			while ( (len1 = in.read(buffer)) != -1 ) 
			{               
				baos.write(buffer,0, len1); // Write Into ByteArrayOutputStream Buffer.
			}

			String temp = "";     
			String s = baos.toString();// baos.toString(); contain Version Code = 2; \n Version name = 2.1;

			for (int i = 0; i < s.length(); i++)
			{               
				i = s.indexOf("=") + 1; 
				while (s.charAt(i) == ' ') // Skip Spaces
				{
					i++; // Move to Next.
				}
				while (s.charAt(i) != ';'&& (s.charAt(i) >= '0' && s.charAt(i) <= '9' || s.charAt(i) == '.'))
				{
					temp = temp.toString().concat(Character.toString(s.charAt(i))) ;
					i++;
				}
				//
				s = s.substring(i); // Move to Next to Process.!
				temp = temp + " "; // Separate w.r.t Space Version Code and Version Name.
			}
			String[] fields = temp.split(" ");// Make Array for Version Code and Version Name.

			VersionCode = Integer.parseInt(fields[0].toString());// .ToString() Return String Value.
			VersionName = fields[1].toString();

			System.out.println("Vesiom code _ " + VersionCode);
			System.out.println("Vesion name _ " + VersionName);
			baos.close();
		}
		catch (MalformedURLException e) {
			Toast.makeText(getApplicationContext(), "Error." + e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (IOException e) {           
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "Error." + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		//return true;
	}// Method End.

	// Download On My Mobile SDCard or Emulator.
	public void DownloadOnSDcard()
	{
		try{
			URL url = new URL(urlpath.toString()); // Your given URL.

			HttpURLConnection c = (HttpURLConnection) url.openConnection();
			c.setRequestMethod("GET");
			c.setDoOutput(true);
			c.connect(); // Connection Complete here.!

			//Toast.makeText(getApplicationContext(), "HttpURLConnection complete.", Toast.LENGTH_SHORT).show();

			String PATH = Environment.getExternalStorageDirectory() + "/download/";
			File file = new File(PATH); // PATH = /mnt/sdcard/download/
			if (!file.exists()) {
				file.mkdirs();
			}
			File outputFile = new File(file, ApkName.toString());           
			FileOutputStream fos = new FileOutputStream(outputFile);

			//      Toast.makeText(getApplicationContext(), "SD Card Path: " + outputFile.toString(), Toast.LENGTH_SHORT).show();

			InputStream is = c.getInputStream(); // Get from Server and Catch In Input Stream Object.

			byte[] buffer = new byte[1024];
			int len1 = 0;
			while ((len1 = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len1); // Write In FileOutputStream.
			}
			fos.close();
			is.close();//till here, it works fine - .apk is download to my sdcard in download file.
			// So plz Check in DDMS tab and Select your Emualtor.

			//Toast.makeText(getApplicationContext(), "Download Complete on SD Card.!", Toast.LENGTH_SHORT).show();
			//download the APK to sdcard then fire the Intent.
		} 
		catch (IOException e) 
		{
			Toast.makeText(getApplicationContext(), "Error! " +
					e.toString(), Toast.LENGTH_LONG).show();
		}           
	}
	
	// show popup add host
	public void showpopupaddhostFromAndroid(){
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);          
		final View textEntryView = inflater.inflate(R.layout.addhost, null);

//		Bundle lang = this.getAlllangs().getBundle(typeLang);

		final EditText txtHostname = (EditText) textEntryView.findViewById(R.id.txtAddHost);
		txtHostname.setText(ahostname);
		final EditText txtPort = (EditText) textEntryView.findViewById(R.id.txtAddPort);
		txtPort.setText(aport);
		final EditText txtUserName = (EditText) textEntryView.findViewById(R.id.txtUserName);
		txtUserName.setText(ausername);
		final EditText txtPass = (EditText) textEntryView.findViewById(R.id.txtPass);
		txtPass.setText(apassword);
		final TextView hostname = (TextView) textEntryView.findViewById(R.id.addhostName);
		final TextView vport = (TextView) textEntryView.findViewById(R.id.aaPort);
		final TextView vus = (TextView) textEntryView.findViewById(R.id.addUsername);
		final TextView vps = (TextView) textEntryView.findViewById(R.id.addPass);

		hostname.setText(lang.getString("hostname"));
		vport.setText(lang.getString("port"));
		vus.setText(lang.getString("username"));
		vps.setText(lang.getString("password"));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(lang.getString("addhost"));
		builder.setView(textEntryView);
		co=0;
		builder.setPositiveButton(lang.getString("ok"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					if(!txtHostname.getText().toString().equals("") && !txtPort.getText().toString().equals("")
							&& !txtUserName.getText().toString().equals("") && !txtPass.getText().toString().equals("")){
						ahostname="";
						aport="";
						ausername="";
						apassword="";
						
						ftpConnectSQLITE.createFtpConnect(txtHostname.getText().toString(), txtPort.getText().toString(), txtUserName.getText().toString(),
								txtPass.getText().toString());

						Intent intent = new Intent(LogoutActivity.this, LogoutActivity.class);
						startActivity(intent);
					}
					else if(txtHostname.getText().toString().equals("") || txtPort.getText().toString().equals("")
							|| txtUserName.getText().toString().equals("") || txtPass.getText().toString().equals("")){

						ahostname = txtHostname.getText().toString();
						aport = txtPort.getText().toString();
						ausername = txtUserName.getText().toString();
						apassword = txtPass.getText().toString();

						Toast.makeText(getApplicationContext(),lang.getString("isempty"), Toast.LENGTH_LONG).show();
						showpopupaddhostFromAndroid();
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		);
		builder.setNegativeButton(lang.getString("cancel"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}
	
	// edit host 
	public void showpopupedithostfromAdroid(){
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);          
		final View textEntryView = inflater.inflate(R.layout.edithost, null);

//		Bundle lang = this.getAlllangs().getBundle(typeLang);

		final EditText txtHostname = (EditText) textEntryView.findViewById(R.id.txteditHost);
		txtHostname.setText(ahostname);
		final EditText txtPort = (EditText) textEntryView.findViewById(R.id.txteditPort);
		txtPort.setText(aport);
		final EditText txtUserName = (EditText) textEntryView.findViewById(R.id.txteditUserName);
		txtUserName.setText(ausername);
		final EditText txtPass = (EditText) textEntryView.findViewById(R.id.txteditPass);
		txtPass.setText(apassword);

		txtHostname.setText(ftp.getHostname());
		txtPort.setText(String.valueOf(ftp.getPort()));
		txtUserName.setText(ftp.getUsername());
		txtPass.setText(ftp.getPassword());

		final TextView hostname = (TextView) textEntryView.findViewById(R.id.edithostName);
		final TextView vport = (TextView) textEntryView.findViewById(R.id.editPort);
		final TextView vus = (TextView) textEntryView.findViewById(R.id.editUsername);
		final TextView vps = (TextView) textEntryView.findViewById(R.id.editPass);

		hostname.setText(lang.getString("hostname"));
		vport.setText(lang.getString("port"));
		vus.setText(lang.getString("username"));
		vps.setText(lang.getString("password"));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(lang.getString("edithost"));
		builder.setView(textEntryView);
		co=0;
		builder.setPositiveButton(lang.getString("ok"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if( !txtHostname.getText().toString().equals("") && !txtPort.getText().toString().equals("")
						&& !txtUserName.getText().toString().equals("") && !txtPass.getText().toString().equals("")){
					ahostname ="";
					aport = "";
					ausername = "";
					apassword = "";
					
					ftp.setHostname(txtHostname.getText().toString());
					ftp.setPort(txtPort.getText().toString());
					ftp.setUsername(txtUserName.getText().toString());
					ftp.setPassword(txtPass.getText().toString());
					
					ftpConnectSQLITE.updateFtp(ftp);
					
					Intent intent = new Intent(LogoutActivity.this, LogoutActivity.class);
					startActivity(intent);
				}
				else if(txtHostname.getText().toString().equals("") || txtPort.getText().toString().equals("")
						|| txtUserName.getText().toString().equals("") || txtPass.getText().toString().equals("")){
					ahostname = txtHostname.getText().toString();
					aport = txtPort.getText().toString();
					ausername = txtUserName.getText().toString();
					apassword = txtPass.getText().toString();
					
					Toast.makeText(getApplicationContext(),lang.getString("isempty"), Toast.LENGTH_LONG).show();
					showpopupEditHost();
				} 

				
			}
		});

		builder.setNegativeButton(lang.getString("cancel"), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});

		AlertDialog alert = builder.create();
		alert.show();	 
	}
}
