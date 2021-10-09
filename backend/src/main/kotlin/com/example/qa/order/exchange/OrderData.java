package com.example.qa.order.exchange;

import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;

import java.sql.Date;

public class OrderData {
    private long asker;
    private long answerer;
    private OrderState state;
    private boolean active;
    private Date createTime;
    private OrderEndReason endReason;
    private String question;
}
