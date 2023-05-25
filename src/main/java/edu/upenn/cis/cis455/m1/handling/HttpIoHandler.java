/*package edu.upenn.cis.cis455.m1.handling;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.HttpParsing;
import edu.upenn.cis.cis455.RequestClass;
import edu.upenn.cis.cis455.ResponseClass;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.interfaces.Request;
import edu.upenn.cis.cis455.m1.interfaces.Response;
import edu.upenn.cis.cis455.m1.server.HttpTaskQueue;
import edu.upenn.cis.cis455.m1.server.HttpWorker;
import edu.upenn.cis.cis455.m1.server.WebService;


 // Handles marshaling between HTTP Requests and Responses
 
public class HttpIoHandler {
    final static Logger logger = LogManager.getLogger(HttpIoHandler.class);
    public static byte[] IOExceptionMsg = "HTTP/1.1 404 IOException\r\n".getBytes();
    public static byte[] HaltExceptionMsg = "HTTP/1.1 404 HaltException\r\n".getBytes();
    
    
      Sends an exception back, in the form of an HTTP response code and message.
      Returns true if we are supposed to keep the connection open (for persistent
      connections).
     
    public static boolean sendException(Socket socket, Request request, HaltException except) {
    	return true;
    }

    
      Sends data back. Returns true if we are supposed to keep the connection open
      (for persistent connections).
     
    public static boolean sendResponse(Socket socket, Request request, Response response) {
        return true;
    }
    
    
    // Generates and sends response to socket
    public void generateAndSendResponse(Socket sock, HttpTaskQueue sharedHttpTaskQueue) throws IOException{
    	OutputStream outputStream = sock.getOutputStream();
//    	System.out.println("PICKED a request from queue by thread="+Thread.currentThread().getName());
    	
    	//CREATE REQUEST OBJ. 
    	RequestClass requestObject;
    	Map<String, String> headers = new HashMap<>();
        Map<String, List<String>> queryParams = new HashMap<>();
        try {
        	String pathInfoWithQueryParams = HttpParsing.parseRequest(sock.getRemoteSocketAddress().toString(),sock.getInputStream(), headers, queryParams); //***DOUBTFUL
        	headers.put("pathInfoWithQueryParams", pathInfoWithQueryParams);
        } catch(HaltException HE) { // This happens when HttpParsing class is unable to read from socket and keeps waiting
        	System.out.println("Halt exception in HTTP Parsing");
        	outputStream.write(HaltExceptionMsg);
        	outputStream.flush(); sock.close();
        	return;
        }
        
//        System.out.print("PARSED a request from queue by thread="+Thread.currentThread().getName()+ " and ");
//    	System.out.println("headers.pathInfoWithQueryParams===" + headers.get("pathInfoWithQueryParams"));
 
        requestObject = new RequestClass(headers,sock);
        
        
        //CREATE RESPONSE OBJ. 
        ResponseClass responseObject = generateResponseObject(requestObject, sharedHttpTaskQueue);
//        System.out.println("Response object created");
        outputStream.write(responseObject.response);
		outputStream.flush();
		sock.close();
//		System.out.println("Response sent by thread="+Thread.currentThread().getName()+" to "+headers.get("pathInfoWithQueryParams"));
    	return;
    }
    
    
    // Generates response object
    public ResponseClass generateResponseObject(RequestClass requestObject, HttpTaskQueue sharedHttpTaskQueue) throws IOException{
    	ResponseClass responseObject;
    	
    	// Check if the file exists
    	String filePath = WebService.directory + requestObject.pathInfo();
    	Path path = Paths.get(filePath);
//    	System.out.println("filePath="+filePath);
    	
    	if (filePath.endsWith("/shutdown")) { // Shut down page
			return handleShutdownRequest(sharedHttpTaskQueue);
		}
		if (filePath.endsWith("/control")) { // Control page
			return handleControlRequest(requestObject);
		}
    	if (!Files.exists(path)) { // File or Path does not exist
    		byte[] responseInBytes = generateHTTPResponseInBytesFromString("File path does not exist", "404 FileNotFound");
    		return new ResponseClass(responseInBytes);
    	}
    	if (Files.isDirectory(path)) {
    		filePath += "/index.html";
    		path = Paths.get(filePath);
    		if (!Files.exists(path)) { // File or Path does not exist
        		byte[] responseInBytes = generateHTTPResponseInBytesFromString("File path does not exist", "404 FileNotFound");
        		return new ResponseClass(responseInBytes);
        	}
    	}
    	
//    	File requestedFile = new File(filePath);
//    	String requestedFileCanonicalPath = requestedFile.getCanonicalPath();
    	
    	// CHECK if not accessing restricted area
//    	File staticRootDirectory = new File(WebService.directory);
//    	String staticRootCanonicalPath = staticRootDirectory.getCanonicalPath();
//    	System.out.println("staticRootCanonicalPath="+staticRootCanonicalPath);
//    	if (!filePath.startsWith(staticRootCanonicalPath)) {
//    		byte[] responseInBytes = generateHTTPResponseInBytesFromString("Cannot access this path", "404 FileNotFound");
//    		return new ResponseClass(responseInBytes);
//    	}
    	
    	// Now read the file contents
    	byte[] body = null;
    	String contentType = Files.probeContentType(path); // This just finds MIME type irrespective if file is present or not
    	body = Files.readAllBytes(path); // This will throw IOException if file does not exist
    	requestObject.headers.put("contentType", contentType);
    	requestObject.headers.put("contentLength", String.valueOf(body.length));
    	requestObject.headers.put("body", Base64.getEncoder().encodeToString(body));
    	
    	if (requestObject.requestMethod().equalsIgnoreCase("GET")) {
    		String hTTPHeaderString = generateHTTPHeaderString(contentType, String.valueOf(body.length), "200 OK");
    		byte[] httpHeaderBytes = hTTPHeaderString.getBytes();
    		byte[] finalResponse = new byte[httpHeaderBytes.length + body.length];
        	int i;
        	for(i=0 ; i < httpHeaderBytes.length ; i++)
        		finalResponse[i] = httpHeaderBytes[i];
        	for(int j=0 ; j<body.length ; j++,i++) 
        		finalResponse[i] = body[j];
        	
        	responseObject = new ResponseClass(finalResponse);
        	responseObject.headersString = hTTPHeaderString;
        	
    	} else if (requestObject.requestMethod().equalsIgnoreCase("HEAD")){
    		String hTTPHeaderString = generateHTTPHeaderString(null, String.valueOf(0), "200 OK");
    		byte[] httpHeaderBytes = hTTPHeaderString.getBytes();
        	responseObject = new ResponseClass(httpHeaderBytes);
        	responseObject.headersString = hTTPHeaderString;
    	} else {
    		throw new IOException();
    	}
    	
    	return responseObject;
    	
    }
    
    // Handles shutdown requests
    ResponseClass handleShutdownRequest(HttpTaskQueue sharedHttpTaskQueue) throws IOException {
    	// Shutdown listener thread
    	WebService.listener.flag = false; // Listener stops adding tasks in the queue
    	WebService.listener.listernerServerSocket.close(); // Will throw exception in listernerServerSocket and stop accept blocking call
    	
    	// Shutdown worker threads
    	HttpWorker.workerFlag = false;
    	synchronized(sharedHttpTaskQueue) {
    		sharedHttpTaskQueue.notifyAll();
    	}
    	byte[] shutDownResponseInBytes = generateHTTPResponseInBytesFromString("Shutting down...", "200 OK");
        return new ResponseClass(shutDownResponseInBytes);
    }
    
    // Handles control requests
    ResponseClass handleControlRequest(RequestClass requestObject) throws IOException {   	
    	String HTMLBody = "<!DOCTYPE html><html>";
    	ArrayList<Thread> httpWorkerThreads = WebService.httpWorkerThreads; 
    	for(Thread thread : httpWorkerThreads) {
    		if(thread.getState() == Thread.State.RUNNABLE) {
    			HTMLBody += "<li>" + thread.getName() + "-" + requestObject.url() + "</li>";
    		} else {
    			HTMLBody += "<li>" + thread.getName() + "-" +thread.getState() + "</li>";
    		}
    	}
    	HTMLBody += "<a href=\"/shutdown\">SHUTDOWN</a>";
    	HTMLBody += "</html>";
    	
    	String headerString = generateHTTPHeaderString("text/html", String.valueOf(HTMLBody.length()), "200 OK");
    	byte[] finalResponse = (headerString + HTMLBody).getBytes();
		return new ResponseClass(finalResponse);
    }
    
    // Generate HTTP response
    byte[] generateHTTPResponseInBytesFromString(String msg, String responseCode) {
    	byte[] msgBytes = msg.getBytes();
    	byte[] headerBytes =  generateHTTPHeaderString("text/plain", String.valueOf(msgBytes.length), responseCode).getBytes();
    	byte[] finalResponse = new byte[headerBytes.length + msgBytes.length];
    	int i;
    	for(i=0 ; i < headerBytes.length ; i++)
    		finalResponse[i] = headerBytes[i];
    	for(int j=0 ; j<msgBytes.length ; j++,i++) 
    		finalResponse[i] = msgBytes[j];
    	return finalResponse;
    }
    
    // Generate headers
    String generateHTTPHeaderString(String contentType, String contentLength, String responseCode) {
    	String headers = "HTTP/1.1 "+responseCode+"\r\n";
    	headers += "Date: Fri, 27 Dec 1965 20:20:20 GMT\r\n";
    	headers += "Connection: close\r\n";
    	headers += "content-type: " + contentType + "\r\n";
    	headers += "content-length: " + contentLength + "\r\n";
    	headers += "\r\n";
    	return headers;
    }
    
}
*/

