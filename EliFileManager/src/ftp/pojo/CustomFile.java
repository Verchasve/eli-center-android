package ftp.pojo;

import java.io.File;
import java.io.Serializable;

public class CustomFile{
	private File file;
	private boolean check = false;
	
	

	public CustomFile(File file) {
		super();
		this.file = file;
	}

	public CustomFile(File file, boolean check) {
		super();
		this.file = file;
		this.check = check;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}
	
	

}
