package com.example.qa.order.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderState {
    // 储存时使用编号，添加时务必加在最后
    CREATED(false),
    PAYED(false),
    PAY_TIMEOUT(true),
    REVIEWED(false),
    REJECTED_BY_REVIEWER(true),
    ACCEPTED(false),
    REJECTED_BY_ANSWERER(true),
    RESPOND_TIMEOUT(true),
    ANSWERED(false),
    ANSWER_TIMEOUT(true),
    CHAT_ENDED(true),
    FULFILLED(true);

    @Getter
    private final boolean finished;
}