package edu.upenn.cis.cis455.m1.handling;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;

import edu.upenn.cis.cis455.HttpParsing;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.interfaces.Request;
import edu.upenn.cis.cis455.m1.interfaces.Response;
import edu.upenn.cis.cis455.m2.interfaces.Route;
import edu.upenn.cis.cis455.m1.server.HttpTaskQueue;
import edu.upenn.cis.cis455.m1.server.HttpWorker;
import edu.upenn.cis.cis455.m2.server.RequestClass2;
import edu.upenn.cis.cis455.m2.server.ResponseClass2;
import edu.upenn.cis.cis455.m2.server.WebService;

 // Handles marshaling between HTTP Requests and Responses
public class HttpIoHandler {
    final static Logger logger = LogManager.getLogger(HttpIoHandler.class);
    public static byte[] IOExceptionMsg = "HTTP/1.1 404 IOException\r\n".getBytes();
    public static byte[] HaltExceptionMsg = "HTTP/1.1 404 HaltException\r\n".getBytes();
    
    
//      Sends an exception back, in the form of an HTTP response code and message.
//      Returns true if we are supposed to keep the connection open (for persistent
//      connections).
    public static boolean sendException(Socket socket, Request request, HaltException except) {
    	return true;
    }

    
//  Sends data back. Returns true if we are supposed to keep the connection open
//  (for persistent connections).
    public static boolean sendResponse(Socket socket, Request request, Response response) {
        return true;
    }
    
            
    // Generates and sends response to socket
    public void generateAndSendResponse(Socket sock, HttpTaskQueue sharedHttpTaskQueue) 
    		throws HaltException, IOException, Exception {
    	OutputStream outputStream = sock.getOutputStream();
    	
    	//CREATE REQUEST OBJ. 
    	RequestClass2 requestObject;
    	Map<String, String> headers = new HashMap<>();
        Map<String, List<String>> queryParams = new HashMap<>();
        ArrayList<String> requestBodyArl = new ArrayList<>();
    	String pathInfoWithQueryParams = HttpParsing.parseRequest(sock.getRemoteSocketAddress().toString(),
    			sock.getInputStream(), headers, queryParams, requestBodyArl); 
    	headers.put("pathInfoWithQueryParams", pathInfoWithQueryParams);
 
        if (requestBodyArl.size() > 0) { // The request has some body
        	if (headers.get("Method").equalsIgnoreCase("PUT") && headers.get("content-type").equals("application/x-www-form-urlencoded")) {
        		requestObject = new RequestClass2(headers, queryParams, requestBodyArl.get(0), sock);
            	Map<String, List<String>> extraParams = new HashMap<>();
            	HttpParsing.decodeParms(requestBodyArl.get(0), extraParams, "&");
            	requestObject.appendToQueryParams(extraParams);
        	} else {
        		requestObject = new RequestClass2(headers, queryParams, requestBodyArl.get(0), sock);
        	}
        } else { // Request has no body
    		requestObject = new RequestClass2(headers, queryParams, "", sock);
    	}
        
        Map.Entry<String, Route> definedPathAndRoute = WebService.getDefinedPathAndRoute(requestObject);
        if (definedPathAndRoute != null)
        	requestObject.definedPath = definedPathAndRoute.getKey();
        
        logger.info(requestObject.url());
        
                
        //CREATE RESPONSE OBJ.
        ResponseClass2 responseObject;
        if (definedPathAndRoute != null) { // Route defined by user
//        	logger.info("Route defined");
        	responseObject = generateResponseObjectWhenRouteDefined(requestObject, definedPathAndRoute);
        } else { // Route not defined by user
//        	logger.info("Route not defined");
        	responseObject = generateResponseObjectWhenRouteNOTDefined(requestObject, sharedHttpTaskQueue);
        }
        outputStream.write(responseObject.entireResponseInBytes);
		outputStream.flush(); sock.close();
    	return;
    }
    
