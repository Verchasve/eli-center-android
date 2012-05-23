package fpt.dao;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

import android.os.Environment;

import ftp.pojo.FtpConnect;

public class ReadXMLFile{

    public static List<FtpConnect> ReadXML () {
    	List<FtpConnect> lstConnect = new ArrayList<FtpConnect>();
    	try {
    		 System.out.println("reading");
    		Document doc =null;
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            try{
            	doc = docBuilder.parse (new File(Environment.getExternalStorageDirectory()+"/ftp_connect.xml"));
            	doc.getDocumentElement ().normalize ();
                System.out.println ("Root element of the doc is " + 
                     doc.getDocumentElement().getNodeName());


                NodeList listOfPersons = doc.getElementsByTagName("ftpconnect");
                int totalPersons = listOfPersons.getLength();
                System.out.println("Total no of ftp connect : " + totalPersons);

                for(int s=0; s<listOfPersons.getLength() ; s++){


                    Node ftpconnects = listOfPersons.item(s);
                    if(ftpconnects.getNodeType() == Node.ELEMENT_NODE){

                    	FtpConnect f = new FtpConnect();
                        Element ftp = (Element)ftpconnects;
                        NodeList ftpnameList = ftp.getElementsByTagName("ftpname");
                        Element ftpnameElement = (Element)ftpnameList.item(0);
                        NodeList textFNList = ftpnameElement.getChildNodes();
                        System.out.println("ftpname : " + ((Node)textFNList.item(0)).getNodeValue().trim());
                        f.setHostname(((Node)textFNList.item(0)).getNodeValue());
                        //-------
                        NodeList portList = ftp.getElementsByTagName("port");
                        Element portElement = (Element)portList.item(0);
                        NodeList textPList = portElement.getChildNodes();
                        System.out.println("port : " + ((Node)textPList.item(0)).getNodeValue().trim());
                        f.setPort(((Node)textPList.item(0)).getNodeValue());
                        //----
                        NodeList usernameList = ftp.getElementsByTagName("username");
                        Element usernameElement = (Element)usernameList.item(0);
                        NodeList textusernameList = usernameElement.getChildNodes();
                        System.out.println("Username : " +((Node)textusernameList.item(0)).getNodeValue().trim());
                        f.setUsername(((Node)textusernameList.item(0)).getNodeValue());
                        
                        NodeList pwList = ftp.getElementsByTagName("password");
                        Element pwElement = (Element)pwList.item(0);
                        NodeList textPWList = pwElement.getChildNodes();
                        System.out.println("PW : " + ((Node)textPWList.item(0)).getNodeValue().trim());
                        //------
                        f.setPassword(((Node)textPWList.item(0)).getNodeValue());
                        lstConnect.add(f);
                    }//end of if clause


                }//end of for loop with s var
            }catch(FileNotFoundException err){
            	 System.out.println ("ko tim thay " );
            	 CreateXMLFile.CreateXMLFile();
            }
            // normalize text representation
            


        }catch (SAXParseException err) {
        	
        	System.out.println ("** Parsing error" + ", line "+ err.getLineNumber () + ", uri " + err.getSystemId ());
        	System.out.println(" " + err.getMessage ());
        }catch (SAXException e) {
        	
        	Exception x = e.getException ();
        	((x == null) ? e : x).printStackTrace ();

        }catch (Throwable t) {
        
        	t.printStackTrace ();
        }
        //System.exit (0);
        return lstConnect;
    }//end of main
    

}