package fpt.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

public class FTPControl {
	
	public FTPClient mFTPClient = null;
	
	// connect ftp
	public static String getSystemKey(String sysName){
		String [] values= sysName.split(" ");
		if(values != null && values.length>0)
			return values[0];
		else
			return null;
	}
	public boolean ftpConnect(String host, String username,
            String password, int port)
	{
		try {
			mFTPClient = new FTPClient();
			// connecting to the host
			mFTPClient.connect(host, port);
			
			// now check the reply code, if positive mean connection success
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
				// login using username & password
				boolean status = mFTPClient.login(username, password);
				
				/* Set File Transfer Mode
				*
				* To avoid corruption issue you must specified a correct
				* transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
				* EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE
				* for transferring text, image, and compressed files.
				*/
//				FTPClientConfig config = new FTPClientConfig(getSystemKey(mFTPClient.getSystemName()));
				
//				mFTPClient.configure(config);
				
				mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
				mFTPClient.enterLocalPassiveMode();
				
				return status;
			}
		} catch(Exception e) {
			Log.d("Error", "Error: could not connect to host " + host );
		}
		
		return false;
	}
	
	// disconnect ftp
	public boolean ftpDisconnect()
	{
	    try {
	        mFTPClient.logout();
	        mFTPClient.disconnect();
	        return true;
	    } catch (Exception e) {
	        Log.d("Error", "Error occurred while disconnecting from ftp server.");
	    }

	    return false;
	}
	
	// get current working directory:
	public String ftpGetCurrentWorkingDirectory()
	{
	    try {
	        String workingDir = mFTPClient.printWorkingDirectory();
	        return workingDir;
	    } catch(Exception e) {
	        Log.d("Error", "Error: could not get current working directory.");
	    }

	    return null;
	}
	// change working directory:
	public boolean ftpChangeDirectory(String directory_path)
	{
	    try {
	        mFTPClient.changeWorkingDirectory(directory_path);
	    } catch(Exception e) {
	        Log.d("Error", "Error: could not change directory to " + directory_path);
	    }

	    return false;
	}
	// list all files in a directory:
	public void ftpPrintFilesList()
	{
	    try {
	        FTPFile[] ftpFiles = mFTPClient.listFiles();
	        int length = ftpFiles.length;

	        for (int i = 0; i < length; i++) {
	            String name = ftpFiles[i].getName();
	            boolean isFile = ftpFiles[i].isFile();

	            if (isFile) {
	                Log.i("Error", "File : " + name);
	            }
	            else {
	                Log.i("Error", "Directory : " + name);
	            }
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	//create new directory:
	public boolean ftpMakeDirectory(String new_dir_path)
	{
	    try {
	        boolean status = mFTPClient.makeDirectory(new_dir_path);
	        return status;
	    } catch(Exception e) {
	        Log.d("Error", "Error: could not create new directory named " + new_dir_path);
	    }

	 return false;
	}
	// delete/remove a directory:
	public boolean ftpRemoveDirectory(String dir_path)
	{
	    try {
	        boolean status = mFTPClient.removeDirectory(dir_path);
	        return status;
	    } catch(Exception e) {
	        Log.d("Error", "Error: could not remove directory named " + dir_path);
	    }

	    return false;
	}
	//delete a file:
	public boolean ftpRemoveFile(String filePath)
	{
	    try {
	        boolean status = mFTPClient.deleteFile(filePath);
	        return status;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return false;
	}
	// rename a file:
	public boolean ftpRenameFile(String from, String to)
	{
	    try {
	        boolean status = mFTPClient.rename(from, to);
	        return status;
	    } catch (Exception e) {
	        Log.d("Error", "Could not rename file: " + from + " to: " + to);
	    }

	    return false;
	}
	
	// download
	public boolean ftpDownload(String srcFilePath, String desFilePath)
	{
	    boolean status = false;
	    try {
	        FileOutputStream desFileStream = new FileOutputStream(desFilePath);;
	        status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
	        desFileStream.close();

	        return status;
	    } catch (Exception e) {
	        Log.d("Error", "download failed");
	    }

	    return status;
	}
	
	public boolean ftpUpload(String srcFilePath, String desFileName,
            String desDirectory)
	{
		boolean status = false;
		try {
			FileInputStream srcFileStream = new FileInputStream(srcFilePath);
			
			// change working directory to the destination directory
			if (ftpChangeDirectory(desDirectory)) {
				status = mFTPClient.storeFile(desFileName, srcFileStream);
			}
			
			srcFileStream.close();
			return status;
		} catch (Exception e) {
			Log.d("Error", "upload failed");
		}
		
		return status;
	}
	
	public List<String> getListFile() {
		List<String> lst = new ArrayList<String>();
		try {
			String[] names = mFTPClient.listNames();
			if (names != null) {
				for (String name : names) {
					lst.add(name);
				}
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}
		return lst;
	}
	
}
