package com.eli.filemanager.pojo;

import java.util.Date;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Files implements Parcelable{
	private Drawable icon;
	private String name;
	private Long size;
	private boolean isFolder = false;
	private Intent action;
	private String childFile;
	private Date modified;
	
	public Files(){}
	
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
	public void setAction(Intent action) {
		this.action = action;
	}
	public Intent getAction() {
		return action;
	}

	public void setChildFile(String childFile) {
		this.childFile = childFile;
	}

	public String getChildFile() {
		return childFile;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
	    dest.writeString(name);
	    dest.writeString(childFile);
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getSize() {
		return size;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public Date getModified() {
		return modified;
	}
}
