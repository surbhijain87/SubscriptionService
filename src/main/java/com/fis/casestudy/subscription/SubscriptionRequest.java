package com.fis.casestudy.subscription;

import java.time.Instant;

public class SubscriptionRequest {
	private String name;
	private String bookId;
	private String notify;
	
	
	public SubscriptionRequest(String name, String bookId, String notify) {
		this.name = name;
		this.bookId = bookId;
		this.notify = notify;
	}
	
	public String getName() {
		return name;
	}
	public String getBookId() {
		return bookId;
	}
	public String getNotify() {
		return notify;
	}

}
