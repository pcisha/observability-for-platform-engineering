package com.example.platform;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class PlatformRequestService {

    private static final Logger logger = LoggerFactory.getLogger(PlatformRequestService.class);
    private static final Random random = new Random();

    // Configuration arrays
    private static final String[] TEAMS = {"payments", "recommendations", "search", "devops"};
    private static final String[] TYPES = {"new_environment", "dashboard", "custom_pipeline", "debug_help"};
    private static final String[] URGENCY_LEVELS = {"low", "medium", "high"};
    private static final String[] RESPONSES = {"received", "delivered", "rejected", "needs_info"};
    private static final String[] COMMENTS = {
        "Have you tried turning it off and on again?",
        "Auto-approved by the coffee machine ☕.",
        "We'll add it to the backlog — right behind the other 437 items.",
        "Okay, but only because you bribed us with cookies.",
        "We'll pretend this never happened.",
        "Budget approved by the CFO's cat."
    };

    private final Tracer tracer;
    private final LongCounter requestsCounter;
    private final LongHistogram responseTimeHistogram;
    private final PlatformRequestRepository repository;

    public PlatformRequestService(@Autowired PlatformRequestRepository repository) {
        this.repository = repository;
        
        // Initialize OpenTelemetry
        this.tracer = GlobalOpenTelemetry.getTracer("platform-request-api");
        Meter meter = GlobalOpenTelemetry.getMeter("platform-request-api");
        
        this.requestsCounter = meter
            .counterBuilder("platform_requests_total")
            .setDescription("Total number of platform requests")
            .build();
        
        this.responseTimeHistogram = meter
            .histogramBuilder("time_to_initial_response_seconds")
            .setDescription("Time to initial response in milliseconds")
            .setUnit("ms")
            .ofLongs()
            .build();
    }

    public PlatformRequestResult processRequest(PlatformRequestDto requestDto, Long latencyMs, boolean simulateError) {
        Span span = tracer.spanBuilder("platform.request.create").startSpan();
        
        try {
            // Handle null input
            if (requestDto == null) {
                requestDto = new PlatformRequestDto(null, null, null, null, null);
            }
            
            // Validate latency parameter
            if (latencyMs != null && latencyMs < 0) {
                throw new IllegalArgumentException("Latency cannot be negative");
            }
            
            // Apply defaults for missing fields
            String team = getOrDefault(requestDto.team(), TEAMS);
            String type = getOrDefault(requestDto.type(), TYPES);
            String urgency = getOrDefault(requestDto.urgency(), URGENCY_LEVELS);
            String title = requestDto.title() != null ? requestDto.title() : "Platform request";
            String description = requestDto.description() != null ? requestDto.description() : "Request description";

            // Add trace attributes
            span.setAttribute("request.team", team);
            span.setAttribute("request.type", type);
            span.setAttribute("request.urgency", urgency);

            // Simulate processing time
            long actualLatency = latencyMs != null ? latencyMs : generateRandomLatency();
            simulateProcessingTime(actualLatency);

            // Handle error simulation
            if (simulateError) {
                span.setStatus(StatusCode.ERROR);
                logger.error("request_failed team={} type={} urgency={} title=\"{}\"", team, type, urgency, title);
                throw new RuntimeException("Simulated error");
            }

            // Generate response
            String response = getRandomElement(RESPONSES);
            String requestId = generateRequestId();
            String comment = getRandomElement(COMMENTS);
            
            span.setAttribute("platform.response", response);

            // Record metrics
            recordMetrics(team, type, urgency, response, actualLatency);

            // Save to database
            PlatformRequest entity = new PlatformRequest(requestId, team, type, urgency, title, description, response, actualLatency, comment);
            repository.save(entity);

            // Log success
            logger.info("request_processed team={} type={} urgency={} response={} latency={}ms title=\"{}\"",
                team, type, urgency, response, actualLatency, title);

            return new PlatformRequestResult(requestId, team, type, urgency, response, actualLatency, comment);

        } finally {
            span.end();
        }
    }

    private String getOrDefault(String value, String[] options) {
        return value != null ? value : getRandomElement(options);
    }

    private String getRandomElement(String[] array) {
        return array[random.nextInt(array.length)];
    }

    private long generateRandomLatency() {
        return 120 + random.nextInt(3081); // 120-3200ms
    }

    private String generateRequestId() {
        return "rq-" + String.format("%05d", random.nextInt(100000));
    }

    private void simulateProcessingTime(long latencyMs) {
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Processing interrupted", e);
        }
    }

    private void recordMetrics(String team, String type, String urgency, String response, long latencyMs) {
        Attributes attributes = Attributes.of(
            AttributeKey.stringKey("type"), type,
            AttributeKey.stringKey("urgency"), urgency,
            AttributeKey.stringKey("team"), team,
            AttributeKey.stringKey("response"), response
        );
        
        requestsCounter.add(1, attributes);
        responseTimeHistogram.record(latencyMs, attributes);
    }

    public record PlatformRequestResult(
        String id,
        String team,
        String type,
        String urgency,
        String platformResponse,
        Long timeToResponseMs,
        String comment
    ) {}
}