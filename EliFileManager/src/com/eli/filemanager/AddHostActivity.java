package com.eli.filemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import android.widget.TextView;

public class AddHostActivity extends Activity{
	private TextView hostName,port,username,password,localDir,remoteDir;
	private EditText txtHostname,txtPort,txtUserName,txtPass,txtLocal,txtRemote;
	private Button bttSave,bttCancel;

	String typeLang = "EN";
	UtilInitLang utilang = new UtilInitLang();
	String lang = "EN";
	Bundle language;
	private FtpConnectDAO connectDAO = new FtpConnectDAO();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.addhost);

		initAttribute();

//		bttSave = (Button)findViewById(R.id.buttonSave);
//		bttCancel = (Button)findViewById(R.id.buttoncancel);

		txtHostname = (EditText)findViewById(R.id.txtAddHost);
		txtPort = (EditText)findViewById(R.id.txtAddPort);
		txtUserName = (EditText)findViewById(R.id.txtUserName);
		txtPass = (EditText)findViewById(R.id.txtPass);
//		txtLocal = (EditText)findViewById(R.id.txtLocalDir);
//		txtRemote =(EditText)findViewById(R.id.txtRemoteDir);

		bttSave.setOnClickListener(processBttSave);
		bttCancel.setOnClickListener(processBttCancel);
	}

	public void initAttribute(){
		language = getIntent().getExtras().getBundle(getLang());		
		hostName = (TextView)findViewById(R.id.addhostName);
		hostName.setText(language.getString("hostname"));
		port = (TextView)findViewById(R.id.aaPort);
		port.setText(language.getString("port"));
		username = (TextView)findViewById(R.id.addUsername);
		username.setText(language.getString("username"));
		password = (TextView)findViewById(R.id.addPass);
		password.setText(language.getString("password"));
//		localDir = (TextView)findViewById(R.id.localDir);
//		localDir.setText(language.getString("localDir"));
//		remoteDir = (TextView)findViewById(R.id.remoteDir);
//		remoteDir.setText(language.getString("remoteDir"));
//
//		bttSave = (Button)findViewById(R.id.buttonSave);
//		bttSave.setText(language.getString("save"));
//		bttCancel = (Button)findViewById(R.id.buttoncancel);
//		bttCancel.setText(language.getString("cancel"));
	}

	public String getLang() {
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			lang = extras.getString("lang");
		}
		return lang;
	}

	public OnClickListener processBttSave = new OnClickListener() {

		@Override
		public void onClick(View v) {
			//			FtpConnect ftpConnect = new FtpConnect();
			//			
			//			ftpConnect.setHostname(txtHostname.getText().toString());
			//			ftpConnect.setPort(Integer.parseInt(txtPort.getText().toString()));
			//			ftpConnect.setUsername(txtUserName.getText().toString());
			//			ftpConnect.setPassword(txtPass.getText().toString());
			//			ftpConnect.setLocaldir(txtLocal.getText().toString());
			//			ftpConnect.setRemotedir(txtRemote.getText().toString());

			//			connectDAO.insertFtpConnect(ftpConnect);
			writingXMLFile();
			Intent intent = new Intent(AddHostActivity.this,FtpClientActivity.class);
			intent.putExtra("lang", typeLang);
			startActivity(intent);
		}
	};

	public OnClickListener processBttCancel = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(AddHostActivity.this,FtpClientActivity.class);
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

	public  int writingXMLFile(){
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

			// set attribute to staff element
			//			Attr attr = doc.createAttribute("id");
			//			attr.setValue("1");
			//			staff.setAttributeNode(attr);

			// shorten way
			// staff.setAttribute("id", "1");

			// firstname elements
			Element firstname = doc.createElement("ftpname");
			firstname.appendChild(doc.createTextNode(txtHostname.getText().toString()));
			staff.appendChild(firstname);

			// lastname elements
			Element lastname = doc.createElement("port");
			lastname.appendChild(doc.createTextNode(txtPort.getText().toString()));
			staff.appendChild(lastname);

			// nickname elements
			Element nickname = doc.createElement("username");
			nickname.appendChild(doc.createTextNode(txtUserName.getText().toString()));
			staff.appendChild(nickname);

			// salary elements
			Element salary = doc.createElement("password");
			salary.appendChild(doc.createTextNode(txtPass.getText().toString()));
			staff.appendChild(salary);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			String fileName = "/mnt/sdcard/ftpconnect.xml";
			File file = new File(fileName);
			boolean success;
			try {
				success = file.createNewFile();
				StreamResult result = new StreamResult(file);
				if(success){
					transformer.transform(source, result);
				}else{
					modifyHost();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		return 1;
	}

	public void modifyHost(){
		try {
			File file = new File("/mnt/sdcard/ftpconnect.xml");

			//Create instance of DocumentBuilderFactory
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			//Get the DocumentBuilder
			DocumentBuilder docBuilder = factory.newDocumentBuilder();

			//Using existing XML Document
			Document doc = docBuilder.parse(file);

			//create the root element
			Element root = doc.getDocumentElement();

			//create child element
			Element rootElement = doc.getDocumentElement();
//			doc.appendChild(rootElement);

			// staff elements
			Element staff = doc.createElement("ftpconnect");
			rootElement.appendChild(staff);


			// firstname elements
			Element firstname = doc.createElement("ftpname");
			firstname.appendChild(doc.createTextNode(txtHostname.getText().toString()));
			staff.appendChild(firstname);

			// lastname elements
			Element lastname = doc.createElement("port");
			lastname.appendChild(doc.createTextNode(txtPort.getText().toString()));
			staff.appendChild(lastname);

			// nickname elements
			Element nickname = doc.createElement("username");
			nickname.appendChild(doc.createTextNode(txtUserName.getText().toString()));
			staff.appendChild(nickname);

			// salary elements
			Element salary = doc.createElement("password");
			salary.appendChild(doc.createTextNode(txtPass.getText().toString()));
			staff.appendChild(salary);

			//set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();

			//create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			String xmlString = sw.toString();

			OutputStream f0;
			byte buf[] = xmlString.getBytes();
			f0 = new FileOutputStream("/mnt/sdcard/ftpconnect.xml");
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
	}
}
