package fpt.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;

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

import ftp.pojo.FtpConnect;

public class DeteleXMLFile {
	 public static void DeleteXMLFile(FtpConnect f) {
		   try{
			   System.out.println("deleting");
			   File file = new File("/mnt/sdcard/ftpconnect.xml");

				//Create instance of DocumentBuilderFactory
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

				//Get the DocumentBuilder
				DocumentBuilder docBuilder = factory.newDocumentBuilder();

				//Using existing XML Document
				Document doc = docBuilder.parse(file);
		   System.out.println(f.getHostname());
		   NodeList listftpconnect= doc.getElementsByTagName("ftpconnect");
		   for(int s=0; s<listftpconnect.getLength(); s++){
			   Node node = listftpconnect.item(s);
			   if("ftpname".equals(node.getNodeName())){
				   if(node.getTextContent()==f.getHostname())
					   node.getParentNode().removeChild(node);
			   }
		   }

//		   for (int i =0; i<list.getLength();i++){ 
//			   Node node = list.item(i); 
//			   // lấy phần tử salary và cập nhật giá trị cho nó. 
//			   if("username".equals(node.getNodeName())){
//				   node.setTextContent("111111"); 
//			   } 
//		   } // ghi nội dung ra file xml 
		   
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
