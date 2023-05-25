package edu.upenn.cis.cis455.m2.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.HttpParsing;

//import edu.upenn.cis.cis455.m2.interfaces.*;

public class ResponseClass2 extends edu.upenn.cis.cis455.m2.interfaces.Response {
	final static Logger logger = LogManager.getLogger(ResponseClass2.class);
	
	public HashMap<String, String> headers = new HashMap<>();
	ArrayList<CookieClass> cookies = new ArrayList<>();
	public byte[] entireResponseInBytes;
	
	public ResponseClass2(byte[] entireResponseInBytes) {
		this.entireResponseInBytes = entireResponseInBytes;
	}
	
	@Override
	public void header(String header, String value) {
		if (!headers.containsKey(header))
			headers.put(header, value);
	}

	@Override
	public void redirect(String location) {
		header("Location", location);
		header("responseCode", "302");
	}

	@Override
	public void redirect(String location, int httpStatusCode) {
		header("Location", location);
		header("responseCode", String.valueOf(httpStatusCode));
	}

	@Override
	public void cookie(String name, String value) {
		cookies.add(new CookieClass(name, value));
	}

	@Override
	public void cookie(String name, String value, int maxAge) {
		CookieClass cookie = new CookieClass(name, value);
		cookie.setMaxAge(maxAge);
		cookies.add(cookie);		
	}

	@Override
	public void cookie(String name, String value, int maxAge, boolean secured) {
		CookieClass cookie = new CookieClass(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setSecured(secured);
		cookies.add(cookie);
	}

	@Override
	public void cookie(String name, String value, int maxAge, boolean secured, boolean httpOnly) {
		CookieClass cookie = new CookieClass(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setSecured(secured);
		cookie.setHttpOnly(httpOnly);
		cookies.add(cookie);
	}

	@Override
	public void cookie(String path, String name, String value) {
		CookieClass cookie = new CookieClass(name, value);
		cookie.setPath(path);
		cookies.add(cookie);
	}

	@Override
	public void cookie(String path, String name, String value, int maxAge) {
		CookieClass cookie = new CookieClass(name, value);
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		cookies.add(cookie);
	}

	@Override
	public void cookie(String path, String name, String value, int maxAge, boolean secured) {
		CookieClass cookie = new CookieClass(name, value);
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		cookie.setSecured(secured);
		cookies.add(cookie);
	}

	@Override
	public void cookie(String path, String name, String value, int maxAge, boolean secured, boolean httpOnly) {
		CookieClass cookie = new CookieClass(name, value);
		cookie.setPath(path);
		cookie.setMaxAge(maxAge);
		cookie.setSecured(secured);
		cookie.setHttpOnly(httpOnly);
		cookies.add(cookie);
	}

	@Override
	public void removeCookie(String name) {
		for (int i = 0 ; i < cookies.size() ; i++) {
			if (cookies.get(i).getName().equals(name)) {
				cookies.remove(i);
				i = -1; 
			}
		}
	}

	@Override
	public void removeCookie(String path, String name) {
		for (int i = 0 ; i < cookies.size() ; i++) {
			if (cookies.get(i).getName().equals(name) && WebService.matches(path, cookies.get(i).getPath())) {
				cookies.remove(i);
				i = -1; 
			}
		}
	}

	@Override
	public String getHeaders() {
		String responseHeader = headers.get("protocolVersion") + " " + headers.get("responseCode") + "\r\n";
		responseHeader += "content-type: " + headers.get("content-type") + "\r\n";
		responseHeader += "content-length: " + headers.get("content-length") + "\r\n";
		
		if (headers.containsKey("Location")) // Redirect
			responseHeader += "Location: " + headers.get("Location") + "\r\n";
		
		for (CookieClass cookie : cookies) {
			responseHeader += "Set-Cookie: " + cookie.getName() + "=" + cookie.getValue();
			if (cookie.getMaxAge() != -1)
				responseHeader += "; " +  "Max-Age=" + cookie.getMaxAge();
			if (cookie.getPath() != "")
				responseHeader += "; " +  "Path=" + cookie.getPath();
			if (cookie.getSecured() == true)
				responseHeader += "; " + "Secure";
			if (cookie.getHttpOnly() == true)
				responseHeader += "; " + "HttpOnly";
			responseHeader += "\r\n";
		}
		responseHeader += "\r\n";
		return responseHeader;
	}
	

}
