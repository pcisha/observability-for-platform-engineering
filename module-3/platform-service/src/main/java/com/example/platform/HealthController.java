package com.example.platform;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/healthz")
    public Map<String, String> healthCheck() {
        return Map.of("status", "ok");
    }

    @GetMapping("/readyz")
    public Map<String, String> readinessCheck() {
        return Map.of("status", "ready");
    }
}