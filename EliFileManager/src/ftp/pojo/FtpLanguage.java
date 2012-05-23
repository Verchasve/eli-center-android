package ftp.pojo;

import java.io.Serializable;

public class FtpLanguage implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String idlang;
	private String langname;
	private int activest=1;
	private String activestname="";
	public String getIdlang() {
		return idlang;
	}
	public void setIdlang(String idlang) {
		this.idlang = idlang;
	}
	public String getLangname() {
		return langname;
	}
	public void setLangname(String langname) {
		this.langname = langname;
	}
	public int getActivest() {
		return activest;
	}
	public void setActivest(int activest) {
		this.activest = activest;
	}
	public String getActivestname() {
		return activestname;
	}
	public void setActivestname(String activestname) {
		this.activestname = activestname;
	}
	
	
	

}
