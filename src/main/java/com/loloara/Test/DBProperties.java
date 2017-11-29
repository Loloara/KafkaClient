package com.loloara.Test;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class DBProperties {
	private Properties prop;
	private OutputStream output = null;
	private InputStream input = null;
	
	public DBProperties() {
		prop = new Properties();
	}
	
	public void setProperties() {
		try {
			output = new FileOutputStream("./config.properties");
			
			prop.setProperty("URL", "xxxx");
			prop.setProperty("USERNAME", "xxxx");
			prop.setProperty("PASSWORD", "xxxx");
			
			prop.store(output, null);
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			if(output != null) {
				try {
					output.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public Properties getProperties() {
		try {
			input = new FileInputStream("./config.properties");
			prop.load(input);
			
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			if(input != null) {
				try {
					input.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return prop;
	}
}
