package com.example.qa.order.model;

import lombok.Getter;

public enum OrderState {
    // 储存时使用编号，添加时务必加在最后
    CREATED(true),
    PAYED(true),
    PAY_TIMEOUT(false),
    REVIEWED(true),
    REJECTED_BY_REVIEWER(false),
    ACCEPTED(true),
    REJECTED_BY_ANSWERER(false),
    RESPOND_TIMEOUT(false),
    ANSWERED(true),
    ANSWER_TIMEOUT(false),
    CHAT_ENDED(false),
    FULFILLED(false);

    @Getter
    private final boolean active;

    private OrderState(boolean active) {
        this.active = active;
    }
}
