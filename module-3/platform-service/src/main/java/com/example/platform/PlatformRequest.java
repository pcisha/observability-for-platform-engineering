package com.example.platform;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_requests")
public class PlatformRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;
    
    @Column(nullable = false)
    private String team;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String urgency;
    
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "platform_response", nullable = false)
    private String platformResponse;
    
    @Column(name = "time_to_response_ms", nullable = false)
    private Long timeToResponseMs;
    
    @Column(length = 500)
    private String comment;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    public PlatformRequest() {
        this.createdAt = LocalDateTime.now();
    }
    
    public PlatformRequest(String requestId, String team, String type, String urgency, 
                          String title, String description, String platformResponse, 
                          Long timeToResponseMs, String comment) {
        this();
        this.requestId = requestId;
        this.team = team;
        this.type = type;
        this.urgency = urgency;
        this.title = title;
        this.description = description;
        this.platformResponse = platformResponse;
        this.timeToResponseMs = timeToResponseMs;
        this.comment = comment;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    
    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPlatformResponse() { return platformResponse; }
    public void setPlatformResponse(String platformResponse) { this.platformResponse = platformResponse; }
    
    public Long getTimeToResponseMs() { return timeToResponseMs; }
    public void setTimeToResponseMs(Long timeToResponseMs) { this.timeToResponseMs = timeToResponseMs; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}