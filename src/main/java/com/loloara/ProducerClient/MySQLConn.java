package com.loloara.ProducerClient;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
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
		SimpleDateFormat sdf= new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.US);
		String sql1 = "select keyword, seq, sinceDate from Keyword_History where status = 'y';";
		String[] keywords = new String[4];
		keywords[0] = "test";
		keywords[1] = "1";
		keywords[2] = "0";
		keywords[3] = "0";
		try {
			ResultSet rs1 = stmt.executeQuery(sql1);
			rs1.next();
			keywords[0] = rs1.getString("keyword");
			keywords[1] = Integer.toString(rs1.getInt("seq"));
			keywords[3] = sdf.format(rs1.getTimestamp("sinceDate"));
			rs1.close();
		
			String sql2 = "select keyword, sinceId from Keyword_History where keyword = '" + keywords[0] + "';";
			ResultSet rs2 = stmt.executeQuery(sql2);
			while(rs2.next()) {
				long pre = Long.parseLong(keywords[2]);
				long com = rs2.getLong("sinceId");
				if(pre < com)
					keywords[2] = Long.toString(com);
			}
			rs2.close();
			
		}catch(SQLException e) {
			e.printStackTrace();
		}

		return keywords;
	}
	
	
	public void updateKeywordHistory(int count, int seq, long sinceId, Date sinceDate, Date latelyDate) {
		StringBuilder sb = new StringBuilder();
		sb.append("update KCC_LAB.Keyword_History set tweets = tweets + ")
				.append(count)
				.append(", sinceId = ")
				.append(sinceId);
		if(sinceDate != null) {
			java.sql.Timestamp sinceObj = new java.sql.Timestamp(sinceDate.getTime());
			sb.append(", sinceDate = '")
			.append(sinceObj)
			.append("'");
		}
		
		if(latelyDate != null) {
			java.sql.Timestamp latelyObj = new java.sql.Timestamp(latelyDate.getTime());
			sb.append(", latelyDate = '")
			.append(latelyObj)
			.append("'");
		}
		String sql = sb.append(" WHERE  seq=")
		.append(seq)
		.append(";").toString();
		
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("updateKeywordHistory");
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
