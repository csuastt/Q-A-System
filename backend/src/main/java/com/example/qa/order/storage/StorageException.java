package com.example.qa.order.storage;

import java.io.IOException;

public class StorageException extends RuntimeException {

    public StorageException(String message){
        super(message);
    }

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
