package ftp.pojo;

import java.io.Serializable;

public class FtpUser implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int iduser;
	private String username;
	private String password;
	private int idrole;
	private String rolename="";
	private int activest=1;
	private String activestname="";
	
	
	public int getIduser() {
		return iduser;
	}
	public void setIduser(int iduser) {
		this.iduser = iduser;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getIdrole() {
		return idrole;
	}
	public void setIdrole(int idrole) {
		this.idrole = idrole;
	}
	public String getRolename() {
		return rolename;
	}
	public void setRolename(String rolename) {
		this.rolename = rolename;
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
