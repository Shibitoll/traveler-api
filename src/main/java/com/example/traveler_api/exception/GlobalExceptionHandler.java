package com.example.traveler_api.exception;

import com.example.traveler_api.dto.ConflictResponse;
import com.example.traveler_api.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            msg = "Validation error";
        } else if (!msg.toLowerCase().contains("validation error")) {
            msg = "Validation error: " + msg;
        }

        ErrorResponse response = new ErrorResponse(
                msg,
                OffsetDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
                ex.getMessage() != null ? ex.getMessage() : "Resource not found",
                OffsetDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(OptimisticLockingConflictException.class)
    public ResponseEntity<ConflictResponse> handleConflict(OptimisticLockingConflictException ex) {
        ConflictResponse response = new ConflictResponse(
                ex.getMessage() != null ? ex.getMessage() : "Conflict: Travel plan was modified by another user",
                ex.getCurrentVersion()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse response = new ErrorResponse(
                "Internal server error",
                OffsetDateTime.now().toString()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}