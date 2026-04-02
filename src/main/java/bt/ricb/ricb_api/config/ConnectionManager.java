package bt.ricb.ricb_api.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConnectionManager {

	public static final Connection getOracleConnection() throws Exception {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@192.168.0.101:1521:iims?oracle.jdbc.timezoneAsRegion=false", "RICB_UWR", "ricb");
//			        "jdbc:oracle:thin:@192.168.0.133:1521:iims?oracle.jdbc.timezoneAsRegion=false", "RICB_LI", "R1CB");

			System.out.println("connection established");


		} catch (ClassNotFoundException e) {

			System.out.println("Connection Failed. Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return connection;
		}

		System.out.println("Oracle JDBC Driver Registered successful!");
		return connection;
	}

	public static final Connection getLifeConnection() throws Exception {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@oracle.ricb.bt:1521:centralizedb?oracle.jdbc.timezoneAsRegion=false", "RICB_COM", "R1CBCOM123");
//					"jdbc:oracle:thin:@192.168.0.133:1521:iims?oracle.jdbc.timezoneAsRegion=false", "RICB_COM", "R1CB");

			System.out.println("connection established");
		} catch (ClassNotFoundException e) {

			System.out.println("Connection Failed. Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return connection;
		}

		System.out.println("Oracle JDBC Driver Registered successful!");
		return connection;
	}
	
	
	public static final Connection getAnnuityLapsedConnection() throws Exception {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@192.168.0.101:1521:IIMS?oracle.jdbc.timezoneAsRegion=false", "ricb_fas", "ricb");
			System.out.println("connection established");
		} catch (ClassNotFoundException e) {

			System.out.println("Connection Failed. Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return connection;
		}

		System.out.println("Oracle JDBC Driver Registered successful!");
		return connection;
	}
	
	public static final Connection getGeneralInsuranceConnection() throws Exception {
		Connection connection = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@oracle.ricb.bt:1521:centralizedb?oracle.jdbc.timezoneAsRegion=false", "RICB_GI", "R1CBGI123");
			System.out.println("connection established");
		} catch (ClassNotFoundException e) {

			System.out.println("Connection Failed. Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return connection;
		}

		System.out.println("Oracle JDBC Driver Registered successful!");
		return connection;
	}

	public static void close(Connection conn, ResultSet rs, PreparedStatement pst) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
		if (pst != null) {
			try {
				pst.close();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		if (conn != null)
			try {
				conn.close();
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
	}
}
