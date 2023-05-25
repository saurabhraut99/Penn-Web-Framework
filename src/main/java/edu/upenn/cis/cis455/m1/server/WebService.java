/**
 * CIS 455/555 route-based HTTP framework
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
package edu.upenn.cis.cis455.m1.server;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.exceptions.HaltException;


public class WebService {
    final static Logger logger = LogManager.getLogger(WebService.class);
    public static int port = 45555;
    public static String ipAddress = "0.0.0.0";
    public static String directory = "./www";
    public static int threads = 20;
    public static HttpTaskQueue sharedHttpTaskQueue = new HttpTaskQueue();
    
    static Thread httpListenerThread;
    public static ArrayList<Thread> httpWorkerThreads = new ArrayList<>();
    
    public static HttpListener listener = new HttpListener(sharedHttpTaskQueue);
    
//    public static WebService getSingleton() {
//        if (singletonWebService == null) 
//        	singletonWebService = new WebService();
//        return singletonWebService;
//    }
    
    /**
     * Launches the Web server thread pool and the listener
     */
    public static void start() {
    	httpListenerThread = new Thread(listener);
    	httpListenerThread.start();
    	
    	for(int i=0;i<WebService.threads;i++) {
    		Thread httpWorkerThread = new Thread(new HttpWorker(sharedHttpTaskQueue));
    		httpWorkerThreads.add(httpWorkerThread);
    		httpWorkerThread.start();
    	}
    	
//    	System.out.println("All server threads started");
    }

    /**
     * Gracefully shut down the server
     */
    public static void stop() {}

    /**
     * Hold until the server is fully initialized.
     * Should be called after everything else.
     */
    public static void awaitInitialization() {
        start();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt() {
        throw new HaltException();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(int statusCode) {
        throw new HaltException(statusCode);
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(String body) {
        throw new HaltException(body);
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(int statusCode, String body) {
        throw new HaltException(statusCode, body);
    }

    ////////////////////////////////////////////
    // Server configuration
    ////////////////////////////////////////////

    /**
     * Set the root directory of the "static web" files
     */
    public static void staticFileLocation(String directory) {
    	WebService.directory = directory;
    }

    /**
     * Set the IP address to listen on (default 0.0.0.0)
     */
    public static void ipAddress(String ipAddress) {
    	WebService.ipAddress = ipAddress;
    }

    /**
     * Set the TCP port to listen on (default 45555)
     */
    public static void port(int port) {
    	WebService.port = port;
    }

    /**
     * Set the size of the thread pool
     */
    public static void threadPool(int threads) {
    	WebService.threads = threads;
    }

}
