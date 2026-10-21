package com.example.traveler_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ConflictResponse", description = "Optimistic locking conflict response")
public class ConflictResponse {
    @Schema(example = "Conflict: Travel plan was modified by another user")
    private String error;

    @JsonProperty("current_version")
    @Schema(example = "2")
    private Integer currentVersion;

    public ConflictResponse() {
    }

    public ConflictResponse(String error, Integer currentVersion) {
        this.error = error;
        this.currentVersion = currentVersion;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Integer getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(Integer currentVersion) {
        this.currentVersion = currentVersion;
    }
}