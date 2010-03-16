package edu.umass.ciir;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CiirProperties {
	
	public static Properties properties = new Properties();
	
	public static void load(String propertiesFile) throws IOException {
		properties.load(new FileInputStream(propertiesFile));
	}
	
	public static String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}
	public static int getPropertyAsInt(String propertyName) {
		return Integer.parseInt(properties.getProperty(propertyName));
	}

}
