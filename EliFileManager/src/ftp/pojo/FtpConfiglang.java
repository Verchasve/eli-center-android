package ftp.pojo;

import java.io.Serializable;

public class FtpConfiglang implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String idkey;
	private String en;
	private String vn;
	private String kr;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIdkey() {
		return idkey;
	}
	public void setIdkey(String idkey) {
		this.idkey = idkey;
	}
	public String getEn() {
		return en;
	}
	public void setEn(String en) {
		this.en = en;
	}
	public String getVn() {
		return vn;
	}
	public void setVn(String vn) {
		this.vn = vn;
	}
	public String getKr() {
		return kr;
	}
	public void setKr(String kr) {
		this.kr = kr;
	}
	
	
}
