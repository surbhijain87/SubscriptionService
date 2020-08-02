package com.fis.casestudy.subscription;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriptionController {

	private SubscriptionRepository repository;

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
		
		Subscription subscription = new Subscription(request.getName(), request.getBookId(), Instant.now(), null);
		return repository.save(subscription);
	}

	@PostMapping("returns")
	Subscription returnSubscriptions(@RequestBody Subscription subscription) {
		//Update book service
		return repository.save(subscription);
	}

}
