package com.example.qa.order.exchange;

import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OrderData {
    private long id;
    private boolean deleted;
    private long asker;
    private long answerer;
    private OrderState state;
    private boolean finished;
    private LocalDateTime createTime;
    private OrderEndReason endReason;
    private String question;
    private int price;

    public OrderData(Order order) {
        this.id = order.getId();
        this.deleted = order.isDeleted();
        this.asker = order.getAsker().getId();
        this.answerer = order.getAnswerer().getId();
        this.state = order.getState();
        this.finished = order.isFinished();
        this.createTime = order.getCreateTime();
        this.endReason = order.getEndReason();
        this.question = order.getQuestion();
        this.price = order.getPrice();
    }
}
