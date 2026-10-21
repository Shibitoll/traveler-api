package com.example.traveler_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HealthCheck")
public class HealthCheck {
    @Schema(example = "healthy")
    private String status;

    @Schema(example = "2026-10-16T21:30:00Z")
    private String timestamp;

    @Schema(example = "120.45")
    private Double uptime;

    public HealthCheck() {
    }

    public HealthCheck(String status, String timestamp, Double uptime) {
        this.status = status;
        this.timestamp = timestamp;
        this.uptime = uptime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Double getUptime() {
        return uptime;
    }

    public void setUptime(Double uptime) {
        this.uptime = uptime;
    }
}