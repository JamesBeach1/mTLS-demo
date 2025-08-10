package org.example.controller;

import org.example.service.ProjectAService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TlsController {

    private static final Logger log = LoggerFactory.getLogger(TlsController.class);

    private final ProjectAService projectAService;

    public TlsController(ProjectAService projectAService) {
        this.projectAService = projectAService;
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("Received hello request in Project-A");
        return "Hello from Project-A";
    }

    @GetMapping("/call-project-b")
    public String callProjectB() {
        log.info("Calling Project-B from Project-A");
        try {
            String response = projectAService.callProjectB();
            log.info("Successfully received response from Project-B: {}", response);
            return "Project-A received: " + response;
        } catch (Exception e) {
            log.error("Error calling Project-B", e);
            return "Error calling Project-B: " + e.getMessage();
        }
    }

    @PostMapping("/data")
    public String receiveData(@RequestBody String data) {
        log.info("Received data in Project-A: {}", data);
        return "Project-A processed: " + data;
    }
}
