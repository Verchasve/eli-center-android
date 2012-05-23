package fpt.util;

import java.io.Serializable;
import java.util.Hashtable;

public class UtilInitLang implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Util util = new Util(true, true);
	private String welcometo;
	private String copyright;
	private static String db="EN";
	
	
	private Hashtable<String, String> langs;

	public UtilInitLang() {
	}
	
	public static String getDb() {
		return db;
	}

	public static void setDb(String db) {
		UtilInitLang.db = db;
	}

	public String getCopyright() {
		copyright=getLangs().get("copyright");
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getWelcometo() {
		welcometo=getLangs().get("welcometo");
		return welcometo;
	}

	public void setWelcometo(String welcometo) {
		this.welcometo = welcometo;
	}
	
	public Hashtable<String, String> getLangs() {

		langs = new Hashtable<String, String>();
		langs = util.getLanguage(this.db);
		return langs;
	}

	public void setLangs(Hashtable<String, String> langs) {
		this.langs = langs;
	}
	
	// TODO
	private String welcome;
	private String logout;
	private String username;
	private String password;
	private String login;
	private String rploginfail;
	private String upload;
	private String newfolder;
	private String download;
	private String foldername;
	private String filename;
	private String filesize;
	private String remove;
	private String addfile;
	private String filetotal;
	private String sizetotal;
	private String cancel;
	private String begin;
	private String rpuploadfail;
	private String rpdownloadfail;
	private String rememberpass;
	private String connect;
	private String add;
	private String edit;
	private String delete;
	private String hostname;
	private String port;
	private String localDir;
	private String remoteDir;
	private String save;
	private String update;
	private String active;
	private String inactive;
	private String titledelete;
	private String deleteerror;
	private String ok;
	private String addhost;
	private String edithost;
	private String report;
	private String folderexit;
	private String overwritefile;
	private String warning;
	private String notchoosedownload;
	private String notchooseupload;
	private String checkall;
	private String fileloading;
	private String rename;
	private String detail;
	private String choose;
	private String filetype;
	private String lastmodify;
	private String properties;
	private String areyousuredeletefile;
	private String areyousuredeletefolder;
	private String folder;
	private String file;
	private String copy;
	private String paste;
	private String appfound;
	private String appnotfound;
	private String isempty;
	private String versionname;
	private String installversioncode;
	private String currenversion;
	private String youareusingversion;
	private String nownewversion;
	private String doyouwantinstall;
	private String deleteall;
	private String notcheckdelete;	
	private String warningDialogUpload;
	private String warningsamefile;
	private String warningsamefolder;
	private String questioncopy;
	private String copycontaint;
	private String existfile;
	private String existfolder;
	private String fullsize;
	private String checkpass;
	private String folderempty;
	private String waiting;
	private String nextgo;

	public Util getUtil() {
		return util;
	}

	public void setUtil(Util util) {
		this.util = util;
	}

	public String getWelcome() {
		welcome = getLangs().get("welcome");
		return welcome;
	}

	public void setWelcome(String welcome) {
		this.welcome = welcome;
	}

	public String getLogout() {
		logout = getLangs().get("logout");
		return logout;
	}

	public void setLogout(String logout) {
		this.logout = logout;
	}

	public String getUsername() {
		username = getLangs().get("username");
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		password = getLangs().get("password");
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLogin() {
		login = getLangs().get("login");
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getRploginfail() {
		rploginfail = getLangs().get("rploginfail");
		return rploginfail;
	}

	public void setRploginfail(String rploginfail) {
		this.rploginfail = rploginfail;
	}

	public String getUpload() {
		upload = getLangs().get("upload");
		return upload;
	}

	public void setUpload(String upload) {
		this.upload = upload;
	}

	public String getNewfolder() {
		newfolder = getLangs().get("newfolder");
		return newfolder;
	}

	public void setNewfolder(String newfolder) {
		this.newfolder = newfolder;
	}

	public String getDownload() {
		download = getLangs().get("download");
		return download;
	}

	public void setDownload(String download) {
		this.download = download;
	}

	public String getFoldername() {
		foldername = getLangs().get("foldername");
		return foldername;
	}

	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}

	public String getFilename() {
		filename = getLangs().get("filename");
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilesize() {
		filesize = getLangs().get("filesize");
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	public String getRemove() {
		remove = getLangs().get("remove");
		return remove;
	}

	public void setRemove(String remove) {
		this.remove = remove;
	}

	public String getAddfile() {
		addfile = getLangs().get("addfile");
		return addfile;
	}

	public void setAddfile(String addfile) {
		this.addfile = addfile;
	}

	public String getFiletotal() {
		filetotal = getLangs().get("filetotal");
		return filetotal;
	}

	public void setFiletotal(String filetotal) {
		this.filetotal = filetotal;
	}

	public String getSizetotal() {
		sizetotal = getLangs().get("sizetotal");
		return sizetotal;
	}

	public void setSizetotal(String sizetotal) {
		this.sizetotal = sizetotal;
	}

	public String getCancel() {
		cancel = getLangs().get("cancel");
		return cancel;
	}

	public void setCancel(String cancel) {
		this.cancel = cancel;
	}

	public String getBegin() {
		begin = getLangs().get("begin");
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public String getRpuploadfail() {
		rpuploadfail = getLangs().get("rpuploadfail");
		return rpuploadfail;
	}

	public void setRpuploadfail(String rpuploadfail) {
		this.rpuploadfail = rpuploadfail;
	}

	public String getRpdownloadfail() {
		rpdownloadfail = getLangs().get("rpdownloadfail");
		return rpdownloadfail;
	}

	public void setRpdownloadfail(String rpdownloadfail) {
		this.rpdownloadfail = rpdownloadfail;
	}

	public String getRememberpass() {
		rememberpass = getLangs().get("rememberpass");
		return rememberpass;
	}

	public void setRememberpass(String rememberpass) {
		this.rememberpass = rememberpass;
	}

	public String getConnect() {
		connect = getLangs().get("connect");
		return connect;
	}

	public void setConnect(String connect) {
		this.connect = connect;
	}

	public String getAdd() {
		add = getLangs().get("add");
		return add;
	}

	public void setAdd(String add) {
		this.add = add;
	}

	public String getEdit() {
		edit = getLangs().get("edit");
		return edit;
	}

	public void setEdit(String edit) {
		this.edit = edit;
	}

	public String getDelete() {
		delete = getLangs().get("delete");
		return delete;
	}

	public void setDelete(String delete) {
		this.delete = delete;
	}

	public String getHostname() {
		hostname = getLangs().get("hostname");
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		port = getLangs().get("port");
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getLocalDir() {
		localDir = getLangs().get("localDir");
		return localDir;
	}

	public void setLocalDir(String localDir) {
		this.localDir = localDir;
	}

	public String getRemoteDir() {
		remoteDir = getLangs().get("remoteDir");
		return remoteDir;
	}

	public void setRemoteDir(String remoteDir) {
		this.remoteDir = remoteDir;
	}

	public String getSave() {
		save = getLangs().get("save");
		return save;
	}

	public void setSave(String save) {
		this.save = save;
	}

	public String getUpdate() {
		update = getLangs().get("update");
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getActive() {
		active = getLangs().get("active");
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getInactive() {
		inactive = getLangs().get("inactive");
		return inactive;
	}

	public void setInactive(String inactive) {
		this.inactive = inactive;
	}

	public String getTitledelete() {
		titledelete = getLangs().get("titledelete");
		return titledelete;
	}

	public void setTitledelete(String titledelete) {
		this.titledelete = titledelete;
	}

	public String getDeleteerror() {
		deleteerror = getLangs().get("deleteerror");
		return deleteerror;
	}

	public void setDeleteerror(String deleteerror) {
		this.deleteerror = deleteerror;
	}

	public void setOk(String ok) {
		this.ok = ok;
	}

	public String getOk() {
		ok = getLangs().get("ok");
		return ok;
	}

	public String getAddhost() {
		addhost = getLangs().get("addhost");
		return addhost;
	}

	public void setAddhost(String addhost) {
		this.addhost = addhost;
	}

	public String getEdithost() {
		edithost = getLangs().get("edithost");
		return edithost;
	}

	public void setEdithost(String edithost) {
		this.edithost = edithost;
	}

	public String getReport() {
		report = getLangs().get("report");
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getFolderexit() {
		folderexit = getLangs().get("folderexit");
		return folderexit;
	}

	public void setFolderexit(String folderexit) {
		this.folderexit = folderexit;
	}

	public String getOverwritefile() {
		overwritefile = getLangs().get("overwritefile");
		return overwritefile;
	}

	public void setOverwritefile(String overwritefile) {
		this.overwritefile = overwritefile;
	}

	public String getWarning() {
		warning = getLangs().get("warning");
		return warning;
	}

	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getNotchoosedownload() {
		notchoosedownload = getLangs().get("notchoosedownload");
		return notchoosedownload;
	}

	public void setNotchoosedownload(String notchoosedownload) {
		this.notchoosedownload = notchoosedownload;
	}

	public String getNotchooseupload() {
		notchooseupload = getLangs().get("notchooseupload");
		return notchooseupload;
	}

	public void setNotchooseupload(String notchooseupload) {
		this.notchooseupload = notchooseupload;
	}

	public String getCheckall() {
		checkall = getLangs().get("checkall");
		return checkall;
	}

	public void setCheckall(String checkall) {
		this.checkall = checkall;
	}

	public String getFileloading() {
		fileloading = getLangs().get("fileloading");
		return fileloading;
	}

	public void setFileloading(String fileloading) {
		this.fileloading = fileloading;
	}

	public String getRename() {
		rename = getLangs().get("rename");
		return rename;
	}

	public void setRename(String rename) {
		this.rename = rename;
	}

	public String getDetail() {
		detail = getLangs().get("detail");
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getChoose() {
		choose = getLangs().get("choose");
		return choose;
	}

	public void setChoose(String choose) {
		this.choose = choose;
	}

	public String getFiletype() {
		filetype = getLangs().get("filetype");
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getLastmodify() {
		lastmodify = getLangs().get("lastmodify");
		return lastmodify;
	}

	public void setLastmodify(String lastmodify) {
		this.lastmodify = lastmodify;
	}

	public String getProperties() {
		properties = getLangs().get("properties");
		return properties;
	}

	public void setProperties(String properties) {
		this.properties = properties;
	}

	public String getAreyousuredeletefile() {
		areyousuredeletefile = getLangs().get("areyousuredeletefile");
		return areyousuredeletefile;
	}

	public void setAreyousuredeletefile(String areyousuredeletefile) {
		this.areyousuredeletefile = areyousuredeletefile;
	}

	public String getAreyousuredeletefolder() {
		areyousuredeletefolder = getLangs().get("areyousuredeletefolder");
		return areyousuredeletefolder;
	}

	public void setAreyousuredeletefolder(String areyousuredeletefolder) {
		this.areyousuredeletefolder = areyousuredeletefolder;
	}

	public String getFolder() {
		folder = getLangs().get("folder");
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getFile() {
		file = getLangs().get("file");
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getCopy() {
		copy = getLangs().get("copy");
		return copy;
	}

	public void setCopy(String copy) {
		this.copy = copy;
	}

	public String getPaste() {
		paste = getLangs().get("filepaste");
		return paste;
	}

	public void setPaste(String paste) {
		this.paste = paste;
	}

	public String getAppfound() {
		appfound = getLangs().get("appfound");
		return appfound;
	}

	public void setAppfound(String appfound) {
		this.appfound = appfound;
	}

	public String getAppnotfound() {
		appnotfound = getLangs().get("appnotfound");
		return appnotfound;
	}

	public void setAppnotfound(String appnotfound) {
		this.appnotfound = appnotfound;
	}

	public String getIsempty() {
		isempty = getLangs().get("isempty");
		return isempty;
	}

	public void setIsempty(String isempty) {
		this.isempty = isempty;
	}

	public String getVersionname() {
		versionname = getLangs().get("versionname");
		return versionname;
	}

	public void setVersionname(String versionname) {
		this.versionname = versionname;
	}

	public String getInstallversioncode() {
		installversioncode = getLangs().get("installversioncode");
		return installversioncode;
	}

	public void setInstallversioncode(String installversioncode) {
		this.installversioncode = installversioncode;
	}

	public String getCurrenversion() {
		currenversion = getLangs().get("currenversion");
		return currenversion;
	}

	public void setCurrenversion(String currenversion) {
		this.currenversion = currenversion;
	}

	public String getYouareusingversion() {
		youareusingversion = getLangs().get("youareusingversion");
		return youareusingversion;
	}

	public void setYouareusingversion(String youareusingversion) {
		this.youareusingversion = youareusingversion;
	}

	public String getNownewversion() {
		nownewversion = getLangs().get("nownewversion");
		return nownewversion;
	}

	public void setNownewversion(String nownewversion) {
		this.nownewversion = nownewversion;
	}

	public String getDoyouwantinstall() {
		doyouwantinstall = getLangs().get("doyouwantinstall");
		return doyouwantinstall;
	}

	public void setDoyouwantinstall(String doyouwantinstall) {
		this.doyouwantinstall = doyouwantinstall;
	}


	public String getDeleteall() {
		deleteall = getLangs().get("deleteall");
		return deleteall;
	}

	public void setDeleteall(String deleteall) {
		this.deleteall = deleteall;
	}

	public String getNotcheckdelete() {
		notcheckdelete = getLangs().get("notcheckdelete");
		return notcheckdelete;
	}

	public void setNotcheckdelete(String notcheckdelete) {
		this.notcheckdelete = notcheckdelete;
	}


	public void setWarningDialogUpload(String warningDialogUpload) {
		this.warningDialogUpload = warningDialogUpload;
	}

	public String getWarningDialogUpload() {
		warningDialogUpload = getLangs().get("warningDialogUpload");
		return warningDialogUpload;
	}

	public void setWarningsamefile(String warningsamefile) {
		this.warningsamefile = warningsamefile;
	}

	public String getWarningsamefile() {
		warningsamefile = getLangs().get("warningsamefile");
		return warningsamefile;
	}

	public void setWarningsamefolder(String warningsamefolder) {
		this.warningsamefolder = warningsamefolder;
	}

	public String getWarningsamefolder() {
		warningsamefolder = getLangs().get("warningsamefolder");
		return warningsamefolder;
	}

	public void setQuestioncopy(String questioncopy) {
		this.questioncopy = questioncopy;
	}

	public String getQuestioncopy() {
		questioncopy = getLangs().get("questioncopy");
		return questioncopy;
	}


	public String getCopycontaint() {
		copycontaint = getLangs().get("copycontaint");
		return copycontaint;
	}

	public void setExistfile(String existfile) {
		this.existfile = existfile;
	}

	public void setCopycontaint(String copycontaint) {
		this.copycontaint = copycontaint;
	}

	public String getExistfile() {
		existfile = getLangs().get("existfile");
		return existfile;
	}

	public void setExistfolder(String existfolder) {
		this.existfolder = existfolder;
	}

	public String getExistfolder() {
		existfolder = getLangs().get("existfolder");
		return existfolder;
	}

	public String getFullsize() {
		fullsize = getLangs().get("fullsize");
		return fullsize;
	}

	public void setFullsize(String fullsize) {
		this.fullsize = fullsize;
	}

	public String getCheckpass() {
		checkpass = getLangs().get("checkpass");
		return checkpass;
	}

	public void setCheckpass(String checkpass) {
		this.checkpass = checkpass;
	}

	public String getFolderempty() {
		folderempty = getLangs().get("folderempty");
		return folderempty;
	}

	public void setFolderempty(String folderempty) {
		this.folderempty = folderempty;
	}

	public String getWaiting() {
		waiting = getLangs().get("waiting");
		return waiting;
	}

	public void setWaiting(String waiting) {
		this.waiting = waiting;
	}

	public String getNextgo() {
		nextgo = getLangs().get("nextgo");
		return nextgo;
	}

	public void setNextgo(String nextgo) {
		this.nextgo = nextgo;
	}

	
	
	

	
}
