package com.fis.casestudy.subscription;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class SubscriptionController {

	
	static Map<String , String> userNamePasswordMap = new HashMap<>();
	
	private SubscriptionRepository repository;

	@Autowired
	private BooksClient bookClient;

	@Autowired
	private KafkaTemplate<String, String> template;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	public SubscriptionController(SubscriptionRepository repository) {
		super();
		this.repository = repository;
		userNamePasswordMap.put("John", "password123");
		userNamePasswordMap.put("Bob", "password12378");
	}

	@RequestMapping(value = "subscriptions/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		final String token = jwtTokenUtil.generateToken("username");
		return ResponseEntity.ok(new JwtResponse(token));
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

			if (request.getNotify().equals("yes")) {
				String topic = "NotificationTopic" + book.getBookId();

				System.out.println("Add to notifications" + topic);

				template.send(topic, request.getName());
			}
			throw new BookNotAvailableException(book.getBookId() + " " + book.getName());
		}

		Subscription subscription = new Subscription(request.getName(), request.getBookId(), Instant.now(), null);
		// decrease availability by calling book service
		Book b = updateBookService(subscription.getBookId(), -1);

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
	
	private void authenticate(String username, String password) throws Exception {
		
		if(!userNamePasswordMap.containsKey(username)) {
			throw new Exception("INVALID_CREDENTIALS", new AuthenticationException("Username doesnt exist"));
		}
		
		if(!userNamePasswordMap.get(username).equals(password)) {
			throw new Exception("INVALID_CREDENTIALS", new AuthenticationException("Invalid password"));
		}
	}
}
