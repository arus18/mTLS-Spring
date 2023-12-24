package com.mtls.mtlsspring;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class BadSSLClient {

    private final RestTemplate restTemplate;

    @Autowired
    public BadSSLClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/send-request")
    public ResponseEntity<String> sendRequest() {
        String url = "https://client.badssl.com/";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response;
    }
}
