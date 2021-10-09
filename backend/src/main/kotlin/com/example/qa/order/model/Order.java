package com.example.qa.order.model;

import com.example.qa.user.model.AppUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "APP_ORDER")
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private boolean deleted = false;
    @ManyToOne
    private AppUser asker;
    @ManyToOne
    private AppUser answerer;
    private OrderState state = OrderState.CREATED;
    private boolean active = true;
    private LocalDateTime createTime;
    // TODO: 其他时间参数或者事件记录
    // TODO: 聊天记录条数
    private OrderEndReason endReason = OrderEndReason.UNKNOWN;
    private String question;
    // TODO：回答（用于问答库）
    // TODO: 价格
    // private int price;

}
