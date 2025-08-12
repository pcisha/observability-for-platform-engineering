package com.example.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for handling platform requests.
 * 
 * Provides endpoints for creating platform feature requests with optional
 * parameters for testing latency and error conditions.
 */
@RestController
@RequestMapping("/requests")
public class PlatformRequestController {

    private static final Logger logger = LoggerFactory.getLogger(PlatformRequestController.class);
    private final PlatformRequestService platformRequestService;

    public PlatformRequestController(@Autowired PlatformRequestService platformRequestService) {
        this.platformRequestService = platformRequestService;
    }

    /**
     * Creates a new platform request.
     * 
     * @param requestDto the request details (optional, defaults applied for missing fields)
     * @param latency_ms optional latency simulation in milliseconds
     * @param error optional flag to simulate an error response
     * @return the processed request with generated ID and response
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createRequest(
            @RequestBody(required = false) PlatformRequestDto requestDto,
            @RequestParam(required = false) Long latency_ms,
            @RequestParam(required = false, defaultValue = "false") boolean error) {

        try {
            PlatformRequestService.PlatformRequestResult result = 
                platformRequestService.processRequest(requestDto, latency_ms, error);

            Map<String, Object> response = Map.of(
                "id", result.id(),
                "team", result.team(),
                "type", result.type(),
                "urgency", result.urgency(),
                "platform_response", result.platformResponse(),
                "time_to_response_ms", result.timeToResponseMs(),
                "comment", result.comment()
            );

            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Failed to process request", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}