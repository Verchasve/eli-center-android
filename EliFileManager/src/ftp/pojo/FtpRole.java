package ftp.pojo;

import java.io.Serializable;

public class FtpRole implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int idrole;
	private String rolename="";
	private String rolenameen;
	private String rolenamevn;
	private String rolenamekr;
	private int activest=1;
	private String activestname="";
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
	public String getRolenameen() {
		return rolenameen;
	}
	public void setRolenameen(String rolenameen) {
		this.rolenameen = rolenameen;
	}
	public String getRolenamevn() {
		return rolenamevn;
	}
	public void setRolenamevn(String rolenamevn) {
		this.rolenamevn = rolenamevn;
	}
	public String getRolenamekr() {
		return rolenamekr;
	}
	public void setRolenamekr(String rolenamekr) {
		this.rolenamekr = rolenamekr;
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
