package edu.upenn.cis.cis455.m2.server;

public class CookieClass {

	private String path = "";
	private String name;
	private String value;
	private int maxAge = -1;
	private boolean secured = false;
	private boolean httpOnly = false;
	
        
    public CookieClass(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setMaxAge(int expiry) {
        maxAge = expiry;
    }
    
    public int getMaxAge() {
        return maxAge;
    }
    
    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setSecured(boolean secured) {
        this.secured = secured;
    }

    public boolean getSecured() {
        return secured;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }
 
    public boolean getHttpOnly() {
        return httpOnly;
    }
}

