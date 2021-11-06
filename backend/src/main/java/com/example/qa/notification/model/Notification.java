package com.example.qa.notification.model;

import com.example.qa.order.model.Order;
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