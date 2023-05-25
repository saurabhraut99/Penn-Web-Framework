package edu.upenn.cis.cis455.m1.server;

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

import org.apache.logging.log4j.Level;

public class TestSendException {
    @Before
    public void setUp() {
        org.apache.logging.log4j.core.config.Configurator.setLevel("edu.upenn.cis.cis455", Level.DEBUG);
    }
    
    String sampleGetRequest = 
//        "GET /a/b/hello.htm?q=x&v=12%200 HTTP/1.1\r\n" +
        "GET /add/5/6 HTTP/1.1\r\n" +
        "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n" +
        "Host: www.cis.upenn.edu\r\n" +
        "Accept-Language: en-us\r\n" +
        "Accept-Encoding: gzip, deflate\r\n" +
        "Cookie: name1=value1; name2=value2; name3=value3\r\n" +
        "Connection: Keep-Alive\r\n\r\n";
    
    String samplePostRequest = 
    		"POST /path/script.cgi HTTP/1.0\n"
    		+ "From: frog@jmarshall.com\n"
    		+ "User-Agent: HTTPTool/1.0\n"
    		+ "Content-Type: application/x-www-form-urlencoded\n"
    		+ "Content-Length: 32\n"
    		+ "\n"
    		+ "home=Cosby&favorite+flavor=flies";
//    
//    @Test
//    public void testSendException() throws IOException {
//        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        Socket s = TestHelper.getMockSocket(sampleGetRequest, byteArrayOutputStream);
//        HaltException halt = new HaltException(404, "Not found");
//        
//        HttpIoHandler.sendException(s, null, halt);
//        
//        String result = byteArrayOutputStream.toString("UTF-8").replace("\r", "");
////      assertTrue(result.startsWith("HTTP/1.1 404"));
//    }
    
//    @Test
//    public void test1() throws IOException, Exception {
//    	SparkController.before((request, response) -> {
//            System.out.println("I am in before filter");
//        });
//        
//        SparkController.get("/add/:x/:y", (req, res) -> {
//        	String x = req.params("x");
//        	String y = req.params("y");
//        	String ans;
//        	try {
//        		ans = String.valueOf( Integer.parseInt(x) + Integer.parseInt(y) );
//        	} catch(Exception e) {
//        		ans = "Invalid Input";
//        	}
//        	System.out.println("I added the numbers");
//        	return res;
//        });
//        
//        SparkController.after((request, response) -> {
//            System.out.println("I am in after filter");
//        });
//        
//    	
//        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        Socket s = TestHelper.getMockSocket(samplePostRequest, byteArrayOutputStream);
//        
//        HttpIoHandler hio = new HttpIoHandler();
//        
//        hio.generateAndSendResponse(s, new HttpTaskQueue());
//        
//        String result = byteArrayOutputStream.toString();
//        System.out.println("result=" + result);
////        assertTrue(result.startsWith("HTTP/1.1 404"));
//    }

    
    @After
    public void tearDown() {}
}
