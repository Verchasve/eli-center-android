package fpt.util;

import java.io.File;

public class FileUtil {
	public static File[] getFilesOfDirectory(String dirPath) {
		File f = new File(dirPath);
		File[] fl=null;
		if(f!=null){
			fl = f.listFiles();
		}
		return fl;
	}
}
