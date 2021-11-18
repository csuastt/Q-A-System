package com.example.qa.utils;

public final class ReflectionUtils {
    private ReflectionUtils() {
    }

    public static boolean hasField(Class<?> objectClass, String fieldName) {
        try {
            objectClass.getDeclaredField(fieldName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
