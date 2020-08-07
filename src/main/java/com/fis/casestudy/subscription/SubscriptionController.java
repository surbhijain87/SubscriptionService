package com.fis.casestudy.subscription;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@RestController

public class SubscriptionController {

	private SubscriptionRepository repository;

	@Autowired
	private BooksClient bookClient;

	public SubscriptionController(SubscriptionRepository repository) {
		super();
		this.repository = repository;
	}

	@GetMapping("subscriptions")
	List<Subscription> getSubscriptions(@RequestParam(required = false) String subscriberName) {
		System.out.println(subscriberName);
		if (StringUtils.isEmpty(subscriberName)) {
			return repository.findAll();
		} else {
			Subscription subscription = repository.findById(subscriberName)
					.orElseThrow(() -> new SubscriptionNotFoundException(subscriberName));
			return Collections.singletonList(subscription);
		}
	}

	@PostMapping("subscriptions")
	Subscription addSubscriptions(@RequestBody SubscriptionRequest request) {

		Book book = queryBookService(request.getBookId());

		System.out.println(book.toString());

		if (book.getAvailable() == 0) {
			// book not available
			throw new BookNotAvailableException(book.getBookId() + " " + book.getName());
		}

		Subscription subscription = new Subscription(request.getName(), request.getBookId(), Instant.now(), null);
		// decrease availability by calling book service
		updateBookService(subscription.getBookId(), -1);

		return repository.save(subscription);
	}

	@PostMapping("subscriptions/returns")
	Subscription returnSubscriptions(@RequestBody Subscription subscription) {
		subscription.setReturnDate(Instant.now());
		// Increase availability by calling book service
		updateBookService(subscription.getBookId(), 1);
		return repository.save(subscription);
	}

	private Book updateBookService(String bookId, int incrementCount) {
		return bookClient.update(bookId, incrementCount);
	}

	private Book queryBookService(String bookId) {
		return bookClient.getBookById(bookId);
	}
}
