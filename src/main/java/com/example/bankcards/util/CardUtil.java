package com.example.bankcards.util;

/**
 * Утилитный класс для работы с банковскими картами.
 * Содержит методы для маскировки номера карты и других операций с PAN.
 */
public class CardUtil {

    private CardUtil() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Маскирует номер карты, оставляя видимыми только последние 4 цифры.
     *
     * @param cardNumber номер карты (должен содержать 16 цифр)
     * @return замаскированный номер карты в формате "************1234",
     *         или исходное значение, если номер некорректный
     */
    public static String maskPan(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return cardNumber;
        }
        return "************" + cardNumber.substring(12);
    }
}
