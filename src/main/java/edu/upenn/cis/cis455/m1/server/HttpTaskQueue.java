package edu.upenn.cis.cis455.m1.server;

import java.util.Queue;
import java.util.LinkedList;

/**
 * Stub class for implementing the queue of HttpTasks
 */
public class HttpTaskQueue {

	Queue<HttpTask> tasks;
	
	public HttpTaskQueue() {
		tasks = new LinkedList<>();
	}
	
	// Adds tasks to the queue
	public void addTask(HttpTask task) {
		tasks.add(task);
	}
	
	// Gets the task from the queue
	public HttpTask getTask() {
		return tasks.remove();
	}
	
	// Checks if empty
	public boolean isEmpty() {
		return tasks.size() == 0;
	}
	
}
