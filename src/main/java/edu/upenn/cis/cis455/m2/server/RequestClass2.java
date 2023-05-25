package edu.upenn.cis.cis455.m2.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.upenn.cis.cis455.HttpParsing;
import edu.upenn.cis.cis455.m2.interfaces.*;


public class RequestClass2 extends Request{

	public Map<String, String> headers;
	public Map<String, List<String>> queryParams;
	public String requestBody;
	public String definedPath = "";
	Socket sock;
	Map<String, Object> attributesMap = new HashMap<>();
//	CookieClass cookie = new CookieClass();
	
	
	public RequestClass2(Map<String, String> headers, Map<String, List<String>> queryParams, String requestBody, Socket sock) {
		this.headers = headers;
		this.queryParams = queryParams;
		this.requestBody = requestBody;
		this.sock = sock;
	}
	
	@Override
	public Session session() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Session session(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> params() {
		String[] definedPathInfo = definedPath.split("/");
		String[] receivedPathInfo = pathInfo().split("/");
		if (definedPathInfo.length != receivedPathInfo.length) {
			System.out.println("NOT EQUALLLL");
			return null;
		}
		Map<String, String> hashMap = new HashMap<>();
		for (int i = 0 ; i < definedPathInfo.length ; i++) {
			if (definedPathInfo[i].length() > 0 && definedPathInfo[i].charAt(0) == ':') {
				hashMap.put(definedPathInfo[i], receivedPathInfo[i]);
			}
		}
		return hashMap;
	}

	@Override
	public String queryParams(String param) {
		return queryParams.get(param).get(0);
	}

	@Override
	public List<String> queryParamsValues(String param) {
		return queryParams.get(param);
	}

	@Override
	public Set<String> queryParams() {
		return queryParams.keySet();
	}

	@Override
	public String queryString() {
		String pathInfoWithQueryParams = headers.get("pathInfoWithQueryParams");
		if (pathInfoWithQueryParams.contains("?"))
			return pathInfoWithQueryParams.substring(pathInfoWithQueryParams.indexOf('?'));
		return "";
	}

	@Override
	public void attribute(String attrib, Object val) {
		attributesMap.put(attrib, val);
	}

	@Override
	public Object attribute(String attrib) {
		return attributesMap.get(attrib);
	}

	@Override
	public Set<String> attributes() {
		return attributesMap.keySet();
	}

	@Override
	public Map<String, String> cookies() {
		if (!headers.containsKey("cookie"))
			return null;
		HashMap<String, List<String>> hashMap = new HashMap<>();
		HttpParsing.decodeParms(headers.get("cookie"), hashMap, ";");
		
		HashMap<String, String> cookieNameValue = new HashMap<>();
		for (Map.Entry<String, List<String>> entry : hashMap.entrySet()) {
			cookieNameValue.put(entry.getKey(), entry.getValue().get(0));
		}
		return cookieNameValue;
	}	

	public String params(String param) {
        if (param == null)
            return null;

        if (param.startsWith(":"))
            return params().get(param.toLowerCase());
        else
            return params().get(':' + param.toLowerCase());
    }
	
	
	public String queryParamOrDefault(String param, String def) {
        String ret = queryParams(param);

        return (ret == null) ? def : ret;
    }
	
	
	public String cookie(String name) {
        if (name == null || cookies() == null)
            return null;
        else
            return cookies().get(name);
    }

	
	
	
	
	
	public void appendToQueryParams(Map<String, List<String>> map) {
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			queryParams.put(entry.getKey(), entry.getValue());
		}
	}
	
	
	//----------------------------------------MS1:
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
		return headers.get("content-type");
	}

	@Override
	public String ip() {
		return sock.getInetAddress().getHostAddress();
	}

	@Override
	public String body() {
		return requestBody;
	}

	@Override
	public int contentLength() {
		return Integer.parseInt(headers.get("content-length"));
	}

	@Override
	public String headers(String name) {
		return headers.get(name);
	}

	@Override
	public Set<String> headers() {
		return headers.keySet();
	}

	


}
