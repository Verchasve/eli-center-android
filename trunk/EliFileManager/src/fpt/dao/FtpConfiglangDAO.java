package fpt.dao;

import java.sql.ResultSet;
import java.util.ArrayList;

import fpt.util.MySqlDataAccessHelper;
import ftp.pojo.FtpConfiglang;



public class FtpConfiglangDAO {

	public ArrayList<FtpConfiglang> allLang() {
		ArrayList<FtpConfiglang> result = new ArrayList<FtpConfiglang>();
		MySqlDataAccessHelper mysql = new MySqlDataAccessHelper();
		try {
			mysql.open();
			String sql = "select * from ftp_configlang";
			ResultSet rs = mysql.executeQuery(sql);

			String vn = "";
			String en = "";
			String kr = "";
			while (rs.next()) {
				String ID = rs.getString("IDKey");
				if (rs.getString("VN") != null)
					vn = rs.getString("VN");
				if (rs.getString("EN") != null)
					en = rs.getString("EN");
				if (rs.getString("KR") != null)
					kr = rs.getString("KR");
				FtpConfiglang l = new FtpConfiglang();
				l.setIdkey(ID);
				l.setEn(en);
				l.setVn(vn);
				l.setKr(kr);
				result.add(l);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mysql.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// result.toArray(new String[result.size()]);
		return result;
	}
}
