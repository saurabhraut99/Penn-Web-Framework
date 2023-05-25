package edu.upenn.cis.cis455;

import edu.upenn.cis.cis455.m1.interfaces.Response;


public class ResponseClass extends Response{

	public byte[] response;
	public String headersString;
	
	public ResponseClass(byte[] response) {
		this.response = response;
	}
	
	@Override
	public String getHeaders() {
		return headersString;
	}
	
}
