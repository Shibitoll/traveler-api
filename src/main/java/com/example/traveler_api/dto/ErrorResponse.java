package com.example.traveler_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ErrorResponse", description = "Standard error response")
public class ErrorResponse {
    @Schema(example = "Resource not found")
    private String error;

    @Schema(example = "2026-10-16T21:30:00Z")
    private String timestamp;

    public ErrorResponse() {
    }

    public ErrorResponse(String error, String timestamp) {
        this.error = error;
        this.timestamp = timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}