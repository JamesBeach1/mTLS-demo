package org.example.controller;

import org.example.service.ProjectBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TlsController {

    private static final Logger log = LoggerFactory.getLogger(TlsController.class);

    private final ProjectBService projectBService;

    public TlsController(ProjectBService projectBService) {
        this.projectBService = projectBService;
    }

    @GetMapping("/hello")
    public String hello() {
        log.info("Received hello request in Project-B");
        return "Hello from Project-B";
    }

    @GetMapping("/data")
    public String getData() {
        log.info("Providing data from Project-B");
        return "Data from Project-B at " + java.time.LocalDateTime.now();
    }

    @GetMapping("/call-project-a")
    public String callProjectA() {
        log.info("Calling Project-A from Project-B");
        try {
            String response = projectBService.callProjectA();
            log.info("Successfully received response from Project-A: {}", response);
            return "Project-B received: " + response;
        } catch (Exception e) {
            log.error("Error calling Project-A", e);
            return "Error calling Project-A: " + e.getMessage();
        }
    }

    @PostMapping("/process")
    public String processData(@RequestBody String data) {
        log.info("Processing data in Project-B: {}", data);
        return "Project-B processed: " + data.toUpperCase();
    }
}