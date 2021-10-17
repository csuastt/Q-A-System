package com.example.qa.config;

public class SystemConfig {
    public static int USERNAME_MIN_LENGTH = 6;
    public static int USERNAME_MAX_LENGTH = 12;
    public static int NICKNAME_MIN_LENGTH = 6;
    public static int NICKNAME_MAX_LENGTH = 12;
    public static int PASSWORD_MIN_LENGTH = 6;
    public static int PASSWORD_MAX_LENGTH = 12;

    public static int USER_TOKEN_EXPIRATION_MILLISECONDS = 86400000;  // 1 day
    public static int ADMIN_TOKEN_EXPIRATION_MILLISECONDS = 864000000;  // 10 days

    public SystemConfig() {
        throw new IllegalStateException();
    }
}
