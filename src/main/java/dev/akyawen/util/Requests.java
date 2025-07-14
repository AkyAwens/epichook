package dev.akyawen.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Requests {
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0";
	
	public String sendGet(String urlString) throws Exception {
	    URL url = new URL(urlString);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("GET");
	    con.setRequestProperty("User-Agent", USER_AGENT);
	
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
	        String inputLine;
	        StringBuilder response = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        return response.toString();
	    } finally {
	        con.disconnect();
	    }
	}
	
	public String sendPost(String urlString, Map<String, String> postParams) throws Exception {
	    URL url = new URL(urlString);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("POST");
	    con.setRequestProperty("User-Agent", USER_AGENT);
	    con.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    con.setDoOutput(true);
	
	    StringBuilder postData = new StringBuilder();
	    for (Map.Entry<String, String> param : postParams.entrySet()) {
	        if (postData.length() != 0) {
	            postData.append('&');
	        }
	        postData.append(param.getKey());
	        postData.append('=');
	        postData.append(param.getValue());
	    }
	    byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
	
	    try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
	        wr.write(postDataBytes);
	        wr.flush();
	    }
	
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
	        String inputLine;
	        StringBuilder response = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        return response.toString();
	    } finally {
	        con.disconnect();
	    }
	}
	
	public String sendDelete(String urlString) throws Exception {
	    URL url = new URL(urlString);
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("DELETE");
	    con.setRequestProperty("User-Agent", USER_AGENT);
	
	    try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
	        String inputLine;
	        StringBuilder response = new StringBuilder();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        return response.toString();
	    } finally {
	        con.disconnect();
	    }
	}
}

