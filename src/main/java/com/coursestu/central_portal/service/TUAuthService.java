package com.coursestu.central_portal.service;

import com.coursestu.central_portal.dto.TULoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class TUAuthService {

    @Value("${tu.api.url}")
    private String tuApiUrl;

    @Value("${tu.api.key}")
    private String tuApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public TULoginResponse authenticate(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("application-key", tuApiKey);

        Map<String, String> body = new HashMap<>();
        body.put("UserName", username);
        body.put("PassWord", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<TULoginResponse> response = restTemplate.postForEntity(
            tuApiUrl, request, TULoginResponse.class
        );

        return response.getBody();
    }
}