package fpt.util;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Properties;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;



public class Util implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// private static Locale local;
	// public UTF8Control utf8Control = new UTF8Control();
	MySqlDataAccessHelper mysqlHelper = new MySqlDataAccessHelper();
	private static boolean allReadLang;
	private static boolean readLang;
	
	private Hashtable<String, String> language = new Hashtable<String, String>();
	private Hashtable<String, Hashtable<String, String>> allLang = new Hashtable<String, Hashtable<String, String>>();

	public MySqlDataAccessHelper getMysqlHelper() {
		return mysqlHelper;
	}

	public void setMysqlHelper(MySqlDataAccessHelper mysqlHelper) {
		this.mysqlHelper = mysqlHelper;
	}

	public Util(boolean allReadlang, boolean readLang) {
		Util.allReadLang = allReadlang;
		Util.readLang = readLang;
	}

	public String getIDLang(String db) {
		String l="EN";
		if(db!=null){
			l=db;
		}
		return l;
	}

	public Hashtable<String, String> getLanguage(String db) {
		if (isReadLang()) {
			String lang = getIDLang(db);
			language = this.getAllLang().get(lang);
		}
		setReadLang(false);
		return language;
	}

	public void setLanguage(Hashtable<String, String> language) {
		this.language = language;
	}

	public Hashtable<String, Hashtable<String, String>> getAllLang() {
		if (isAllReadLang()) {
			try {
				String userDB = "erp";
				String passDB = "nguyen";
				String host = "14.63.219.99";

				Connection connection;

				DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());
				String connectionString = "jdbc:mysql://" + host
						+ ":3306/eliftpclient";
				Properties pros = new Properties();
				pros.setProperty("characterEncoding", "utf8");
				pros.setProperty("user", userDB);
				pros.setProperty("password", passDB);

				connection = DriverManager
						.getConnection(connectionString, pros);

				allLang.clear();

				String sql = "select * from ftp_configlang";
				Statement st = connection.createStatement();
				ResultSet rs = st.executeQuery(sql);
				ResultSetMetaData rsm = rs.getMetaData();
				int countLable = rsm.getColumnCount();
				for (int i = 1; i < countLable; i++) {
					allLang.put(rsm.getColumnLabel(i + 1),
							new Hashtable<String, String>());
				}

				while (rs.next()) {
					String ID = rs.getString("IDKey");
					for (int i = 1; i < countLable; i++) {
						allLang.get(rsm.getColumnLabel(i + 1)).put(ID,rs.getString(i + 1));
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		setAllReadLang(false);
		return allLang;
	}

	public void setAllLang(Hashtable<String, Hashtable<String, String>> allLang) {
		this.allLang = allLang;
	}

	public Hashtable<String, String> getDefaultLanguage() {
		String lang = "EN";
		Hashtable<String, Hashtable<String, String>> temp = getAllLang();
		return temp.get(lang);
	}

	// private ResourceBundle getResourceBundleAllLang() {
	// local = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	// resourceBundle = ResourceBundle.getBundle("lang", local, utf8Control);
	// return resourceBundle;
	// }

	public static boolean isReadLang() {
		return readLang;
	}

	public static boolean isAllReadLang() {
		return allReadLang;
	}

	public static void setAllReadLang(boolean allReadLang) {
		Util.allReadLang = allReadLang;
	}

	public static void setReadLang(boolean readLang) {
		Util.readLang = readLang;
	}
	
	

	public static void main(String[] args) throws Exception {
		// Util ut = new Util();
		// //System.out.prln(ut.getLanguage().get("NewDocument"));
		// //System.out.prln("#{initLang.language.get(\"NewDocument\")}");
	}

}
