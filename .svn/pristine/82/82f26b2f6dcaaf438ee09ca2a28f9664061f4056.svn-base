package com.negengec.geotweetdownloader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DbOperations {

	public void runOperations(String sql, String hostUrl, String hostPort, String dbName, String dbUser,
			String dbPassword, String operationType) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection("jdbc:postgresql://"+hostUrl+":"+hostPort+"/"
					+ dbName, dbUser, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		// System.out.println("Opened database successfully");
		try {
			stmt = c.createStatement();
			if(operationType == "Insert"){
				stmt.executeUpdate(sql);
			}
			else if (operationType == "Select"){
				stmt.executeQuery(sql);
			} else if(operationType == "Create"){
				stmt.executeUpdate(sql);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Can not insert data!");
			e.printStackTrace();
		}
		try {
			c.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
