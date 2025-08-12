package com.example.platform;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for PlatformRequest entities.
 * 
 * Provides CRUD operations and custom queries for analyzing
 * platform request patterns and metrics.
 */
@Repository
public interface PlatformRequestRepository extends JpaRepository<PlatformRequest, Long> {
    
    // Basic finders
    List<PlatformRequest> findByTeam(String team);
    List<PlatformRequest> findByType(String type);
    List<PlatformRequest> findByUrgency(String urgency);
    List<PlatformRequest> findByPlatformResponse(String platformResponse);
    List<PlatformRequest> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Analytics queries
    @Query("SELECT COUNT(p) FROM PlatformRequest p WHERE p.team = ?1")
    long countByTeam(String team);
    
    @Query("SELECT AVG(p.timeToResponseMs) FROM PlatformRequest p WHERE p.type = ?1")
    Double averageResponseTimeByType(String type);
}