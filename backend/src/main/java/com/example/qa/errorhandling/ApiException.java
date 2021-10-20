package com.example.qa.errorhandling;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public ApiException(HttpStatus status) {
        this(status, null);
    }

    public ApiException(int rawStatusCode) {
        this(rawStatusCode, null);
    }

    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ApiException(int rawStatusCode, String message) {
        this(HttpStatus.valueOf(rawStatusCode), message);
    }
}
