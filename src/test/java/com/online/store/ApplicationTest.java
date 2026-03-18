package com.online.store;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApplicationTest {

	@Test
	@DisplayName("Should return Russian greeting from root endpoint")
	void shouldReturnRussianGreeting() {
		Application application = new Application();

		assertEquals("Привет", application.hello());
	}
}
