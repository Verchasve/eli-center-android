package fpt.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import android.app.AlertDialog;
import android.os.Environment;
import android.util.Log;

import fpt.dao.FtpConnectDAO;
import ftp.pojo.FtpUser;

@SuppressWarnings("unused")
public class FTPUtil {
	public static String HOST = "www.elisoft.co.kr";
	public static int PORT = 21;
	public static String USER = "elisoft";
	public static String PASS = "7890";

	static String USERNAME = "";
	static String UPLOAD_FOLDER = "upload";
	static String DOWNLOAD_FOLDER = "dowload";
	static String MY_FILES = "myfiles";
	static FTPClient client = new FTPClient();

	public static void ftpDownloadFile(String fileRemotePath, String filePathSrc) {
		try {

			boolean download = client.retrieveFile(fileRemotePath,
					new FileOutputStream(filePathSrc));
			if (download) {
				//System.out.prln("File download...");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// try {
			// client.logout();
			// client.disconnect();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
		}
	}

	public static void downloadOK(String pathSave, List<FTPFile> names,String temp) {
		
		connects();
		//System.out.println("pathSave: " + pathSave);
		//System.out.println("temp: " + temp);
		for (FTPFile name : names) {
			System.out.println("name: " + name.getName());
			
			testsOK(name, pathSave,temp);
		}
		closeConnection();
		// //System.out.prln("mkdir: "+Environment.getExternalStorageDirectory());
		// File file=new
		// File(Environment.getExternalStorageDirectory()+"/luan");
		// file.mkdir();

	}
	
	public static boolean download(String pathSave, List<FTPFile> names,String temp) {
		boolean kq=true;
		connects();
		for (FTPFile name : names) {
			kq=tests(name, pathSave,temp);
			if(kq==false){
				break;
			}
		}
		closeConnection();
		return kq;
	}

	public static void testsOK(FTPFile fileRemotePath, String pathSave,String ftpPath) {

		try {
			
			String tempRemode=ftpPath+"/"+fileRemotePath.getName();
			String tempAndroid=pathSave +"/"+ fileRemotePath.getName();
			System.out.println("tempRemode: " + tempRemode);
			System.out.println("tempAndroid: " + tempAndroid);
			if (!fileRemotePath.isFile()) {
				File file = new File(tempAndroid);
				
				file.mkdir();
				FTPFile[] ftpFiles = client.listFiles(tempRemode);
				if (ftpFiles != null) {
					for (int i = 0; i < ftpFiles.length; i++) {
						testsOK(ftpFiles[i], tempAndroid, tempRemode);
					}
				}
			} else {
				client.retrieveFile(tempRemode, new FileOutputStream(tempAndroid));		
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

	}
	
	public static boolean tests(FTPFile fileRemotePath, String pathSave,String ftpPath) {
		boolean kq=true;
		try {
			//System.out.prln("workdirec: " + client.printWorkingDirectory());
			String tempRemode=ftpPath+"/"+fileRemotePath.getName();
			String tempAndroid=pathSave +"/"+ fileRemotePath.getName();
			if (!fileRemotePath.isFile()) {
				
				//System.out.prln("mkdir: " + tempAndroid);
				File file = new File(tempAndroid);
				if(file.isDirectory()){
					kq=false;					
				}else{
					file.mkdir();
					FTPFile[] ftpFiles = client.listFiles(tempRemode);
					if (ftpFiles != null) {
						for (int i = 0; i < ftpFiles.length; i++) {
							if(tests(ftpFiles[i], tempAndroid, tempRemode)==false){
								break;
							}
						}
					}
				}
				
				
			} else {
				//System.out.prln(" file: " + fileRemotePath);
				File f=new File(tempAndroid);
				if(f.isFile()){
					kq=false;
				}else{
					client.retrieveFile(tempRemode, new FileOutputStream(tempAndroid));
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}

		return kq;
	}

	public static boolean uploadFile(File fileSrc, String pathDes, String temp) {
		boolean kq=true;
		try {

			if (!fileSrc.isFile()) {
				temp += fileSrc.getName();
				//System.out.prln("directory: " + temp);
				//client.makeDirectory(pathDes + temp);
				String s=pathDes + temp;
				
				
				if(client.changeWorkingDirectory(pathDes + temp)){
					kq=false;
				}else{
					client.makeDirectory(pathDes + temp);
					File[] files = fileSrc.listFiles();
					if (files != null) {
						for (int i = 0; i < files.length; i++) {
							if(uploadFile(files[i], pathDes, temp + "/")==false){
								break;
							}
						}
					}
					
				}
				

			} else {
				temp += fileSrc.getName();
				int fg=0;

				FTPFile[] lsf= FTPUtil.getListFile(pathDes);
				if(lsf!=null && lsf.length>0){
					
					for(int k=0;k<lsf.length;k++){
						if(lsf[k].isFile() && lsf[k].getName().equals(fileSrc.getName())){
							fg=1;
							break;
						}
					}
				}
				
				if(fg==1)
					kq=false;
				else
					client.storeFile(pathDes + temp, new FileInputStream(fileSrc));
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return kq;
	}
	
	public static void uploadFileOK(File fileSrc, String pathDes, String temp) {
		try {

			if (!fileSrc.isFile()) {
				temp += fileSrc.getName();
				
				System.out.println("temp: " + temp);
				if(pathDes.equals("/"))
					pathDes="";
				
				client.makeDirectory(pathDes + temp);
				
				File[] files = fileSrc.listFiles();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						
						uploadFileOK(files[i], pathDes, temp + "/");
					}
				}

			} else {
				temp += fileSrc.getName();
				//System.out.prln("file: " + temp);
				client.storeFile(pathDes + temp, new FileInputStream(fileSrc));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public static boolean uploadFTP(List<File> filesSrc, String pathDes) {
		boolean kq=true;
		connects();
		for (int i = 0; i < filesSrc.size(); i++) {
			kq=uploadFile(filesSrc.get(i), pathDes, "/");
			if(kq==false){
				break;
			}
		}
		closeConnection();
		return kq;
	}
	
	public static void uploadFTPOK(List<File> filesSrc, String pathDes) {
		connects();
		for (int i = 0; i < filesSrc.size(); i++) {
			uploadFileOK(filesSrc.get(i), pathDes, "/");
		}
		closeConnection();
	}

	public static void ftpCreateFolder(String fileRemotePath) {
		connects();
		try {
			if (!client.changeWorkingDirectory(fileRemotePath)) {
				//System.out.prln("not exists");
				client.makeDirectory(fileRemotePath);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			closeConnection();
		
		}
	}

	public static void ftpDeleteFolder(String fileRemotePath) {
		try {
			connects();
			if (client.changeWorkingDirectory(fileRemotePath)) {
				//System.out.prln(" exists");
				String[] names = client.listNames();
				for (String name : names) {
					if (client.changeWorkingDirectory(name)) {
						System.out.println("chay vo day as");
						client.removeDirectory(fileRemotePath);
						System.out.println("duoc khong the");
						ftpDeleteFolder("/" + name);
					} else {
						System.out.println("vo day a");
						client.deleteFile(name);
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			 try {
			 client.logout();
			 client.disconnect();
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
		}
	}

	public static void ftpUploadFile(String filePathDes, String filePathSrc) {

		try {
			client.storeFile(filePathDes, new FileInputStream(filePathSrc));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// try {
			// client.logout();
			// client.disconnect();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

		}
	}

	public static void ftpDeleteFile(String deletePath) {
		try {
			connects();
			System.out.println("den day chua");
			boolean deleted = client.deleteFile(deletePath);
			if (deleted) {
				System.out.println("File deleted...");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			 try {
			 client.logout();
			 client.disconnect();
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
		}
	}

	public static boolean connects() {
		boolean rs = false;
		try {
			client.connect(HOST, PORT);
			client.setAutodetectUTF8(true);
		//	client.setControlEncoding("euckr");
			rs = client.login(USER, PASS);
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static boolean connects(String h, String us, String pass, int port) {
		boolean rs = false;
		try {
			client.connect(h, port);
			rs = client.login(us, pass);
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.enterLocalPassiveMode();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	public static void closeConnection() {
		try {
			client.logout();
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int checkFile(String path) {
		connects();
		int kq = 0;
		try {
			if (client.changeWorkingDirectory(path))
				kq = 1;

		} catch (IOException e) {

		}
		closeConnection();
		return kq;
	}

	public static FTPFile[] getListFile(String path) {
		connects();
		//List<String> lst = new ArrayList<String>();
		FTPFile[] ftpFiles=null;
		try {
			//System.out.println("charset: "+client.setControlEncoding(encoding));
			client.setControlEncoding("UTF8");
			ftpFiles = client.listFiles(path);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
		return ftpFiles;
	}

	// public static void main(String[] args) {
	// getListFile();
	// ftpUploadFile("/luan/luan1/luan/luan.txt", uploadPathSrc+"test.txt");
	// ftpDownloadFile("luan1.txt", downloadPathSrc+"luan1.txt");
	// ftpDeleteFile("/luan");
	// ftpCreateFolder("/luan/luan1/luan");
	// ftpDeleteFolder("/luan");

	// }
	
	public static boolean sdcardCreateFolder(String fileRemotePath) {
//		File folder = new File(Environment.getExternalStorageDirectory() + "/"+fileRemotePath);
		File folder = new File(fileRemotePath);
		boolean success = false;
		if(!folder.exists())
		{
		    success = folder.mkdir();
		}  
		if(success==false){
			System.out.println("ko tao dc");
			
		}else{
			System.out.println("tao dc");
		}
		return success;
	}
	
	
	public static void renameFile(String beforeFileName,String afterFileName) {
		try {
			connects();
			System.out.println("before file name +++++ " + beforeFileName);
			System.out.println("After file name +++++ " + afterFileName);
			boolean success = client.rename(beforeFileName, afterFileName);
			if(success){
				System.out.println("File have been renamed");
			}

		} catch (IOException e) {
			Log.d("FtpClient", "Could not rename " + beforeFileName+ "to " + afterFileName);
			e.printStackTrace();
		} finally {
			 try {
			 client.logout();
			 client.disconnect();
			 } catch (Exception e) {
			 e.printStackTrace();
			 }
		}
	}
	
	// rename folder
	public static void renameFolder(String beforeFolder,String afterfolder,String path){
		try{
			connects();
			System.out.println("before folder name +++= " + beforeFolder);
			System.out.println("after folder name +++== " + afterfolder);
			String[] names = client.listNames();
				System.out.println("thu muc ++++ " + client.changeWorkingDirectory(path));
				if(client.changeWorkingDirectory(path)){
					client.rename(beforeFolder, afterfolder);
			}
		}catch (Exception e) {
			Log.d("FtpClient", "Could not rename " + beforeFolder+ "to " + afterfolder);
			e.printStackTrace();
		}finally {
			 try {
				 client.logout();
				 client.disconnect();
				 } catch (Exception e) {
				 e.printStackTrace();
				 }
			}
	}
	
//	public static void deletFolder(String nameFile,String path){
//		try{
//			connects();
//			System.out.println("thu muc delete :::: " + client.changeWorkingDirectory(path));
//			System.out.println("name file ::; " + nameFile);
//			String[] names = client.listNames();
//			if(client.changeWorkingDirectory(path)){
//				if(!client.changeWorkingDirectory(path)){
//					client.deleteFile(nameFile);
//				}else{
//					for(String name:names){
//						client.deleteFile(nameFile);
//						deletFolder(nameFile, path);
//					}
//					
//					client.removeDirectory(nameFile);
//				}
//			}else{
//				client.deleteFile(nameFile);
//			}
//		}catch (Exception e) {
//			Log.d("FtpClient", "Could not delete " + nameFile);
//			e.printStackTrace();
//		}finally {
//			 try {
//				 client.logout();
//				 client.disconnect();
//				 } catch (Exception e) {
//				 e.printStackTrace();
//				 }
//			}
//	}
	
	
//	public static void deletFolder(FTPFile ftpFile,String path){
//		try{
//			connects();
//			String pathcha= path+"/" + ftpFile.getName();
//			System.out.println(" path parent +++ " + pathcha);
//			if(client.changeWorkingDirectory(pathcha)){
//				FTPFile[] ftpFiles = client.listFiles(pathcha);
//				if(ftpFiles!=null && ftpFiles.length>0){
//					for (int i = 0; i < ftpFiles.length; i++) {
//						if(ftpFiles[i].isDirectory()){
////							client.deleteFile(pathcha);
////							client.removeDirectory(pathcha);
//							deletFolder(ftpFiles[i], pathcha);
//						}else{
//							client.deleteFile(pathcha);
//						}
//					}
//					ftpFiles = client.listFiles(pathcha);
//					if(ftpFiles==null || ftpFiles.length<=0){
//						client.removeDirectory(pathcha);
//					}
//				}else
//					client.removeDirectory(pathcha);
//			}else{
//				client.deleteFile(pathcha);
//			}
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}finally {
//			 try {
////				 client.logout();
//				 client.disconnect();
//				 } catch (Exception e) {
//				 e.printStackTrace();
//				 }
//			}
//	}
	
	public static void deletFolder(FTPFile ftpFile,String path){
		try{
			connects();
			dodelete(ftpFile,path);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			closeConnection();
		}
	}
	
	public static void dodelete(FTPFile ftpFile,String path) throws IOException{
		String pathcha="";
		if(path.equals("/")){
			pathcha= path+ ftpFile.getName();
		}else
			pathcha= path+"/" + ftpFile.getName();
		
		System.out.println("pathcha: "+pathcha);
		
		FTPFile[] ftpFiles = client.listFiles(pathcha);
		if(ftpFile.isDirectory()){
			
			if(ftpFiles!=null && ftpFiles.length>0){
				for (int i = 0; i < ftpFiles.length; i++) {
					dodelete(ftpFiles[i], pathcha);
				}
				client.removeDirectory(pathcha);
				System.out.println("delete thu muc: "+ftpFile.getName());
			}else{
				if(client.removeDirectory(pathcha)){
					System.out.println("delete thu muc rong: "+ftpFile.getName());
				}else
					System.out.println("loi delete thu muc rong");
			}
			
		}else{
			client.deleteFile(pathcha);
			System.out.println("delete : "+ftpFile.getName());
		}
	}
	
	public static long totalSize(FTPFile ftpFile,String path){
		long result = 0;
		
		try {
			connects();
			String pathcha= path+"/" + ftpFile.getName();
			FTPFile[] ftpFiles = client.listFiles(pathcha);
			for (int i = 0; i < ftpFiles.length; i++) {
				if(ftpFiles[i].isDirectory()){
					result += totalSize(ftpFiles[i], pathcha );
				}else{
					result += ftpFiles[i].getSize();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			 try {
//				 client.logout();
				 client.disconnect();
				 } catch (Exception e) {
				 e.printStackTrace();
				 }
			}
		return result;
	}
}
