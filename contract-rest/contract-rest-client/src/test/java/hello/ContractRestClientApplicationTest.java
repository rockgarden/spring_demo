package hello;

import org.assertj.core.api.BDDAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.junit.StubRunnerRule;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContractRestClientApplicationTest {

	@Rule
	public StubRunnerRule stubRunnerRule = new StubRunnerRule()
			.downloadStub("com.example", "contract-rest-service", "0.0.1-SNAPSHOT", "stubs").withPort(8000)
			.stubsMode(StubRunnerProperties.StubsMode.LOCAL);

	/**
	 * If use localhost:8100, we get error:
	 * org.springframework.web.client.ResourceAccessException: I/O error on GET
	 * request for "http://localhost:8100/person/1": Connection refused (Connection
	 * refused); nested exception is java.net.ConnectException: Connection refused
	 * (Connection refused)
	 * 
	 * Other is equal error.
	 */
	@Test
	public void get_person_from_service_contract() {
		// given:
		RestTemplate restTemplate = new RestTemplate();

		// when:
		ResponseEntity<Person> personResponseEntity = restTemplate.getForEntity("http://localhost:8000/person/1",
				Person.class);

		// then:
		BDDAssertions.then(personResponseEntity.getStatusCodeValue()).isEqualTo(200);
		BDDAssertions.then(personResponseEntity.getBody().getId()).isEqualTo(1l);
		BDDAssertions.then(personResponseEntity.getBody().getName()).isEqualTo("foo");
		BDDAssertions.then(personResponseEntity.getBody().getSurname()).isEqualTo("bee");

	}
}
