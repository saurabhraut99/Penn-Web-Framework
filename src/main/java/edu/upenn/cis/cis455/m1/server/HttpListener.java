package edu.upenn.cis.cis455.m1.server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Stub for your HTTP server, which listens on a ServerSocket and handles
 * requests
 */


// Adds taks to the queue
public class HttpListener implements Runnable {

	HttpTaskQueue sharedHttpTaskQueue;
	public boolean flag = true;
	public ServerSocket listernerServerSocket;
	HttpListener(HttpTaskQueue sharedHttpTaskQueue) {
		this.sharedHttpTaskQueue = sharedHttpTaskQueue;
	}
	
	// Adds task to the queue
	public void addToQueue(HttpTask task) { 
		while (true) { 
			synchronized (sharedHttpTaskQueue) {
				sharedHttpTaskQueue.addTask(task);
				sharedHttpTaskQueue.notifyAll();
				break;
			}
		}
	}
	
	
	@Override
	public void run() {
		try {
			listernerServerSocket = new ServerSocket(45555);
			while (flag) {
				Socket socket = listernerServerSocket.accept(); /* blocks */
//				System.out.println("Request receiveddd by listener thread");
				addToQueue(new HttpTask(socket));
//				System.out.println("Request added to queue by listener thread");
			}
			listernerServerSocket.close();
		} catch(Exception e) {
			System.out.println("Excep in HttpListener");
		}
		System.out.println("Gracefully terminating listener thread");
		
	}
}
