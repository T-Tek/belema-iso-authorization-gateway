package com.belema_fintech.util;

public class CardUtil {
    public static String maskPan(String pan) {
        if (pan == null) {
            return "******";
        }

        int panLength = pan.length();

        if (panLength < 10) {
            return "******";
        }

        String firstSixDigits = pan.substring(0, 6);
        String lastFourDigits = pan.substring(panLength - 4);
        int numberOfMaskedCharacters = panLength - 10;
        StringBuilder maskedSectionBuilder = new StringBuilder();

        for (int i = 0; i < numberOfMaskedCharacters; i++) {
            maskedSectionBuilder.append("*");
        }

        String maskedSection = maskedSectionBuilder.toString();

        return firstSixDigits + maskedSection + lastFourDigits;
    }
}
