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
	
	public String[] getKeyword() {
		String sql1 = "select * from Keyword_History where status = 'y';";
		String sql2 = "select keyword, seq, sinceId from Keyword_History order by seq desc limit 1000;";
		String[] keywords = new String[3];
		keywords[0] = "test";
		keywords[1] = "1";
		keywords[2] = "0";
		try {
			ResultSet rs1 = stmt.executeQuery(sql1);
			rs1.next();
			keywords[0] = rs1.getString("keyword");
			keywords[1] = Integer.toString(rs1.getInt("seq"));
			rs1.close();
			
			ResultSet rs2 = stmt.executeQuery(sql2);
			while(rs2.next()) {
				if(keywords[0].equals(rs2.getString("keyword"))) {
					keywords[2] = Long.toString(rs2.getLong("sinceId"));
					break;
				}
			}
			rs2.close();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}

		return keywords;
	}
	
	
	public void addCountSinceIdToKeyword(int count, int seq, long sinceId) {
		StringBuilder sb = new StringBuilder();
		String sql = sb.append("update KCC_LAB.Keyword_History set tweets = tweets + ")
				.append(count)
				.append(", sinceId = ")
				.append(sinceId)
				.append(" WHERE  seq=")
				.append(seq)
				.append(";").toString();
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
