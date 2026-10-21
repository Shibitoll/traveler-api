package com.example.traveler_api.exception;

import lombok.Getter;

@Getter
public class OptimisticLockingConflictException extends RuntimeException {
    private final Integer currentVersion;

    public OptimisticLockingConflictException(String message, Integer currentVersion) {
        super(message);
        this.currentVersion = currentVersion;
    }
}