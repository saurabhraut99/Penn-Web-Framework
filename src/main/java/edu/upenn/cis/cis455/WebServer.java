package edu.upenn.cis.cis455;

import static edu.upenn.cis.cis455.SparkController.*;
//import static spark.Spark.get;
import static spark.Spark.get;

import java.util.Map;
import java.util.List;
import java.util.Set;

import edu.upenn.cis.cis455.m2.interfaces.Session;

public class WebServer {
	public static void main(String[] args) {   

		SparkController.get("/add/:x/:y", (req, res) -> {
        	String x = req.params("x");
        	String y = req.params("y");
        	String ans;
        	try {
        		ans = String.valueOf(Integer.parseInt(x) + Integer.parseInt(y));
        	} catch(Exception e) {
        		ans = "Invalid Input";
        	}
        	return ans;
        });
        
		SparkController.get("/mul", (req, res) -> {
        	String x = req.queryParams("x");
        	String y = req.queryParams("y");
        	String ans;
        	try {
        		ans = String.valueOf( Integer.parseInt(x) * Integer.parseInt(y) );
        	} catch(Exception e) {
        		ans = "Invalid Input";
        	}
        	return ans;
        });
        
        awaitInitialization();
        
        System.out.println("Waiting to handle requests!");
    }


}
