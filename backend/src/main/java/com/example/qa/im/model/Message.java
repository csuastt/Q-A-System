package com.example.qa.im.model;

import com.example.qa.order.model.Order;
import com.example.qa.user.model.User;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Table(name = "APP_MESSAGE")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private ZonedDateTime sendTime;
    @ManyToOne(optional = false)
    private Order order;
    /**
     * Sender of this message.
     * <code>null</code> if this message is from system
     */
    @Nullable
    @ManyToOne
    private User sender;
    private String body;
}