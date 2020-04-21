package com.example.testingweb;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * @SpringBootTest annotation tells Spring Boot to look for a main configuration
 *                 class (one with `@SpringBootApplication`, for instance) and
 *                 use that to start a Spring application context.
 */
@SpringBootTest
/**
 * @AutoConfigureMockMvc the full Spring application context is started but
 *                       without the server, only test the layer below that,
 *                       where Spring handles the incoming HTTP request and
 *                       hands it off to your controller. That way, almost of
 *                       the full stack is used, and your code will be called in
 *                       exactly the same way as if it were processing a real
 *                       HTTP request
 */
@AutoConfigureMockMvc
public class TestingWebApplicationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void shouldReturnDefaultMessage() throws Exception {
		this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Hello, World")));
	}
}
