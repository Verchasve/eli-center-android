package fpt.util;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MySqlDataAccessHelper implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Connection connection;
	String userDB="erp";
	String passDB="nguyen";
	String host="14.63.219.99";
	
//	String userDB="root";
//	String passDB="123";
//	String host="192.168.1.5";

	public Connection getConnection() {
		return connection;
	}

	public void open() {
		try {
			String database = "eliftpclient";
			DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());
			String connectionString = "jdbc:mysql://" + host + ":3306/"
					+ database +"?useUnicode=true&amp;characterEncoding=UTF-8";
			Properties pros = new Properties();
			pros.setProperty("characterEncoding", "utf8");
			pros.setProperty("user", userDB);
			pros.setProperty("password", passDB);

			this.connection = DriverManager.getConnection(connectionString,
					pros);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void open(String databasename) {
		try {
			DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());
			String connectionString = "jdbc:mysql://" + host + ":3306/"
					+ databasename+"?useUnicode=true&amp;characterEncoding=UTF-8";
			Properties pros = new Properties();
			pros.setProperty("characterEncoding", "utf8");
			pros.setProperty("user", userDB);
			pros.setProperty("password", passDB);

			this.connection = DriverManager.getConnection(connectionString,
					pros);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void open(String databasename, String userDb, String passDb,
			String hosts) {
		try {
			userDB = userDb;
			passDB = passDb;
			host = hosts;

			DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());
			String connectionString = "jdbc:mysql://" + host + ":3306/"
					+ databasename;
			Properties pros = new Properties();
			pros.setProperty("characterEncoding", "utf8");
			pros.setProperty("user", userDB);
			pros.setProperty("password", passDB);

			this.connection = DriverManager.getConnection(connectionString,
					pros);

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void close() {
		try {
			this.connection.close();
		} catch (SQLException ex) {
			Logger.getLogger(MySqlDataAccessHelper.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	public ResultSet executeQuery(String sql) {
		ResultSet rs = null;
		try {

			Statement sm = this.connection.createStatement();
			rs = sm.executeQuery(sql);
		} catch (SQLException ex) {
			//System.out.prln(ex.getMessage());
		}
		return rs;
	}

	public int executeUpdate(String sql) {
		int num = -1;
		try {
			Statement sm = this.connection.createStatement();
			num = sm.executeUpdate(sql);
		} catch (SQLException ex) {
//			ex.printStackTrace();
		}
		return num;
	}

	public void callProcedure(String sql) {
		try {
			CallableStatement stm = this.getConnection().prepareCall(sql);
			stm.execute();
		} catch (Exception e) {

		}
	}

	public String callProcedureWithData(String sql, int IDApp) {
		String ID = "";
		try {
			CallableStatement stm = this.connection.prepareCall(sql);
			stm.setInt(1, IDApp);
			ResultSet rss = stm.executeQuery();
			if (rss.next()) {
				ID = rss.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ID;
	}
}
