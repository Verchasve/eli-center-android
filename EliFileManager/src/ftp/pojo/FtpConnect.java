package ftp.pojo;

import java.io.Serializable;

public class FtpConnect implements Serializable{

	private int idftpconnect;
	private String hostname;
	private int idtype;
	private String typename;
	private String port;
	private String username;
	private String password;
	private String localdir;
	private String remotedir;
	private int activest=1;
	private String activestname;
	public int getIdftpconnect() {
		return idftpconnect;
	}
	public void setIdftpconnect(int idftpconnect) {
		this.idftpconnect = idftpconnect;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getIdtype() {
		return idtype;
	}
	public void setIdtype(int idtype) {
		this.idtype = idtype;
	}
	public String getTypename() {
		return typename;
	}
	public void setTypename(String typename) {
		this.typename = typename;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
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
	public String getLocaldir() {
		return localdir;
	}
	public void setLocaldir(String localdir) {
		this.localdir = localdir;
	}
	public String getRemotedir() {
		return remotedir;
	}
	public void setRemotedir(String remotedir) {
		this.remotedir = remotedir;
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
	
	@Override
	public String toString() {
		return this.hostname;
	}
}
