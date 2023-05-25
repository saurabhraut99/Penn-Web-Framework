package edu.upenn.cis.cis455.m2.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import edu.upenn.cis.cis455.RequestClass;
import edu.upenn.cis.cis455.ResponseClass;
import edu.upenn.cis.cis455.SparkController;
import edu.upenn.cis.cis455.TestHelper;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.handling.HttpIoHandler;
import edu.upenn.cis.cis455.m1.interfaces.Request;
import edu.upenn.cis.cis455.m1.server.HttpTaskQueue;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Testing {
	final static Logger logger = LogManager.getLogger(Testing.class);
	
    @Before
    public void setUp() {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    }
    
    @Test
    public void test1() throws Exception{ // Checks exceptions

        SparkController.get("/add/:x/:y", (req, res) -> {
        	String x = req.params("x");
        	String y = req.params("y");
        	String ans;
        	ans = String.valueOf(Integer.parseInt(x) + Integer.parseInt(y) );
        	return ans;
        });
    	
        String getRequest = 
        		"GET /add/3/7/9 HTTP/1.1\r\n"
        	+ "Host: www.cis.upenn.edu";
        
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(getRequest, byteArrayOutputStream);
        HttpIoHandler httpIoHandler = new HttpIoHandler();
        httpIoHandler.generateAndSendResponse(s, new HttpTaskQueue());
        String result = byteArrayOutputStream.toString();
        assertTrue(result.startsWith("HTTP/1.1 404"));
    }
    
    @Test
    public void test2() throws Exception { // Test filters
    	
    	SparkController.before((request, response) -> {
//		     logger.info("Before filter without path");
		});
		
    	SparkController.before("/test/abc/saurabh", (request, response) -> {
// 		   logger.info("Before filter with path");
 		});
    	
    	SparkController.get("/test/abc/saurabh", (request, response) -> { 
//    		logger.info("Executing get request");
		   return "Saurabh";
		});
		
    	SparkController.after("/test/abc/saurabh", (request, response) -> {
//  		   logger.info("After filter with path");
  		});
    	
    	SparkController.after((request, response) -> {
//    		logger.info("After filter without path");
		});

        String getRequest = "GET /test/abc/saurabh HTTP/1.1\r\n"
        		+ "Host: www.cis.upenn.edu";
        
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(getRequest, byteArrayOutputStream);
        HttpIoHandler httpIoHandler = new HttpIoHandler();
        httpIoHandler.generateAndSendResponse(s, new HttpTaskQueue());
        String result = byteArrayOutputStream.toString();
        assertTrue(result.contains("Saurabh"));
    }
    
    
    @Test
    public void testWildcard() throws Exception { // Test wild cards
    	
    	SparkController.get("/abc/*", (request, response) -> { 
//    		logger.info("Executing get request");
		   return "Test";
		});

        String getRequest = "GET /abc/xyz HTTP/1.1\r\n"
        		+ "Host: www.cis.upenn.edu";
        
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(getRequest, byteArrayOutputStream);
        HttpIoHandler httpIoHandler = new HttpIoHandler();
        httpIoHandler.generateAndSendResponse(s, new HttpTaskQueue());
        String result = byteArrayOutputStream.toString();
        assertTrue(result.contains("Test"));
    }
    
    
    @Test
    public void testCookie() throws Exception { // Test cookies
    	
    	SparkController.get("/abc", (request, response) -> { 
    		response.cookie("Saurabh", "Pass");
    		return "Test";
		});

        String getRequest = "GET /abc HTTP/1.1\r\n"
        		+ "Host: www.cis.upenn.edu";
        
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(getRequest, byteArrayOutputStream);
        HttpIoHandler httpIoHandler = new HttpIoHandler();
        httpIoHandler.generateAndSendResponse(s, new HttpTaskQueue());
        String result = byteArrayOutputStream.toString();
//        logger.info("result="+result);
        assertTrue(result.contains("Pass"));
    }
    
    
    @Test
    public void testRedirect() throws Exception { // Test redirect
    	
    	SparkController.get("/abc", (request, response) -> { 
    		response.cookie("Saurabh", "Pass");
    		response.redirect("http://www.google.com");
    		return "Test";
		});

        String getRequest = "GET /abc HTTP/1.1\r\n"
        		+ "Host: www.cis.upenn.edu";
        
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Socket s = TestHelper.getMockSocket(getRequest, byteArrayOutputStream);
        HttpIoHandler httpIoHandler = new HttpIoHandler();
        httpIoHandler.generateAndSendResponse(s, new HttpTaskQueue());
        String result = byteArrayOutputStream.toString();
//        logger.info("result="+result);
        assertTrue(result.contains("http://www.google.com"));
        assertTrue(result.contains("302"));
    }
    
    

    
    @After
    public void tearDown() {}
}
