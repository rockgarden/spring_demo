package com.example.resthateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @RestController annotation is present on the class, an implicit @ResponseBody
 *                 annotation is added to the greeting method. This causes
 *                 Spring MVC to render the returned HttpEntity and its payload
 *                 (the Greeting) directly to the response.
 */
@RestController
public class GreetingController {

	private static final String TEMPLATE = "Hello, %s!";

	/**
	 * 
	 * Both linkTo(…) and methodOn(…) are static methods on ControllerLinkBuilder
	 * that let you fake a method invocation on the controller. The returned
	 * LinkBuilder will have inspected the controller method’s mapping annotation to
	 * build up exactly the URI to which the method is mapped.
	 * 
	 * The call to withSelfRel() creates a Link instance that you add to the
	 * Greeting representation model.
	 * 
	 * @RequestParam binds the value of the query string parameter name into the
	 *               name parameter of the greeting() method. This query string
	 *               parameter is implicitly not required because of the use of the
	 *               defaultValue attribute. If it is absent in the request, the
	 *               defaultValue of World is used.
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping("/greeting")
	public HttpEntity<Greeting> greeting(@RequestParam(value = "name", defaultValue = "World") String name) {

		Greeting greeting = new Greeting(String.format(TEMPLATE, name));
		greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());

		return new ResponseEntity<>(greeting, HttpStatus.OK);
	}
}
