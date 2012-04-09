package bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.w3c.dom.*;

import pojo.ListItem;

@ManagedBean (name="getData")
@SessionScoped
public class GetData {

	private ArrayList<ListItem> listItem = new ArrayList<ListItem>();
	private ListItem item = new ListItem();
	private String action = "";
	private UploadedFile fileUpload;

	public void onload() {
		getAllData();
	}

	public void getAllData() {
		listItem.clear();
		try {
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/data/auto-update/");
			path += File.separator + "listitem.xml";
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("item");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					ListItem item = new ListItem();

					item.setId(getTagValue("id", eElement));
					item.setName(getTagValue("name", eElement));
					item.setPackageName(getTagValue("package", eElement));
					item.setSize(getTagValue("size", eElement));
					item.setIcon(getTagValue("icon", eElement));
					item.setVersion(getTagValue("version", eElement));
					item.setVersion_code(getTagValue("version-code", eElement));
					item.setLink(getTagValue("link", eElement));
					item.setDescription(getTagValue("description", eElement));
					item.setLastUpdate(getTagValue("lastupdate", eElement));

					listItem.add(item);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}
	
	public void select(ActionEvent evt) {
		item = (ListItem) evt.getComponent().getAttributes().get("item");
		action = "update";
	}
	
	public void create() {
		item = new ListItem();
		action = "create";
	}

	public void update() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
		Date date = new Date();
		item.setLastUpdate(dateFormat.format(date));
		for (int i = 0; i < listItem.size(); i++) {
			if(listItem.get(i).getId().equals(item.getId())){
				listItem.remove(i);
				listItem.add(item);
			}
		}

		writeData();

		action = "";
		item = new ListItem();
	}

	private void writeData() {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("product");
			doc.appendChild(rootElement);

			for (ListItem item : listItem) {
				// item elements
				Element itemE = doc.createElement("item");
				rootElement.appendChild(itemE);

				// id elements
				Element idE = doc.createElement("id");
				idE.appendChild(doc.createTextNode(item.getId()));
				itemE.appendChild(idE);
				
				// name elements
				Element nameE = doc.createElement("name");
				nameE.appendChild(doc.createTextNode(item.getName()));
				itemE.appendChild(nameE);
				
				// package elements
				Element packageE = doc.createElement("package");
				packageE.appendChild(doc.createTextNode(item.getPackageName()));
				itemE.appendChild(packageE);

				// size elements
				Element sizeE = doc.createElement("size");
				sizeE.appendChild(doc.createTextNode(item.getSize()));
				itemE.appendChild(sizeE);

				// icon elements
				Element iconE = doc.createElement("icon");
				iconE.appendChild(doc.createTextNode(item.getIcon()));
				itemE.appendChild(iconE);

				// version elements
				Element versionE = doc.createElement("version");
				versionE.appendChild(doc.createTextNode(item.getVersion()));
				itemE.appendChild(versionE);
				
				// version-code elements
				Element version_codeE = doc.createElement("version-code");
				version_codeE.appendChild(doc.createTextNode(item.getVersion_code()));
				itemE.appendChild(version_codeE);
				
				// link elements
				Element linkE = doc.createElement("link");
				linkE.appendChild(doc.createTextNode(item.getLink()));
				itemE.appendChild(linkE);
				
				// description elements
				Element desE = doc.createElement("description");
				desE.appendChild(doc.createTextNode(item.getDescription()));
				itemE.appendChild(desE);
				
				// lastupdate elements
				Element lastupdateE = doc.createElement("lastupdate");
				lastupdateE.appendChild(doc.createTextNode(item.getLastUpdate()));
				itemE.appendChild(lastupdateE);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/data/auto-update/");
			path += File.separator + "listitem.xml";
			StreamResult result = new StreamResult(new File(path));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}
	
	public void handleFileUpload(FileUploadEvent event) {
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/data/app/");
		File result = new File(path + File.separator + event.getFile().getFileName());

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(result);
            long size = event.getFile().getSize();
			byte [] buffer = new byte[(int)size];

            int bulk;
            InputStream inputStream = event.getFile().getInputstream();
            while (true) {
                bulk = inputStream.read(buffer);
                if (bulk < 0) {
                    break;
                }
                fileOutputStream.write(buffer, 0, bulk);
                fileOutputStream.flush();
            }

            fileOutputStream.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            FacesMessage error = new FacesMessage("The files were not uploaded!");
            FacesContext.getCurrentInstance().addMessage(null, error);
        }
    } 
	
	public void uploadFile() throws IOException {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
		Date date = new Date();
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/data/app/");
		FileOutputStream fos = null;
		InputStream stream = null;
		if(fileUpload == null){
			System.out.println("File is null");
			return;
		}
		System.out.println("File name: " + fileUpload.getFileName());
		try{
			stream = fileUpload.getInputstream();
			long size = fileUpload.getSize();
			byte [] buffer = new byte[(int)size];
			stream.read(buffer, 0, (int)size);
			fos = new FileOutputStream(path + File.separator + fileUpload.getFileName());
			String link = createLinkURL()+"/data/app/";
			item.setLink(link + fileUpload.getFileName());
			fos.write(buffer);
		}catch (Exception e) {
			e.printStackTrace(System.out);
			if(fos != null){
				fos.close();
			}
			if(stream != null){
				stream.close();
			}
		}
		item.setLastUpdate(dateFormat.format(date));
		if(action.equals("update")){
			for (int i = 0; i < listItem.size(); i++) {
				if(listItem.get(i).getId().equals(item.getId())){
					listItem.remove(i);
					listItem.add(item);
				}
			}
		}else if(action.equals("create")){
			item.setId(new Date().getTime()+"");
			item.setIcon("...");
			listItem.add(item);
		}

		writeData();
	}
	
	public String createLinkURL() {
		String url = "";
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		url = request.getRequestURL().toString();
		return url;
	}

	public void setListItem(ArrayList<ListItem> listItem) {
		this.listItem = listItem;
	}

	public ArrayList<ListItem> getListItem() {
		return listItem;
	}

	public void setItem(ListItem item) {
		this.item = item;
	}

	public ListItem getItem() {
		return item;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setFileUpload(UploadedFile fileUpload) {
		this.fileUpload = fileUpload;
	}

	public UploadedFile getFileUpload() {
		return fileUpload;
	} 
}