    // Generates response object When Route Defined
    public ResponseClass2 generateResponseObjectWhenRouteDefined(RequestClass2 requestObject, Map.Entry<String, Route> definedPathAndRoute) throws Exception {
    	ResponseClass2 responseObject = new ResponseClass2(null); 
    	WebService.executeBeforeMethods(requestObject, responseObject); // Sending requestObject to set it's attributes
    	
    	String responseBody = "";
    	try {
    		responseBody = definedPathAndRoute.getValue().handle(requestObject, responseObject).toString();
    	} catch(Exception e) {
    		logger.info("Exception while handling the user defined route");
    		throw new Exception();
    	}
    	responseObject.body(responseBody);
    	responseObject.header("protocolVersion", requestObject.protocol());
    	responseObject.header("responseCode", "200 OK");
    	responseObject.header("content-length", String.valueOf(responseBody.getBytes().length));
    	responseObject.header("content-type", "text/plain");
    	String responseHeaders = responseObject.getHeaders();
    	
    	byte[] response = convertTwoStringsToByte(responseHeaders, responseBody);
    	responseObject.entireResponseInBytes = response;
    	WebService.executeAfterMethods(requestObject, responseObject);
    	return responseObject;
    }
           
    // Generates response object When Route NOT Defined
    public ResponseClass2 generateResponseObjectWhenRouteNOTDefined(RequestClass2 requestObject, HttpTaskQueue sharedHttpTaskQueue) throws IOException{
    	ResponseClass2 responseObject;
    	
    	// Check if the file exists
    	String filePath = WebService.directory + requestObject.pathInfo();
    	Path path = Paths.get(filePath);
//    	System.out.println("WebService.directory="+WebService.directory);
//    	System.out.println("requestObject.pathInfo()="+requestObject.pathInfo());
//    	System.out.println("filePath="+filePath);
    	
    	if (filePath.endsWith("/shutdown")) { // Shut down page
			return handleShutdownRequest(sharedHttpTaskQueue);
		}
		if (filePath.endsWith("/control")) { // Control page
			return handleControlRequest(requestObject);
		}
		
//		System.out.println("filepath="+filePath);
    	if (!Files.exists(path)) { // File or Path does not exist
    		byte[] responseInBytes = generateHTTPResponseInBytesFromString("File path does not exist", "404 FileNotFound");
    		return new ResponseClass2(responseInBytes);
    	}
    	if (Files.isDirectory(path)) {
    		filePath += "/index.html";
    		path = Paths.get(filePath);
    		if (!Files.exists(path)) { // File or Path does not exist
//    			logger.info("File does not exist!!");
        		byte[] responseInBytes = generateHTTPResponseInBytesFromString("File path does not exist", "404 FileNotFound");
        		return new ResponseClass2(responseInBytes);
        	}
    	}
    	
//    	File requestedFile = new File(filePath);
//    	String requestedFileCanonicalPath = requestedFile.getCanonicalPath();
    	
    	// CHECK if not accessing restricted area
//    	File staticRootDirectory = new File(WebService.directory);
//    	String staticRootCanonicalPath = staticRootDirectory.getCanonicalPath();
//    	System.out.println("staticRootCanonicalPath="+staticRootCanonicalPath);
//    	if (!filePath.startsWith(staticRootCanonicalPath)) {
//    		byte[] responseInBytes = generateHTTPResponseInBytesFromString("Cannot access this path", "404 FileNotFound");
//    		return new ResponseClass(responseInBytes);
//    	}
    	
    	// Now read the file contents
    	byte[] body = null;
    	String requestedFileContentType = Files.probeContentType(path); // This just finds MIME type irrespective if file is present or not
    	body = Files.readAllBytes(path); // This will throw IOException if file does not exist
//    	requestObject.headers.put("contentType", contentType);
//    	requestObject.headers.put("contentLength", String.valueOf(body.length));
    	
    	if (requestObject.requestMethod().equalsIgnoreCase("GET")) {
    		String hTTPHeaderString = generateHTTPHeaderString(requestedFileContentType, String.valueOf(body.length), "200 OK");
    		byte[] httpHeaderBytes = hTTPHeaderString.getBytes();
    		byte[] finalResponse = new byte[httpHeaderBytes.length + body.length];
        	int i;
        	for(i=0 ; i < httpHeaderBytes.length ; i++)
        		finalResponse[i] = httpHeaderBytes[i];
        	for(int j=0 ; j<body.length ; j++,i++) 
        		finalResponse[i] = body[j];
        	
        	responseObject = new ResponseClass2(finalResponse);
//        	responseObject.headersString = hTTPHeaderString;
        	
    	} else if (requestObject.requestMethod().equalsIgnoreCase("HEAD")){
    		String hTTPHeaderString = generateHTTPHeaderString(null, String.valueOf(0), "200 OK");
    		byte[] httpHeaderBytes = hTTPHeaderString.getBytes();
        	responseObject = new ResponseClass2(httpHeaderBytes);
//        	responseObject.headersString = hTTPHeaderString;
    	} else {
    		throw new IOException();
    	}
    	return responseObject;
    }
    
