package com.example.qa.errorhandling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException e) {
        if (e.getMessage() == null) {
            return new ResponseEntity<>(null, new HttpHeaders(), e.getStatus());
        } else {
            return new ResponseEntity<>(new ApiError(e.getMessage()), new HttpHeaders(), e.getStatus());
        }
    }
}
