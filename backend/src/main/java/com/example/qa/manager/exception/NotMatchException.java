package com.example.qa.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class NotMatchException extends Throwable {
    public NotMatchException(String msg) {
        super(msg);
    }
}
