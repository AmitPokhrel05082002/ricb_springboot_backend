package bt.ricb.ricb_api.util;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiClient {
	private final RestTemplate restTemplate = new RestTemplate();

	public String consumeApi(String apiUrl) {
		ResponseEntity<String> response = this.restTemplate.getForEntity(apiUrl, String.class, new Object[0]);
		if (response.getStatusCode().is2xxSuccessful()) {
			return (String) response.getBody();
		}
		throw new RuntimeException("Failed to consume API: " + response.getStatusCode());
	}
}
