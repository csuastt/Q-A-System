package com.example.qa.im.exception;

public class NoMatchedOrderException extends IMException {
    public NoMatchedOrderException() {
        super("Cannot find matched order");
    }
}
