package edu.umass.ciir.crawling;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleContentCrawler {
	
	/**
	 * Downloads the content of the URL.
	 * 
	 * @param url
	 * @return content of downloaded URL or null if the URL could not be fetched.
	 */
	public String fetchUrl(String urlString) 
	throws Exception {
		
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestProperty("User-Agent", "Mozilla/Firefox");       
		conn.setRequestMethod("GET");
		int responseCode = conn.getResponseCode();
		if (HttpURLConnection.HTTP_OK != responseCode) {
		    return null;
		}

		StringBuilder text = new StringBuilder();
		BufferedReader br  = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
		try {
			while (br.ready()) {
				text.append(br.readLine());
			}
		} finally {
			br.close();
		}
		return text.toString();
	}

}
