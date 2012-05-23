package fpt.dao;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
 
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.os.Environment;

public class CreateXMLFile {
	public static void CreateXMLFile() {
		 
		  try {
			 System.out.println("creating");
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			
			
			Element ftpconnects=doc.createElement("ftpconnects");
			
			
			
//			Element ftpconnect=doc.createElement("ftpconnect");
//			   
//		   Element ftpname=doc.createElement("ftpname");
//		   ftpname.appendChild(doc.createTextNode("elisoft.co.kr"));
//		   ftpconnect.appendChild(ftpname);
//		   
//		   Element port=doc.createElement("port");
//		   port.appendChild(doc.createTextNode("21"));
//		   ftpconnect.appendChild(port);
//		   
//		   Element username=doc.createElement("username");
//		   username.appendChild(doc.createTextNode("elisoft"));
//		   ftpconnect.appendChild(username);
//		   
//		   Element password=doc.createElement("password");
//		   password.appendChild(doc.createTextNode("7890"));
//		   ftpconnect.appendChild(password);
//		   
//		   ftpconnects.appendChild(ftpconnect);
		
			
			
			doc.appendChild(ftpconnects);
	 
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()+"/ftp_connect.xml"));
	 
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
	 
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
		}
}
