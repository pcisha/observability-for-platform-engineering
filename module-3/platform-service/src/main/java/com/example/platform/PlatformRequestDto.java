package com.example.platform;

/**
 * Data Transfer Object for incoming platform requests.
 * All fields are optional - defaults will be applied for missing values.
 */
public record PlatformRequestDto(
    String type,        // new_environment, dashboard, custom_pipeline, debug_help
    String urgency,     // low, medium, high
    String team,        // payments, recommendations, search, devops
    String title,       // Brief description of the request
    String description  // Detailed description
) {}