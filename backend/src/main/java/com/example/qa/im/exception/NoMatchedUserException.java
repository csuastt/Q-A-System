package com.example.qa.im.exception;

public class NoMatchedUserException extends IMException {
    public NoMatchedUserException() {
        super("Message sender is not matched to current login user nor order's participant");
    }
}
