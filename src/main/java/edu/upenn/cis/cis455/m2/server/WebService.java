/**
 * CIS 455/555 route-based HTTP framework
 * 
 * Z. Ives, 8/2017
 * 
 * Portions excerpted from or inspired by Spark Framework, 
 * 
 *                 http://sparkjava.com,
 * 
 * with license notice included below.
 */

/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.cis455.m2.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.m2.interfaces.Route;
import edu.upenn.cis.cis455.m2.interfaces.Filter;

public class WebService extends edu.upenn.cis.cis455.m1.server.WebService {
    final static Logger logger = LogManager.getLogger(WebService.class);
    //Me:
    public static ArrayList<Filter> beforeFiltersWithoutPath = new ArrayList<>();
    public static LinkedHashMap<String, Filter> beforeFiltersWithPath = new LinkedHashMap<>();
    public static ArrayList<Filter> afterFiltersWithoutPath = new ArrayList<>();
    public static LinkedHashMap<String, Filter> afterFiltersWithPath = new LinkedHashMap<>();

    public static LinkedHashMap<String, Route> getRequests = new LinkedHashMap<>();
    public static LinkedHashMap<String, Route> headRequests = new LinkedHashMap<>();
    public static LinkedHashMap<String, Route> postRequests = new LinkedHashMap<>();
    public static LinkedHashMap<String, Route> deleteRequests = new LinkedHashMap<>();
    public static LinkedHashMap<String, Route> optionsRequests = new LinkedHashMap<>();
    public static LinkedHashMap<String, Route> putRequests = new LinkedHashMap<>();
            
    
    public WebService() {
        super();
    }

    ///////////////////////////////////////////////////
    // For more advanced capabilities
    ///////////////////////////////////////////////////
    
    // ME: I added this one function
    public static void get(String path, Route route) {
    	getRequests.put(path, route);
    }
    

    /**
     * Handle an HTTP POST request to the path
     */
    public static void post(String path, Route route) {
    	postRequests.put(path, route);
    }

    /**
     * Handle an HTTP PUT request to the path
     */
    public static void put(String path, Route route) {
    	putRequests.put(path, route);
    }

    /**
     * Handle an HTTP DELETE request to the path
     */
    public static void delete(String path, Route route) {
    	deleteRequests.put(path, route);
    }

    /**
     * Handle an HTTP HEAD request to the path
     */
    public static void head(String path, Route route) {
    	headRequests.put(path, route);
    }

    /**
     * Handle an HTTP OPTIONS request to the path
     */
    public static void options(String path, Route route) {
    	optionsRequests.put(path, route);
    }

    ///////////////////////////////////////////////////
    // HTTP request filtering
    ///////////////////////////////////////////////////

    /**
     * Add filters that get called before a request
     */
    public static void before(Filter filter) {
    	beforeFiltersWithoutPath.add(filter);
    }

    /**
     * Add filters that get called after a request
     */
    public static void after(Filter filter) {
    	afterFiltersWithoutPath.add(filter);
    }

    /**
     * Add filters that get called before a request
     */
    public static void before(String path, Filter filter) {
    	beforeFiltersWithPath.put(path, filter);
    }

    /**
     * Add filters that get called after a request
     */
    public static void after(String path, Filter filter) {
    	afterFiltersWithPath.put(path, filter);
    }


    public static void executeBeforeMethods(RequestClass2 requestObject, ResponseClass2 responseObject) throws Exception{
    	for(Filter filter : beforeFiltersWithoutPath) {
    		filter.handle(requestObject, responseObject);
    	}
    	
    	for(Map.Entry<String, Filter> entry : beforeFiltersWithPath.entrySet()) {
    		if (matches(requestObject.pathInfo(), entry.getKey())) {
    			entry.getValue().handle(requestObject, responseObject);
    			break;
    		}
    	}
	}
    
    public static void executeAfterMethods(RequestClass2 requestObject, ResponseClass2 responseObject) throws Exception{
    	for(Map.Entry<String, Filter> entry : afterFiltersWithPath.entrySet()) {
    		if (matches(requestObject.pathInfo(), entry.getKey())) {
    			entry.getValue().handle(requestObject, responseObject);
    			break;
    		}
    	}
    	
    	for(Filter filter : afterFiltersWithoutPath) {
    		filter.handle(requestObject, responseObject);
    	}
	}
    
    
    public static Map.Entry<String, Route> getDefinedPathAndRoute(RequestClass2 requestObject) {
    	String receivedPath = requestObject.pathInfo();
    	String receivedMethod = requestObject.requestMethod().toUpperCase();
    	
    	switch (receivedMethod) {
	    	case "GET":
	    		for(Map.Entry<String, Route> entry : getRequests.entrySet()) {
	        		if (matches(receivedPath, entry.getKey()))
	        			return entry;
	        	}
	    		return null;
	    	case "HEAD":
	    		for(Map.Entry<String, Route> entry : headRequests.entrySet()) {
	        		if (matches(receivedPath, entry.getKey()))
	        			return entry;
	        	}
	    		return null;
	    	case "PUT":
	    		for(Map.Entry<String, Route> entry : putRequests.entrySet()) {
	        		if (matches(receivedPath, entry.getKey()))
	        			return entry;
	        	}
	    		return null;
	    	case "POST":
	    		for(Map.Entry<String, Route> entry : postRequests.entrySet()) {
	        		if (matches(receivedPath, entry.getKey()))
	        			return entry;
	        	}
	    		return null;
	    	case "DELETE":
	    		for(Map.Entry<String, Route> entry : deleteRequests.entrySet()) {
	        		if (matches(receivedPath, entry.getKey()))
	        			return entry;
	        	}
	    		return null;
	    	case "OPTIONS":
	    		for(Map.Entry<String, Route> entry : optionsRequests.entrySet()) {
	        		if (matches(receivedPath, entry.getKey()))
	        			return entry;
	        	}
	    		return null;
	    	default: // Unidentified method
	    		return null;
    	}
    	    	
	}
    
    // Helper method that checks if the 2 paths match
    public static boolean matches(String receivedPath, String definedPath) {
    	String[] receivedPathArr = receivedPath.split("/");
    	String[] definedPathArr = definedPath.split("/");
    	if (receivedPathArr.length != definedPathArr.length)
    		return false;
    	
    	for(int i = 0 ; i < receivedPathArr.length ; i++) {
    		if (receivedPathArr[i].equals(definedPathArr[i]))
    			continue;
    		if (definedPathArr[i].equals("*"))
    			continue;
    		if (receivedPathArr[i].length() > 0 && definedPathArr[i].length() > 0  &&  definedPathArr[i].charAt(0) == ':')
    			continue;
//    		if (i == receivedPathArr.length - 1 && receivedPathArr[i].startsWith(definedPathArr[i]))
//    			continue;
//    		logger.info("receivedPathArr[i]="+receivedPathArr[i]);
//    		logger.info("definedPathArr[i]="+definedPathArr[i]);
    		return false;
    	}
    	return true;
    }

	

}
