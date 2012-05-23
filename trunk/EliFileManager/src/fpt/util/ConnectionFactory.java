package fpt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;


public class ConnectionFactory {
	private static ConnectionFactory connectionFactory = new ConnectionFactory();

	String userDB;
	String passDB;
	String host;

	private ConnectionFactory() {
	};

	public static ConnectionFactory getDefaultFactory() {
		if (connectionFactory == null) {
			connectionFactory = new ConnectionFactory();
		}
		return connectionFactory;
	}

	public Connection createConnection() {
		Connection connection = null;

		try {
			String database = "eliftpclient";
			DriverManager.registerDriver(new org.gjt.mm.mysql.Driver());
			String connectionString = "jdbc:mysql://" + host + ":3306/"
					+ database;
			Properties pros = new Properties();
			pros.setProperty("characterEncoding", "utf8");
			pros.setProperty("user", userDB);
			pros.setProperty("password", passDB);
			connection = DriverManager.getConnection(connectionString, pros);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return connection;
	}
}
