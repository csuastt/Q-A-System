package com.example.qa.order.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderState {
    // 储存时使用编号，添加时务必加在最后
    CREATED(false, false),
    PAYED(false, false),
    PAY_TIMEOUT(true, false),
    REVIEWED(false, true),
    REJECTED_BY_REVIEWER(true, false),
    ACCEPTED(false, true),
    REJECTED_BY_ANSWERER(true, true),
    RESPOND_TIMEOUT(true, true),
    ANSWERED(false, true),
    ANSWER_TIMEOUT(true, true),
    CHAT_ENDED(true, true),
    FULFILLED(true, true),
    CANCELLED(true, false);

    @Getter
    private final boolean finished;
    @Getter
    private final boolean visibleToAnswerer;
}
