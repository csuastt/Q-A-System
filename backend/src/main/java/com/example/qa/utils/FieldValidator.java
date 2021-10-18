package com.example.qa.utils;

public final class FieldValidator {

    private FieldValidator() {
    }

    public static boolean length(String string, int minLength, int maxLength) {
        return string != null && minLength < string.length() && string.length() < maxLength;
    }

    public static boolean lengthIfNotNull(String string, int minLength, int maxLength) {
        return string == null || length(string, minLength, maxLength);
    }

    public static boolean value(Integer integer, int min, int max) {
        return integer != null && min < integer && integer < max;
    }

    public static boolean valueIfNotNull(Integer integer, int min, int max) {
        return integer == null || value(integer, min, max);
    }
}
