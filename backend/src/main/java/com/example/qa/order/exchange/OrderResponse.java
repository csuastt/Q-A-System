package com.example.qa.order.exchange;

import com.example.qa.order.model.Order;
import com.example.qa.user.exchange.UserResponse;
import com.example.qa.user.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private UserResponse asker;
    private UserResponse answerer;
    private Order.State state;
    private Boolean finished;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime createTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private ZonedDateTime expireTime;
    private Order.EndReason endReason;
    private String questionTitle;
    private String questionDescription;
    private String answer;
    private Integer price;
    private Boolean showPublic;
    private Integer messageCount;
    private Integer rating;
    private String ratingText;
    private Integer publicPrice;
    private Boolean purchased;

    public OrderResponse(Order order, int level) {
        this(order, level, null);
    }

    public OrderResponse(Order order, int level, User user) {
        this.id = order.getId();
        this.asker = new UserResponse(order.getAsker());
        this.answerer = new UserResponse(order.getAnswerer());
        this.state = order.getState();
        this.finished = order.isFinished();
        this.createTime = order.getCreateTime();
        this.expireTime = order.getExpireTime();
        this.endReason = order.getEndReason();
        this.questionTitle = order.getQuestionTitle();
        this.price = order.getPrice();
        this.showPublic = order.isShowPublic();
        this.messageCount = order.getMessageCount();
        this.rating = order.getRating();
        this.publicPrice = order.getPublicPrice();
        if (this.publicPrice > 0 && user != null) {
            this.purchased = user.getPurchasedOrders().contains(order);
        }
        if (level >= 1) {
            this.questionDescription = order.getQuestionDescription();
            this.answer = order.getAnswer();
            this.ratingText = order.getRatingText();
        }
    }
}
