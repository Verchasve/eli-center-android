package com.eli.filemanager.pojo;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Files{
	private Drawable icon;
	private String name;
	private Long size;
	private boolean isFolder = false;
	private Intent action;
	private String childFile;
	private Long modified;
	private String path;
	private BluetoothDevice bluetooth;
	
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

	public void setSize(Long size) {
		this.size = size;
	}

	public Long getSize() {
		return size;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

	public Long getModified() {
		return modified;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setBluetooth(BluetoothDevice bluetooth) {
		this.bluetooth = bluetooth;
	}

	public BluetoothDevice getBluetooth() {
		return bluetooth;
	}
}
