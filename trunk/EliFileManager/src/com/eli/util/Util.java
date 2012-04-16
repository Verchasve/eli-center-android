package com.eli.util;

public class Util {

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
