package com.fis.casestudy.subscription;

public class SubscriptionNotFoundException extends RuntimeException{
	
	public  SubscriptionNotFoundException(String id) {
	    super("Could not find Subscription " + id);
	  } 

}