    // Handles shutdown requests
    ResponseClass2 handleShutdownRequest(HttpTaskQueue sharedHttpTaskQueue) throws IOException {
    	// Shutdown listener thread
    	WebService.listener.flag = false; // Listener stops adding tasks in the queue
    	WebService.listener.listernerServerSocket.close(); // Will throw exception in listernerServerSocket and stop accept blocking call
    	
    	// Shutdown worker threads
    	HttpWorker.workerFlag = false;
    	synchronized(sharedHttpTaskQueue) {
    		sharedHttpTaskQueue.notifyAll();
    	}
    	byte[] shutDownResponseInBytes = generateHTTPResponseInBytesFromString("Shutting down...", "200 OK");
        return new ResponseClass2(shutDownResponseInBytes);
    }
    
    // Handles control requests
    ResponseClass2 handleControlRequest(RequestClass2 requestObject) throws IOException {   	
    	String HTMLBody = "<!DOCTYPE html><html>";
    	ArrayList<Thread> httpWorkerThreads = WebService.httpWorkerThreads; 
    	for(Thread thread : httpWorkerThreads) {
    		if(thread.getState() == Thread.State.RUNNABLE) {
    			HTMLBody += "<li>" + thread.getName() + "-" + requestObject.url() + "</li>";
    		} else {
    			HTMLBody += "<li>" + thread.getName() + "-" +thread.getState() + "</li>";
    		}
    	}
    	HTMLBody += "<a href=\"/shutdown\">SHUTDOWN</a>";
    	HTMLBody += readLogFile();
    	HTMLBody += "</html>";
    	
    	String headerString = generateHTTPHeaderString("text/html", String.valueOf(HTMLBody.length()), "200 OK");
    	byte[] finalResponse = (headerString + HTMLBody).getBytes();
		return new ResponseClass2(finalResponse);
    }
    
