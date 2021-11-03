package com.example.qa.order.model;

import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.user.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "APP_ORDER")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private boolean deleted = false;
    @ManyToOne
    private User asker;
    @ManyToOne
    private User answerer;
    private OrderState state = OrderState.CREATED;
    @Setter(AccessLevel.NONE)
    private boolean finished = false;
    private boolean reviewed = false;
    private ZonedDateTime createTime;
    private ZonedDateTime expireTime;
    private OrderEndReason endReason = OrderEndReason.UNKNOWN;
    private String question;
    private String answerSummary;
    private int price;

    public void setState(OrderState state) {
        if (state != null) {
            this.state = state;
            finished = state.isFinished();
        }
    }

    // 传 data 前先用 checkOrderData 检查
    public Order(OrderRequest data, User asker, User answerer, boolean allProperties) {
        this.asker = asker;
        this.answerer = answerer;
        question = data.getQuestion();
        createTime = ZonedDateTime.now();
        price = answerer.getPrice();
        if (allProperties) {
            setState(data.getState());
            endReason = Objects.requireNonNullElse(data.getEndReason(), endReason);
            answerSummary = data.getAnswerSummary();
            price = Objects.requireNonNullElse(data.getPrice(), price);
        }
    }

    // 传 data 前先用 checkOrderData 检查，仅限管理员，默认所有修改
    public void update(OrderRequest data) {
        setState(data.getState());
        endReason = Objects.requireNonNullElse(data.getEndReason(), endReason);
        question = Objects.requireNonNullElse(data.getQuestion(), question);
        price = Objects.requireNonNullElse(data.getPrice(), price);
    }
}
