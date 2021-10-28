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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private Long id;
    private Boolean deleted;
    private UserResponse asker;
    private UserResponse answerer;
    private OrderState state;
    private Boolean finished;
    // @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private ZonedDateTime createTime;
    private OrderEndReason endReason;
    private String question;
    private String answerSummary;
    private Integer price;

    public OrderResponse(Order order) {
        this(order, 0);
    }

    public OrderResponse(Order order, int level) {
        this.id = order.getId();
        this.asker = new UserResponse(order.getAsker());
        this.answerer = new UserResponse(order.getAnswerer());
        this.state = order.getState();
        this.finished = order.isFinished();
        this.createTime = order.getCreateTime();
        this.endReason = order.getEndReason();
        this.question = order.getQuestion();
        this.price = order.getPrice();
        if (level >= 1) {
            this.answerSummary = order.getAnswerSummary();
        }
        if (level >= 2) {
            this.deleted = order.isDeleted();
        }
    }
}
