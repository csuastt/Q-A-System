package com.example.qa.errorhandling;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ApiException extends ResponseStatusException {
    public ApiException(HttpStatus status) {
        super(status);
    }

    public ApiException(int rawStatusCode) {
        super(HttpStatus.valueOf(rawStatusCode));
    }

    public ApiException(HttpStatus status, String reason) {
        super(status, reason);
    }

    public ApiException(int rawStatusCode, String reason) {
        super(HttpStatus.valueOf(rawStatusCode), reason);
    }
}
