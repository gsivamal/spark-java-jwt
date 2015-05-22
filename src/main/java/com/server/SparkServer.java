package com.server;

import static spark.Spark.*;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Pattern;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.security.User;

import spark.ModelAndView;
import spark.Request;
import spark.Session;
import spark.template.freemarker.FreeMarkerEngine;

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
		
		before("/", (req, resp) -> {

				String token =	req.headers("authorization");
			
				System.out.println("Token in Before:"+token);
					
				//may be first time
				if(token == null){
					resp.redirect("/login");
				}else{
					System.out.println("Continue to request page...");
				}		
			}
		);
		
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
			
			if(email.equals("admin") && password.equals("admin")){
				//Generate token
				//Token format:[header.payload.signature]
				HashMap<String, Object> claims = new HashMap<String, Object>();
					claims.put("email", email);					
				String token = signer.sign(claims, new JWTSigner.Options().setIssuedAt(true));
				
				//resp.header("authorization",token);
				resp.redirect("/dashboard",301); // moved permanently		
				
				tokenDb.put(email, token);
				
				//return new ModelAndView(null, "index.ftl");
			}else{
			   halt(401, "Un Authorized!!!");
			}
			return 301;// salai check this later...
		});
		
		
		after((req, resp) -> {
			
			//set token only after login valid, after gets called everytime...:O(
			String token =	resp.raw().getHeader("authorization");
			
			String req_token =	req.headers("authorization");
			
			System.out.println("Token in after:"+token);
			System.out.println("Token in req after:"+req_token);
				
				//to verify
				//Map<String, Object> decoded = verifier.verify(token);
				//long iat = ((Number) decoded.get("iat")).longValue();
				
				//System.out.println("Token generated:"+token);				
				
				//resp.header("authorization", token);
		});
		
		get("/dashboard", (req, resp) -> {
			
			String token =	resp.raw().getHeader("authorization");
			
			System.out.println("Token in dashboard:"+token);
			
			 return new ModelAndView(null, "index.ftl");
		}, templateEngine);
		
		
	}

	private String getToken(Request req) throws SecurityException {
		 String token = null;
		 final String authorizationHeader = req.headers("authorization");
		 if (authorizationHeader == null) {
			 throw new SecurityException("Unauthorized: No Authorization header was found");
		 }
		 
		 String[] parts = authorizationHeader.split(" ");
		 if (parts.length != 2) {
			 throw new SecurityException("Unauthorized: Format is Authorization: Bearer [token]");
		 }
		 
		 String scheme = parts[0];
		 String credentials = parts[1];
		 
		 Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
		 if (pattern.matcher(scheme).matches()) {
			 token = credentials;
		 }
		 return token;
	 }		
	 
	 
	
    public static void main(String[] args) {
    	
    	SparkServer hw = new SparkServer();
    		hw.bootstarp();    	
    		hw.startApp();
    }
    
}
