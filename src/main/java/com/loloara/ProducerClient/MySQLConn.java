package com.loloara.ProducerClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MySQLConn {
	private final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private String URL = "";
	private String USERNAME = "";
	private String PASSWORD = "";
	
	private Connection conn = null;
	private Statement stmt = null;
	
	public MySQLConn() {
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			input = new FileInputStream("./config.properties");
			prop.load(input);
			URL = prop.getProperty("URL");
			USERNAME = prop.getProperty("USERNAME");
			PASSWORD = prop.getProperty("PASSWORD");
			
		}catch(IOException e) {
			e.printStackTrace();
		}
		
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
			
			rs.close();
			stmt.close();
			conn.close();

			return keywords;
		}catch(SQLException e) {
			e.printStackTrace();
		}finally{
			
		}
		return "test";
	}
}
