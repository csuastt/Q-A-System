package com.example.qa.errorhandling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException e) {
        if (e.getReason() == null) {
            return new ResponseEntity<>(null, e.getResponseHeaders(), e.getStatus());
        } else {
            return new ResponseEntity<>(new ApiError(e.getReason()), e.getResponseHeaders(), e.getStatus());
        }
    }
}
