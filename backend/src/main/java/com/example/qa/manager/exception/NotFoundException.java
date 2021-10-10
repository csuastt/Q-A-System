package com.example.qa.manager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotFoundException extends Throwable {
    public NotFoundException(String msg) {
        super(msg);
    }
}
