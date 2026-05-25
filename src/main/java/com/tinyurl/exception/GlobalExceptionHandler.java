package com.tinyurl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            RateLimitExceededException.class
    )
    public ResponseEntity<?> handleRateLimitException(
            RateLimitExceededException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(
                        Map.of(
                                "error",
                                ex.getMessage()
                        )
                );
    }
}