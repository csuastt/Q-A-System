package com.example.qa.config;

public final class SystemConfig {
    private SystemConfig() {
    }

    public static final int USERNAME_MIN_LENGTH = 6;
    public static final int USERNAME_MAX_LENGTH = 12;
    public static final int NICKNAME_MIN_LENGTH = 0;
    public static final int NICKNAME_MAX_LENGTH = 30;
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 12;
    public static final int DESCRIPTION_MIN_LENGTH = 0;
    public static final int DESCRIPTION_MAX_LENGTH = 200;
    public static final int PRICE_MIN = 1;
    public static final int PRICE_MAX = 100;
    // RECHARGE_MIN = 1
    public static final int RECHARGE_MAX = 1000;
    public static final int BALANCE_MAX = 10000;

    public static final int QUESTION_MIN_LENGTH = 5;
    public static final int QUESTION_MAX_LENGTH = 100;

    public static final int USER_LIST_MAX_PAGE_SIZE = 50;
    public static final int ORDER_LIST_MAX_PAGE_SIZE = 50;
    public static final int ADMIN_LIST_MAX_PAGE_SIZE = 50;

    public static final int USER_TOKEN_EXPIRATION_MILLISECONDS = 86400000;  // 1 day
    public static final int ADMIN_TOKEN_EXPIRATION_MILLISECONDS = 864000000;  // 10 days
}
