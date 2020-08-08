package com.fis.casestudy.subscription;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("zuul-gateway")
public interface BooksClient {

	@RequestMapping("/books/books/{id}")
	public Book getBookById(@PathVariable("id") String bookId);
	
	@RequestMapping("/books/books")
	public List<Book> getBookById();

	@PostMapping("/books/books/updateAvailability/{bookId}/{incrementCount}")
	public Book update(@PathVariable("bookId") String bookId,
			@PathVariable("incrementCount") int incrementCount);
	
}
