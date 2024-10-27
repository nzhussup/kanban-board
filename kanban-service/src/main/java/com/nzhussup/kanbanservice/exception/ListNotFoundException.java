package com.nzhussup.kanbanservice.exception;

public class ListNotFoundException extends RuntimeException {
    public ListNotFoundException(String message) {
        super(message);
    }

    public ListNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

