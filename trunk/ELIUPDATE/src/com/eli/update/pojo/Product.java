package com.eli.update.pojo;

public class Product {
	
	private String id;
	private String name;
	private String size;
	private String icon;
	private String version;
	private String link;
	private String description;
	
	public Product(String id,String name, String size,String icon, String version, String link,String description){
		this.id=id;
		this.name = name;
		this.size = size;
		this.icon=icon;
		this.version = version;
		this.description = description;
		this.link  = link;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}
}
