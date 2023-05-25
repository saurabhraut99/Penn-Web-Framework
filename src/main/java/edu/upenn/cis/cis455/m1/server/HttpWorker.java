package edu.upenn.cis.cis455.m1.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.handling.HttpIoHandler;

/**
 * Stub class for a thread worker that handles Web requests
 */
public class HttpWorker implements Runnable {
	
	public static boolean workerFlag = true;
	HttpTaskQueue sharedHttpTaskQueue;
	
	HttpWorker(HttpTaskQueue sharedHttpTaskQueue) {
		this.sharedHttpTaskQueue = sharedHttpTaskQueue;
	}
	
	// Reads from queue
	private HttpTask readFromQueue() throws InterruptedException {
		while (true) { // This loop's use is that after getting a wakeup at wait() step, he again has to check, 
			//what if some other thread consumed the item Because we know that when wait() is called the thread stops execution & releases the lock 
			synchronized (sharedHttpTaskQueue) {
				if (sharedHttpTaskQueue.isEmpty()) {
					//If the queue is empty, we push the current thread to waiting state. Way to avoid polling.
					if (workerFlag == false) {
						return null;
					}
					sharedHttpTaskQueue.wait();
					if (workerFlag == false) {
						return null;
					}
				} else {
					HttpTask httpTask = sharedHttpTaskQueue.getTask();
					sharedHttpTaskQueue.notifyAll(); 
					return httpTask;
				}
			}
		}
	}

	
    @Override
    public void run() {
    	while (workerFlag) {
    		Socket sock = null;
			try {
				HttpTask task = readFromQueue();
				if (task==null) {
					break;
				}
				sock = task.getSocket();
				HttpIoHandler httpIoHandler = new HttpIoHandler();
				httpIoHandler.generateAndSendResponse(sock, sharedHttpTaskQueue);
			} catch (HaltException HE) {
//				System.out.println("Halt Exception in generateAndSendResponse caught by HTTPworker");				
				try {
					byte[] IOExceptionMsg = "HTTP/1.1 404 HaltException\r\n".getBytes();
					OutputStream outputStream = sock.getOutputStream();
					outputStream.write(IOExceptionMsg);
					outputStream.flush(); sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (IOException IOE) {
//				System.out.println("IO Exception in generateAndSendResponse caught by HTTPworker");				
				try {
					byte[] IOExceptionMsg = "HTTP/1.1 404 IOException\r\n".getBytes();
					OutputStream outputStream = sock.getOutputStream();
					outputStream.write(IOExceptionMsg);
					outputStream.flush(); sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (InterruptedException ex) {
//				System.out.println("Interrupted Exception");
				try {
					byte[] InterruptedExceptionMsg = "HTTP/1.1 404 IOException\r\n".getBytes();
					OutputStream outputStream = sock.getOutputStream();
					outputStream.write(InterruptedExceptionMsg);
					outputStream.flush(); sock.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
//				System.out.println("Exception");
				try {
					byte[] ExceptionMsg = "HTTP/1.1 404 Exception\r\n".getBytes();
					OutputStream outputStream = sock.getOutputStream();
					outputStream.write(ExceptionMsg);
					outputStream.flush(); sock.close();
				} catch (IOException E) {
					E.printStackTrace();
				}
			}
			
  		}
//    	System.out.println("Gracefully terminating thread ="+ Thread.currentThread().getName());
    }
}
