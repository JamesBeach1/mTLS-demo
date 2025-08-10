package org.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProjectBService {

    private final RestTemplate restTemplate;
    private final Logger log = LoggerFactory.getLogger(ProjectBService.class);

    public ProjectBService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callProjectA() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "https://localhost:8443/api/data",
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling project-A", e);
            throw e;
        }
    }

}
