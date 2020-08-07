package com.fis.casestudy.subscription;

public class BookNotAvailableException extends RuntimeException{
	
	public  BookNotAvailableException(String id) {
	    super("Book not available" + id);
	  } 

}
