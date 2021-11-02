package com.example.qa.config;

import lombok.Getter;

import java.util.Objects;

public class SystemConfig {
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
    // RECHARGE_MIN = 1
    public static final int RECHARGE_MAX = 1000;
    public static final int BALANCE_MAX = 10000;

    public static final int QUESTION_MIN_LENGTH = 5;
    public static final int QUESTION_MAX_LENGTH = 100;

    public static final int USER_LIST_MAX_PAGE_SIZE = 50;
    public static final int ORDER_LIST_MAX_PAGE_SIZE = 50;
    public static final int ADMIN_LIST_MAX_PAGE_SIZE = 50;

    public static final long USER_TOKEN_EXPIRATION_MILLISECONDS = 86400000;  // 1 day
    public static final long ADMIN_TOKEN_EXPIRATION_MILLISECONDS = 864000000;  // 10 days

    @Getter
    private static int minPrice = 1;
    @Getter
    private static int maxPrice = 100;
    private static long respondExpirationMilliseconds = 259200000;  // 3 days
    private static long answerExpirationMilliseconds = 86400000;    // 1 day
    private static long fulfillExpirationMilliseconds = 259200000;  // 3 days
    private static int maxChatMessages = 9999;              // unlimited, ≥ 2
    private static long maxChatTimeMilliseconds = 604800000;  // 7 days (after answering)

    public static Configurable getConfigurable() {
        Configurable result = new Configurable();
        result.setMinPrice(minPrice);
        result.setMaxPrice(maxPrice);
        result.setRespondExpirationMilliseconds(respondExpirationMilliseconds);
        result.setAnswerExpirationMilliseconds(answerExpirationMilliseconds);
        result.setFulfillExpirationMilliseconds(fulfillExpirationMilliseconds);
        result.setMaxChatMessages(maxChatMessages);
        result.setMaxChatTimeMilliseconds(maxChatTimeMilliseconds);
        return result;
    }

    public static void updateConfig(Configurable configurable) {
        minPrice = Objects.requireNonNullElse(configurable.getMinPrice(), minPrice);
        maxPrice = Objects.requireNonNullElse(configurable.getMaxPrice(), maxPrice);
        respondExpirationMilliseconds = Objects.requireNonNullElse(configurable.getRespondExpirationMilliseconds(), respondExpirationMilliseconds);
        answerExpirationMilliseconds = Objects.requireNonNullElse(configurable.getAnswerExpirationMilliseconds(), answerExpirationMilliseconds);
        fulfillExpirationMilliseconds = Objects.requireNonNullElse(configurable.getFulfillExpirationMilliseconds(), fulfillExpirationMilliseconds);
        maxChatMessages = Objects.requireNonNullElse(configurable.getMaxChatMessages(), maxChatMessages);
        maxChatTimeMilliseconds = Objects.requireNonNullElse(configurable.getMaxChatTimeMilliseconds(), maxChatTimeMilliseconds);
    }
}
