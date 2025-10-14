package com.example.bankcards.exception.cardexception;

public class CardNumberAlreadyExistException extends RuntimeException {
    public CardNumberAlreadyExistException(String message) {
        super(message);
    }
}
