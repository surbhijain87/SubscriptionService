package com.fis.casestudy.subscription;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SubscriptionController {

	private SubscriptionRepository repository;

	private RestTemplate restTemplate;
	
	@Value("${books.service.path}")
	private String bookServicePath; 
	
	public SubscriptionController(SubscriptionRepository repository, RestTemplate restTemplate) {
		super();
		this.repository = repository;
		this.restTemplate = restTemplate;
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
		Subscription subscription = new Subscription(request.getName(), request.getBookId(), Instant.now(), null);
		
		//decrease availability by calling book service
		callBookService(subscription.getBookId() , -1);

		
		return repository.save(subscription);
	}

	@PostMapping("returns")
	Subscription returnSubscriptions(@RequestBody Subscription subscription) {

		//Increase availability by calling book service
		callBookService(subscription.getBookId() , 1);
		return repository.save(subscription);
	}

	
	private Book callBookService(String bookId , int incrementCount) {
		String url = bookServicePath + "/books/updateAvailability/"+ bookId + "/" + incrementCount;
		return restTemplate.postForObject(url, null, Book.class);
	}
}
