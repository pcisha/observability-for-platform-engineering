package com.example.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Platform Request Service - A demo application for platform engineering observability.
 * 
 * This service simulates a platform team receiving and processing feature requests from development teams.
 * It demonstrates comprehensive observability with:
 * - OpenTelemetry metrics, traces, and logs
 * - PostgreSQL database integration
 * - Containerized deployment
 */
@SpringBootApplication
public class PlatformRequestApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlatformRequestApplication.class, args);
    }
}