package com.loloara.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class MySQLConn {
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private final String URL = "jdbc:mysql://192.168.10.210:3306/mysql";
	private final String USERNAME = "root";
	private final String PASSWORD = "1q2w3e4r%T";
	
	private Connection conn = null;
	private Statement stmt = null;
	
	public MySQLConn() {
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			System.out.println("\nMySQL Connection");
			stmt = conn.createStatement();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getKeyword() {
		String sql = "select * from Keyword_History where status = 'y';";
		String keywords="";
		try {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				keywords += rs.getString("keyword") + " ";
			}
			keywords = keywords.trim();
			return keywords;
		}catch(SQLException e) {
			e.printStackTrace();
		}
		return "test";
	}
}
