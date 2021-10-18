package com.example.qa.config;

public final class SystemConfig {
    private SystemConfig() {
    }

    public static int USERNAME_MIN_LENGTH = 6;
    public static int USERNAME_MAX_LENGTH = 12;
    public static int NICKNAME_MIN_LENGTH = 6;
    public static int NICKNAME_MAX_LENGTH = 12;
    public static int PASSWORD_MIN_LENGTH = 6;
    public static int PASSWORD_MAX_LENGTH = 12;
    public static int DESCRIPTION_MIN_LENGTH = 6;
    public static int DESCRIPTION_MAX_LENGTH = 200;
    public static int PRICE_MIN = 1;
    public static int PRICE_MAX = 100;
    public static int RECHARGE_MAX = 1000;
    public static int BALANCE_MAX = 10000;

    public static int USER_LIST_MAX_PAGE_SIZE = 50;

    public static int USER_TOKEN_EXPIRATION_MILLISECONDS = 86400000;  // 1 day
    public static int ADMIN_TOKEN_EXPIRATION_MILLISECONDS = 864000000;  // 10 days
}
