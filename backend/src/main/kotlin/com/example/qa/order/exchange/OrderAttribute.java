package com.example.qa.order.exchange;

import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

// for POST requests

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderAttribute {
    private long asker;
    private long answerer;
    private OrderState state;
    private boolean active;
    private LocalDateTime createTime;
    private OrderEndReason endReason;
    private String question;
}
