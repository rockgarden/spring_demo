package com.example.restservicecors;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();

	/**
	 * This @CrossOrigin annotation enables cross-origin resource sharing only for
	 * this specific method. By default, its allows all origins, all headers, and
	 * the HTTP methods specified in the @RequestMapping annotation.
	 * 
	 * @param name
	 * @return
	 */
	@CrossOrigin(origins = "http://localhost:9000")
	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(required = false, defaultValue = "World") String name) {
		System.out.println("==== in greeting ====");
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

	/**
	 * The difference between the greetingWithJavaconfig method and the greeting
	 * method (used in the controller-level CORS configuration) is the route
	 * (/greeting-javaconfig rather than /greeting) and the presence of
	 * the @CrossOrigin origin.
	 * 
	 * @param name
	 * @return
	 */
	@GetMapping("/greeting-javaconfig")
	public Greeting greetingWithJavaconfig(@RequestParam(required = false, defaultValue = "World") String name) {
		System.out.println("==== in greeting ====");
		return new Greeting(counter.incrementAndGet(), String.format(template, name));
	}

}
