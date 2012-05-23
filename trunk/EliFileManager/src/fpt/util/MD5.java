package fpt.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	 private static BigInteger data;

	  /**
	   * Encrypt data using MD5.
	   *
	   * @param str String will be encrypted.
	   * @return String has been ecrypted.
	   * @throws NoSuchAlgorithmException
	   */
	  public static String encrypt(String str) {
	    try {
	      byte[] chars = str.getBytes();
	      MessageDigest m = MessageDigest.getInstance("MD5");
	      m.update(chars, 0, chars.length);
	      MD5.data = new BigInteger(1, m.digest());
	    }
	    catch(NoSuchAlgorithmException alg) {
	      alg.printStackTrace();
	    }

			// Return formatted string.
			return String.format("%1$032X", MD5.data);
		}
}
