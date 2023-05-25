package edu.upenn.cis.cis455;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.upenn.cis.cis455.m1.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.WebService;

public class RequestClass extends Request{
	
	public Map<String, String> headers;
	Socket sock;
	
	public RequestClass(Map<String, String> headers, Socket sock) {
		this.headers = headers;
		this.sock = sock;
	}
	
	
	@Override
	public String requestMethod() {
		return headers.get("Method");
	}

	@Override
	public String host() {
		return sock.getInetAddress().getHostName();
	}

	@Override 	
	public String userAgent() {
		return headers.get("user-agent");
	}

	@Override
	public int port() {
		return sock.getPort();
	}

	@Override
	public String pathInfo() {
		String pathInfoWithQueryParams = headers.get("pathInfoWithQueryParams");
		int index = pathInfoWithQueryParams.indexOf('?');
		if(index == -1) // No Query Params
			return pathInfoWithQueryParams;
		return pathInfoWithQueryParams.substring(0, index);
	}

	@Override
	public String url() {
		return "http://" +  headers.get("host")  + headers.get("pathInfoWithQueryParams");
	}

	@Override
	public String uri() {
		return "http://" +  headers.get("host") + pathInfo();
	}

	@Override
	public String protocol() {
		return headers.get("protocolVersion");
	}

	@Override
	public String contentType() {
		return headers.get("contentType");
	}

	@Override
	public String ip() {
		return sock.getInetAddress().getHostAddress();
	}

	@Override
	public String body() {
		return headers.get("body");
	}

	@Override
	public int contentLength() {
		return Integer.parseInt(headers.get("contentLength"));
	}

	@Override
	public String headers(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> headers() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