    // Merges and gives bytes response
    byte[] convertTwoStringsToByte(String s1, String s2) {
    	byte[] b1 = s1.getBytes();
    	byte[] b2 =  s2.getBytes();
    	byte[] ans = new byte[b1.length + b2.length];
    	int i;
    	for(i=0 ; i < b1.length ; i++)
    		ans[i] = b1[i];
    	for(int j=0 ; j<b2.length ; j++,i++) 
    		ans[i] = b2[j];
    	return ans;
    }
    
    // Generate HTTP response
    byte[] generateHTTPResponseInBytesFromString(String msg, String responseCode) {
    	byte[] msgBytes = msg.getBytes();
    	byte[] headerBytes =  generateHTTPHeaderString("text/plain", String.valueOf(msgBytes.length), responseCode).getBytes();
    	byte[] finalResponse = new byte[headerBytes.length + msgBytes.length];
    	int i;
    	for(i=0 ; i < headerBytes.length ; i++)
    		finalResponse[i] = headerBytes[i];
    	for(int j=0 ; j<msgBytes.length ; j++,i++) 
    		finalResponse[i] = msgBytes[j];
    	return finalResponse;
    }
    
    // Generate headers
    String generateHTTPHeaderString(String contentType, String contentLength, String responseCode) {
    	String headers = "HTTP/1.1 "+responseCode+"\r\n";
//    	headers += "Date: Fri, 27 Dec 1965 20:20:20 GMT\r\n";
    	headers += "Connection: close\r\n";
    	headers += "content-type: " + contentType + "\r\n";
    	headers += "content-length: " + contentLength + "\r\n";
    	headers += "\r\n";
    	return headers;
    }
    
    
    String readLogFile() throws IOException {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        Map<String, Appender> appenders = config.getAppenders();
        String fileName = "";
        for (String name: appenders.keySet()) {
            Appender appender = appenders.get(name);
            if (appender instanceof FileAppender) {
                fileName = ((FileAppender) appender).getFileName();
                break;
            }
        }
        Path path = Paths.get(fileName);
        return new String(Files.readAllBytes(path));
    }

    void printMap(HashMap<String, String> hashMap) {
      System.out.println("hashMap:");
      for(Map.Entry<String, String> entry : hashMap.entrySet())
      	System.out.println(entry.getKey() + "=" + entry.getValue());
      System.out.println("hashMap over!!");
    }
}
