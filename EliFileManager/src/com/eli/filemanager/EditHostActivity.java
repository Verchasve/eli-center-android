package com.eli.filemanager;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fpt.dao.FtpConnectDAO;
import fpt.util.UtilInitLang;
import ftp.pojo.FtpConnect;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

public class EditHostActivity extends Activity{
	private TextView hostName,port,username,password,localDir,remoteDir;
	private EditText txtHostname,txtPort,txtUserName,txtPass,txtLocal,txtRemote;
	private Button bttSave,bttCancel;
	//	private RadioButton rdActive,rdInActive;


	String typeLang = "EN";
	UtilInitLang utilang = new UtilInitLang();
	String lang = "EN";
	Bundle language;
	private FtpConnectDAO connectDAO = new FtpConnectDAO();
	private FtpConnect ftp = new FtpConnect();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edithost);

		initAttribute();



		txtHostname = (EditText)findViewById(R.id.txteditHost);
		txtPort = (EditText)findViewById(R.id.txteditPort);
		txtUserName = (EditText)findViewById(R.id.txteditUserName);
		txtPass = (EditText)findViewById(R.id.txteditPass);
//		txtLocal = (EditText)findViewById(R.id.txteditLocalDir);
//		txtRemote =(EditText)findViewById(R.id.txteditRemoteDir);

		Intent intent = this.getIntent();
		ftp =(FtpConnect)intent.getSerializableExtra("ftp");


		txtHostname.setText(ftp.getHostname());
		txtPort.setText(String.valueOf(ftp.getPort()));
		txtUserName.setText(ftp.getUsername());
		txtPass.setText(ftp.getPassword());
		txtLocal.setText(ftp.getLocaldir());
		txtRemote.setText(ftp.getRemotedir());
		//		if(ftp.getActivest()==1){
		//			rdActive.setChecked(true);
		//			rdInActive.setChecked(false);
		//		}else{
		//			rdActive.setChecked(false);
		//			rdInActive.setChecked(true);
		//		}

		bttSave.setOnClickListener(processBttUpdate);
		bttCancel.setOnClickListener(processBttCancel);
	}

	public void initAttribute(){
		language = getIntent().getExtras().getBundle(getLang());

		hostName = (TextView)findViewById(R.id.edithostName);
		hostName.setText(language.getString("hostname"));
		port = (TextView)findViewById(R.id.editPort);
		port.setText(language.getString("port"));
		username = (TextView)findViewById(R.id.editUsername);
		username.setText(language.getString("username"));
		password = (TextView)findViewById(R.id.editPass);
		password.setText(language.getString("password"));
//		localDir = (TextView)findViewById(R.id.editlocalDir);
//		localDir.setText(language.getString("localDir"));
//		remoteDir = (TextView)findViewById(R.id.editremoteDir);
//		remoteDir.setText(language.getString("remoteDir"));

		//		rdActive = (RadioButton)findViewById(R.id.radioButtonActive);
		//		rdActive.setText(language.getString("active"));
		//		rdInActive = (RadioButton)findViewById(R.id.radioButtonInActive);
		//		rdInActive.setText(language.getString("inactive"));

//		bttSave = (Button)findViewById(R.id.editbuttonSave);
//		bttSave.setText(language.getString("update"));
//		bttCancel = (Button)findViewById(R.id.editbuttoncancel);
//		bttCancel.setText(language.getString("cancel"));
	}

	public String getLang() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			lang = extras.getString("lang");
		}
		return lang;
	}

	public OnClickListener processBttUpdate = new OnClickListener() {

		@Override
		public void onClick(View v) {
			System.out.println("user name ++++ " + txtUserName.getText().toString());
			System.out.println("sdcard +++ " + txtLocal.getText().toString());

			//			ftp.setHostname(txtHostname.getText().toString());
			//			ftp.setPort(Integer.parseInt(txtPort.getText().toString()));
			//			ftp.setUsername(txtUserName.getText().toString());
			//			ftp.setPassword(txtPass.getText().toString());
			//			ftp.setLocaldir(txtLocal.getText().toString());
			//			ftp.setRemotedir(txtRemote.getText().toString());
			//			
			//			connectDAO.updateFtpConnect(ftp);

			editHost();

			Intent intent = new Intent(EditHostActivity.this,FtpClientActivity.class);
			intent.putExtra("lang", typeLang);
			startActivity(intent);
		}
	};

	public OnClickListener processBttCancel = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(EditHostActivity.this,FtpClientActivity.class);
			intent.putExtra("lang", typeLang);
			startActivity(intent);
		}
	};

	public FtpConnectDAO getConnectDAO() {
		return connectDAO;
	}

	public void setConnectDAO(FtpConnectDAO connectDAO) {
		this.connectDAO = connectDAO;
	}

	public FtpConnect getFtp() {
		return ftp;
	}

	public void setFtp(FtpConnect ftp) {
		this.ftp = ftp;
	}


	public void editHost(){
		try{
			
			
			File file = new File("/mnt/sdcard/ftpconnect.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			
//			String filepath = "/mnt/sdcard/ftp_test5.xml"; 
//			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance(); 
//			DocumentBuilder docBuilder = docFactory.newDocumentBuilder(); 
//			Document doc;
//
//			doc = docBuilder.parse(filepath);
			
			int v=0;
			NodeList listftpconnect= doc.getElementsByTagName("ftpconnect");
			   
			   for(int s=0; s<listftpconnect.getLength(); s++){
				   Node nodeftpconnect = listftpconnect.item(s);
				   
				   NodeList ftp1 = nodeftpconnect.getChildNodes();
				   for(int i=0; i<ftp1.getLength(); i++){
					   
					   Node node = ftp1.item(i);
					   //System.out.println(node.getNodeName());
					   if("ftpname".equals(node.getNodeName())){
						  
						   if(node.getTextContent().equals(ftp.getHostname())){
							   v=s;
							   System.out.println("vi tri :" + v);
						   }
					   }
				   }
			   }
			
			Node staff = doc.getElementsByTagName("ftpconnect").item(v); 
			NodeList list = staff.getChildNodes(); 
			for (int i =0; i<list.getLength();i++){ 
				Node node = list.item(i); 
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
			} 

			TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
			Transformer transformer = transformerFactory.newTransformer(); 
			DOMSource source = new DOMSource(doc); 
			StreamResult result = new StreamResult(file); 
			transformer.transform(source, result); 
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
