/**
 * CIS 455/555 route-based HTTP framework.
 * 
 * Basic service handler and route manager.
 * 
 * V. Liu, Z. Ives
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

package edu.upenn.cis.cis455;

import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m2.interfaces.Route;
import edu.upenn.cis.cis455.m2.interfaces.Filter;
import edu.upenn.cis.cis455.m2.interfaces.Session;

// change to to edu.upenn.cis.cis455.m2 for m2
import edu.upenn.cis.cis455.m2.server.WebService;

public class SparkController {

    // We don't want people to use the constructor
    protected SparkController() {}

    /**
     * Milestone 2 only: Handle an HTTP GET request to the path
     */
    public static void get(String path, Route route) {
    	WebService.get(path, route);
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Handle an HTTP POST request to the path
     */
    public static void post(String path, Route route) {
        WebService.post(path, route);
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Handle an HTTP PUT request to the path
     */
    public static void put(String path, Route route) {
        WebService.put(path, route);
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Handle an HTTP DELETE request to the path
     */
    public static void delete(String path, Route route) {
    	WebService.delete(path, route);
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Handle an HTTP HEAD request to the path
     */
    public static void head(String path, Route route) {
        WebService.head(path, route);
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Handle an HTTP OPTIONS request to the path
     */
    public static void options(String path, Route route) {
        WebService.options(path, route);
    	//throw new UnsupportedOperationException();
    }

    ///////////////////////////////////////////////////
    // HTTP request filtering
    ///////////////////////////////////////////////////

    /**
     * Milestone 2 only: Add filters that get called before a request
     */
    public static void before(Filter... filters) {
    	// Me
    	for (Filter filter : filters) {
            WebService.before(filter);
        }
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Add filters that get called after a request
     */
    public static void after(Filter... filters) {
    	for (Filter filter : filters) {
    		WebService.after(filter);
        }
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Add filters that get called before a request
     */
    public static void before(String path, Filter... filters) {
    	for (Filter filter : filters) {
            WebService.before(path, filter);
        }
    	//throw new UnsupportedOperationException();
    }

    /**
     * Milestone 2 only: Add filters that get called after a request
     */
    public static void after(String path, Filter... filters) {
    	for (Filter filter : filters) {
    		WebService.after(path, filter);
        }
    	//throw new UnsupportedOperationException();
    }
    
    
    // The following 2 functions are OPTIONAL for Milestone 1.
    // They will be used in Milestone 2 so user code can fail
    // and we will gracefully return something.

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt() {
        return WebService.halt();
    	//throw new UnsupportedOperationException();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(int statusCode, String body) {
    	return WebService.halt(statusCode, body);
    	//throw new UnsupportedOperationException();
    }

    ////////////////////////////////////////////
    // Server configuration
    ////////////////////////////////////////////

    /**
     * Set the IP address to listen on (default 0.0.0.0)
     */
    public static void ipAddress(String ipAddress) {
    	WebService.ipAddress(ipAddress);
//        throw new UnsupportedOperationException();
    }

    /**
     * Set the port to listen on (default 45555)
     */
    public static void port(int port) {
    	WebService.port(port);
//        throw new UnsupportedOperationException();
    }

    /**
     * Set the size of the thread pool
     */
    public static void threadPool(int threads) {
    	WebService.threadPool(threads);
//        throw new UnsupportedOperationException();
    }

    /**
     * Set the root directory of the "static web" files
     */
    public static void staticFileLocation(String directory) {
    	WebService.staticFileLocation(directory);
//        throw new UnsupportedOperationException();
    }

    /**
     * Hold until the server is fully initialized
     */
    public static void awaitInitialization() {
    	WebService.awaitInitialization();
    }

    /**
     * Gracefully shut down the server
     */
    public static void stop() {
    	WebService.stop(); 
//        throw new UnsupportedOperationException();
    }

    public static String createSession() {
        throw new UnsupportedOperationException();
    }

    public static Session getSession(String id) {
        throw new UnsupportedOperationException();
    }
}
