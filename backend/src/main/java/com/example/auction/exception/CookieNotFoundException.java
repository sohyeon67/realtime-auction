package com.example.auction.exception;

public class CookieNotFoundException extends RuntimeException {

    public CookieNotFoundException() {
        super();
    }

    public CookieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public CookieNotFoundException(String message) {
        super(message);
    }
}
