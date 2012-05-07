package com.eli.util;

import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;

import com.eli.filemanager.pojo.Files;
import com.eli.filemanager.pojo.Users;

public class Util {
	
	public static final int EN = 0;
	public static final int KR = 1;
	public static final int VI = 2;
	public static Users users = null;
	public static ArrayList<Files> listHistory= new ArrayList<Files>();
	
	
	public static String locale(int key){
		String locale = "";
		switch (key) {
		case 0:
			locale = "";
			break;
		case 1:
			locale = "kr";		
			break;
		case 2:
			locale = "vi";
			break;
		default:
			break;
		}
		return locale;
	}
	
	// check type of file
	public static boolean checkExtendFile(String filename, String extend) {
		int number = extend.length();
		if (filename.length() > number) {
			if (filename.substring(filename.length() - number).toLowerCase()
					.equals(extend.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
