package com.example.qa.im.exception;

public class NoMessageBodyException extends IMException {
    public NoMessageBodyException() {
        super("Message body not found");
    }
}
