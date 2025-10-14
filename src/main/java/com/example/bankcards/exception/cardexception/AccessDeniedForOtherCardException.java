package com.example.bankcards.exception.cardexception;

public class AccessDeniedForOtherCardException extends RuntimeException {
    public AccessDeniedForOtherCardException(String message) {
        super(message);
    }
}
