package com.eli.filemanager.pojo;

public class Users {
	private int id;
	private int background;
	private int display;
	private int language = 0;
	public int getBackground() {
		return background;
	}
	public void setBackground(int background) {
		this.background = background;
	}
	public int getDisplay() {
		return display;
	}
	public void setDisplay(int display) {
		this.display = display;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setLanguage(int language) {
		this.language = language;
	}
	public int getLanguage() {
		return language;
	}
}
