package com.example.fairtrip.exception;

public class NoVendorAvailableException extends RuntimeException {
    public NoVendorAvailableException(String message) {
        super(message);
    }
}
