package com.eli.filemanager.pojo;

import android.graphics.drawable.Drawable;

public class Files {
	private Drawable icon;
	private String name;
	private boolean isFolder;
	
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}
	public boolean isFolder() {
		return isFolder;
	}
}
