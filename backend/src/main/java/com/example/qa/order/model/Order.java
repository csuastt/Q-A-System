package com.example.qa.order.model;

import com.example.qa.order.exchange.OrderEditData;
import com.example.qa.user.model.AppUser;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "APP_ORDER")
public class Order {
    @Id
    @GeneratedValue
    private long id;
    private boolean deleted = false;
    @ManyToOne
    private AppUser asker;
    @ManyToOne
    private AppUser answerer;
    private OrderState state = OrderState.CREATED;
    @Setter(AccessLevel.NONE)
    private boolean finished = false;
    private ZonedDateTime createTime;
    // TODO: 其他时间参数或者事件记录
    // TODO: 聊天记录条数
    private OrderEndReason endReason = OrderEndReason.UNKNOWN;
    private String question;
    // TODO：回答（用于问答库）
    private int price;

    public void setState(OrderState state) {
        if (state != null) {
            this.state = state;
            finished = state.isFinished();
        }
    }

    // 传 data 前先用 checkOrderData 检查
    public Order(OrderEditData data, @NonNull AppUser asker, @NonNull AppUser answerer, boolean allProperties) {
        this.asker = asker;
        this.answerer = answerer;
        question = data.getQuestion();
        createTime = ZonedDateTime.now();
        price = answerer.getPrice();
        if (allProperties) {
            setState(data.getState());
            endReason = data.getEndReason() != null ? data.getEndReason() : endReason;
            price = data.getPrice() != null && data.getPrice() > 0 ? data.getPrice() : price;
        }
    }

    // 传 data 前先用 checkOrderData 检查，仅限管理员，默认所有修改
    public void update(OrderEditData data, @NonNull AppUser asker, @NonNull AppUser answerer) {
        this.asker = asker;
        this.answerer = answerer;
        setState(data.getState());
        endReason = data.getEndReason() != null ? data.getEndReason() : endReason;
        question = data.getQuestion() != null ? data.getQuestion() : question;
        price = data.getPrice() != null && data.getPrice() > 0 ? data.getPrice() : price;
    }
}
