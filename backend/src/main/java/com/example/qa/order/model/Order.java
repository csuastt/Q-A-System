package com.example.qa.order.model;

import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.user.model.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
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
    @Setter(AccessLevel.NONE)
    private boolean visibleToAnswerer = false;
    private boolean reviewed = false;
    private ZonedDateTime createTime;
    private ZonedDateTime expireTime;
    private OrderEndReason endReason = OrderEndReason.UNKNOWN;
    private String questionTitle;
    @Lob
    @Type(type = "text")
    private String questionDescription;
    @Lob
    @Type(type = "text")
    private String answer;
    private int price;
    private boolean showPublic = false;

    @ElementCollection
    private List<Attachment> attachmentList;

    // 传 data 前先用 checkOrderData 检查
    public Order(OrderRequest data, User asker, User answerer, boolean allProperties) {
        this.asker = asker;
        this.answerer = answerer;
        questionTitle = data.getTitle();
        questionDescription = data.getDescription();
        createTime = ZonedDateTime.now();
        price = answerer.getPrice();
        showPublic = Objects.requireNonNullElse(data.getShowPublic(), false);
        if (allProperties) {
            setState(data.getState());
            endReason = Objects.requireNonNullElse(data.getEndReason(), endReason);
            answer = data.getAnswer();
            price = Objects.requireNonNullElse(data.getPrice(), price);
        }
    }

    public void setState(OrderState state) {
        if (state != null) {
            this.state = state;
            finished = state.isFinished();
            if (state == OrderState.CANCELLED && reviewed) {
                visibleToAnswerer = true;
            } else {
                visibleToAnswerer = state.isVisibleToAnswerer();
            }
        }
    }

    // 传 data 前先用 checkOrderData 检查，仅限管理员，默认所有修改
    public void update(OrderRequest data) {
        setState(data.getState());
        endReason = Objects.requireNonNullElse(data.getEndReason(), endReason);
        questionTitle = Objects.requireNonNullElse(data.getTitle(), questionTitle);
        questionDescription = Objects.requireNonNullElse(data.getDescription(), questionDescription);
        price = Objects.requireNonNullElse(data.getPrice(), price);
    }
}
