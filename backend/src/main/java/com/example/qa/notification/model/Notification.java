package com.example.qa.notification.model;

import com.example.qa.order.model.Order;
import com.example.qa.order.model.OrderState;
import com.example.qa.user.model.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Table(name = "APP_NOTIF")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull
    private ZonedDateTime createTime;
    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;
    @ManyToOne
    @NotNull
    private User receiver;
    @ManyToOne
    private Order target;
    private boolean haveRead;

    private String msgSummary;
    @Enumerated(EnumType.STRING)
    private OrderState newState;
    private ZonedDateTime deadline;

    public static Notification ofPlain(User receiver, String msg) {
        return Notification.builder()
                .createTime(ZonedDateTime.now())
                .type(Type.PLAIN)
                .receiver(receiver)
                .haveRead(false)
                .msgSummary(msg)
                .build();
    }

    public static Notification ofNewMessage(User receiver, Order target, String msgSummary) {
        return Notification.builder()
                .createTime(ZonedDateTime.now())
                .type(Type.NEW_MESSAGE)
                .receiver(receiver)
                .target(target)
                .haveRead(false)
                .msgSummary(msgSummary)
                .build();
    }

    public static Notification ofOrderStateChanged(User receiver, Order target) {
        return Notification.builder()
                .createTime(ZonedDateTime.now())
                .type(Type.ORDER_STATE_CHANGED)
                .receiver(receiver)
                .target(target)
                .haveRead(false)
                .newState(target.getState())
                .build();
    }

    public static Notification ofDeadlineOrTimeout(User receiver, Order target, Type notifType, ZonedDateTime deadline) {
        if (notifType.ordinal() < 3) {
            throw new IllegalArgumentException();
        }
        return Notification.builder()
                .createTime(ZonedDateTime.now())
                .type(notifType)
                .receiver(receiver)
                .target(target)
                .haveRead(false)
                .deadline(deadline)
                .build();
    }

    public enum Type {
        PLAIN,
        NEW_MESSAGE,
        ORDER_STATE_CHANGED,
        ACCEPT_DEADLINE,
        ACCEPT_TIMEOUT,
        ANSWER_DEADLINE,
        ANSWER_TIMEOUT
    }
}