package com.coursestu.central_portal.service;

import com.coursestu.central_portal.dto.StudentDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class TUService {

    private final String TU_API_URL = "https://restapi.tu.ac.th/api/v2/profile/std/info/?id=";
    private final String APP_KEY = "TU628f012c9f8e1e46a1c0310ba8beb11a5c968cc908552332642be042a23a653ea0cecb786099f31b7570af74a6049264"; 

    public StudentDTO getStudentInfo(String studentId) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Application-Key", APP_KEY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<StudentDTO> response = restTemplate.exchange(
                TU_API_URL + studentId,
                HttpMethod.GET,
                entity,
                StudentDTO.class
            );
            return response.getBody();
        } catch (Exception e) {
            System.out.println("Error calling TU API: " + e.getMessage());
            return null;
        }
    }
}