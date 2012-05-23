package fpt.dao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

import ftp.pojo.FtpConnect;
 
public class ModifyXMLFile {
   public static void ModifyXMLFile(FtpConnect f) {
	   try{
		   System.out.println("modifing");
	   String filepath = "/mnt/sdcard/ftpconnect.xml"; 
	   DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance(); 
	   DocumentBuilder docBuilder = docFactory.newDocumentBuilder(); 
	   Document doc;

		doc = docBuilder.parse(filepath);
	
	   
	  // Node company = doc.getFirstChild(); // Node staff = company.getFirstChild(); 
	  
	  // Element ftp=doc.createElement("staff");
	  // s.appendChild(doc.createTextNode("abc"));
	   //Node staff = doc.getElementsByTagName("root").item(0); 
	  // Element ftpconnect = doc.createElement("ftpconnect"); 
	   
	  
	  // Element ftpname=doc.createElement("ftpname");
	  // ftpname.appendChild(doc.createTextNode("elisoft.co.kr"));
	 //  ftpconnect.appendChild(ftpname); 
	  // staff.appendChild(ftpconnect);
	   Node staff = doc.getElementsByTagName("ftpconnects").item(0); 
	   
	   Element ftpconnect=doc.createElement("ftpconnect");
	   
	   Element ftpname=doc.createElement("ftpname");
	   ftpname.appendChild(doc.createTextNode("elisoft.co.kr"));
	   ftpconnect.appendChild(ftpname);
	   
	   Element port=doc.createElement("port");
	   port.appendChild(doc.createTextNode("21"));
	   ftpconnect.appendChild(port);
	   
	   Element username=doc.createElement("username");
	   username.appendChild(doc.createTextNode("elisoft"));
	   ftpconnect.appendChild(username);
	   
	   Element password=doc.createElement("password");
	   password.appendChild(doc.createTextNode("7890"));
	   ftpconnect.appendChild(password);
	  
	   staff.appendChild(ftpconnect);
//	   
//	   Node staff = doc.getElementsByTagName("ftpconnect").item(0); 
//	   NodeList list = staff.getChildNodes(); 
//	   
//	   
//	   
//	   for (int i =0; i<list.getLength();i++){ 
//		   Node node = list.item(i); 
//		   // lấy phần tử salary và cập nhật giá trị cho nó. 
//		   if("username".equals(node.getNodeName())){
//			   node.setTextContent("111111"); 
//		   } 
//	   } // ghi nội dung ra file xml 
	   
	   TransformerFactory transformerFactory = TransformerFactory.newInstance(); 
	   Transformer transformer = transformerFactory.newTransformer(); 
	   DOMSource source = new DOMSource(doc); 
	   StreamResult result = new StreamResult(new File(filepath)); 
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
