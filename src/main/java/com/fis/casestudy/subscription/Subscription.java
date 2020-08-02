package com.fis.casestudy.subscription;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Subscription {

	@Id
	private String name;
	private String bookId;
	private Instant subscriptionDate;
	private Instant returnDate;
	
	
	@SuppressWarnings("unused")
	private Subscription() {
		super();
	}


	@Override
	public String toString() {
		return "Susbcription [name=" + name + ", bookId=" + bookId + ", subscriptionDate=" + subscriptionDate
				+ ", returnDate=" + returnDate + "]";
	}


	public String getName() {
		return name;
	}


	public Subscription(String name, String bookId, Instant subscriptionDate, Instant returnDate) {
		super();
		this.name = name;
		this.bookId = bookId;
		this.subscriptionDate = subscriptionDate;
		this.returnDate = returnDate;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getBookId() {
		return bookId;
	}


	public void setBookId(String bookId) {
		this.bookId = bookId;
	}


	public Instant getSubscriptionDate() {
		return subscriptionDate;
	}


	public void setSubscriptionDate(Instant subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}


	public Instant getReturnDate() {
		return returnDate;
	}


	public void setReturnDate(Instant returnDate) {
		this.returnDate = returnDate;
	}
}
