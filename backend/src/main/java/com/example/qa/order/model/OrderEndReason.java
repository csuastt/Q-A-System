package com.example.qa.order.model;

public enum OrderEndReason {
    // 储存时使用编号，添加时务必加在最后
    UNKNOWN, ASKER, ANSWERER, TIME_LIMIT, MESSAGE_LIMIT, SYSTEM
}
