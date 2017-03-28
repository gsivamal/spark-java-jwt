package com.server;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import spark.ModelAndView;
import spark.Request;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

import static spark.Spark.*;

public class SparkServer {

	private static final String SECRET;

		static {
	        SECRET = "my company secret";
	    }

	private static JWTSigner signer = new JWTSigner(SECRET);
	private static JWTVerifier verifier = new JWTVerifier(SECRET);

	private FreeMarkerEngine templateEngine = new FreeMarkerEngine();

	//hash db
	Hashtable<String, String> tokenDb = new Hashtable<String, String>();

	void bootstarp(){
		//1. Datasource initialize
	}

	 void startApp(){

		staticFileLocation("/webapp/html/"); // Static files

		get("/hello", (req, res) -> "Hello World");

		get("/login", (req, resp) -> {
            Map<String, Object> model = new HashMap<>();
            	model.put("message", "Hello World!");

            // The hello.ftl file is located in directory:
            // src/test/resources/spark/template/freemarker
            return new ModelAndView(model, "login.ftl");

        }, templateEngine );

		post("/auth", (req, resp) -> {

		  	String email = req.queryParams("email");
			String password = req.queryParams("password");
			System.out.println("Password:"+password);

			return 301;// salai check this later...
		});

		get("/dashboard", (req, resp) -> {

			String token =	resp.raw().getHeader("authorization");

			System.out.println("Token in dashboard:"+token);

			 return new ModelAndView(null, "index.ftl");
		}, templateEngine);


	}





    public static void main(String[] args) {

    	SparkServer hw = new SparkServer();
    		hw.bootstarp();
    		hw.startApp();
    }

}
