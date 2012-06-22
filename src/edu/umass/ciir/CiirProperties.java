package edu.umass.ciir;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class CiirProperties {
	
	public static Properties properties = new Properties();
	
	public static void load(String propertiesFile) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(propertiesFile);
		properties.load(fileInputStream);
		fileInputStream.close();
	}
	
	public static String getProperty(String propertyName) {
		String propVal = properties.getProperty(propertyName);
		if (propVal == null) {
			throw new IllegalStateException("Required property not specified, " + propertyName);
		}
		return propVal;
	}
	
	public static String getProperty(String propertyName, String defaultVal) {
		String propVal = properties.getProperty(propertyName);
		if (propVal == null) {
			return defaultVal;
		} 
		return propVal;
	}
	
	public static double getPropertyAsDouble(String propertyName, double defaultVal) {
		String propVal = properties.getProperty(propertyName);
		if (propVal == null) {
			return defaultVal;
		} 
		return Double.parseDouble(propVal);
	}
	
	public static int getPropertyAsInt(String propertyName, int defaultVal) {
		
		String propVal = properties.getProperty(propertyName);
		if (propVal == null) {
			return defaultVal;
		} else {
			return Integer.parseInt(properties.getProperty(propertyName));
		}
		
	}

	public static boolean getPropertyAsBoolean(String propertyName) {
		String propVal = properties.getProperty(propertyName);
		if (propVal == null) {
			throw new IllegalStateException("Required property not specified:" + propertyName);
		}
		return Boolean.parseBoolean(propVal);
	}
	
	public static boolean getPropertyAsBoolean(String propertyName, boolean defaultVal) {
		String propVal = properties.getProperty(propertyName);
		if (propVal == null) {
			return defaultVal;
		} else {
			return Boolean.parseBoolean(propVal);
		}
		
	}
	
	public static void printProperties() {
		String[] keySet = properties.keySet().toArray(new String[0]);
		Arrays.sort(keySet);
		for (String key : keySet) {
			String val = properties.getProperty(key);
			if (val.length() > 40) {
				val = val.substring(0, 37) + "...";
			}
			System.out.println(key + "=" + val);
		}
	}

}
