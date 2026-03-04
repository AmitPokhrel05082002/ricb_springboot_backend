package bt.ricb.ricb_api.controllers;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
@CrossOrigin({ "*" })
public class CustomerController {

    @Value("${external.customer.api}")
    private String externalCustomerApi;
    
    @Value("${external.soa.api}")
    private String externalSoaApi;
    
    @Value("${external.bg.status.api}")
    private String externalBgStatusApi;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/customer/{cid}")
    public ResponseEntity<?> getCustomerDetails(@PathVariable String cid, @RequestHeader("Authorization") String authHeader) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    externalCustomerApi + "/" + cid,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer Not Found");
        }
    }
    
    
    @PostMapping("/customer/soa")
    public ResponseEntity<?> getCustomerSOA(@RequestBody Map<String, String> payload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ Add Basic Authentication header
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString("ugrodev:Pass@123".getBytes());
        headers.set("Authorization", basicAuth);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    externalSoaApi,
                    HttpMethod.POST,
                    requestEntity,
                    Object.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("SOA Details Not Found or External API Failed");
        }
    }
    
    @PostMapping("/customer/bg-status")
    public ResponseEntity<?> validateBGStatus(@RequestBody Map<String, String> payload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // ✅ Basic Authentication
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString("ugrodev:Pass@123".getBytes());
        headers.set("Authorization", basicAuth);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    externalBgStatusApi,
                    HttpMethod.POST,
                    requestEntity,
                    Object.class
            );

            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("BG Status Not Found or External API Failed");
        }
    }



}

