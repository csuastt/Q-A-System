package com.example.qa.errorhandling;

import org.springframework.http.HttpStatus;

public class MessageException extends RuntimeException {

    public MessageException() {
    }

    public MessageException(String message) {
        super(message);
    }

    public MessageException(HttpStatus status) {
        super(status.getReasonPhrase());
    }

    // Reuse HttpStatus to express common exception category
    public MessageException(HttpStatus status, String message) {
        super(status.getReasonPhrase() + ": " + message);
    }
}
