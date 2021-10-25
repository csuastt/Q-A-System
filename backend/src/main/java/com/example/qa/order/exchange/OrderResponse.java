package com.example.qa.order.exchange;

import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderEndReason;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.exchange.UserResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class OrderResponse {
    private long id;
    private boolean deleted;
    private UserResponse asker;
    private UserResponse answerer;
    private OrderState state;
    private boolean finished;
    // @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private ZonedDateTime createTime;
    private OrderEndReason endReason;
    private String question;
    private int price;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.deleted = order.isDeleted();
        this.asker = new UserResponse(order.getAsker());
        this.answerer = new UserResponse(order.getAnswerer());
        this.state = order.getState();
        this.finished = order.isFinished();
        this.createTime = order.getCreateTime();
        this.endReason = order.getEndReason();
        this.question = order.getQuestion();
        this.price = order.getPrice();
    }
}
