package com.example.bankcards.util;

public class CardUtil {
    public static String maskPan(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        return "************" + cardNumber.substring(12);
    }

}

