package com.fis.casestudy.subscription;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("books")
public interface BooksClient {

	@RequestMapping("/books/{id}")
	Book getBookById(@PathVariable("id") String bookId);
	
	@RequestMapping("/books")
	List<Book> getBookById();

	@PostMapping("/books/updateAvailability/{bookId}/{incrementCount}")
	public Book update(@PathVariable("bookId") String bookId,
			@PathVariable("incrementCount") int incrementCount);
	
}
