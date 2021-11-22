package com.example.qa.order.model;

import com.example.qa.order.exchange.OrderRequest;
import com.example.qa.user.model.User;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    private State state = State.CREATED;
    @Setter(AccessLevel.NONE)
    private boolean finished = false;
    @Setter(AccessLevel.NONE)
    private boolean visibleToAnswerer = false;
    private boolean reviewed = false;
    private ZonedDateTime createTime;
    private ZonedDateTime expireTime;
    private ZonedDateTime notifyTime;
    private EndReason endReason = EndReason.UNKNOWN;
    private String questionTitle;
    @Lob
    @Type(type = "text")
    private String questionDescription;
    @Lob
    @Type(type = "text")
    private String answer;
    private int price;
    private boolean showPublic = false;
    private int messageCount = 0;
    private int rating = 0;

    @ElementCollection
    private List<Attachment> attachmentList;

    @ElementCollection
    private List<UUID> pics;

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

    public void setState(State state) {
        if (state != null) {
            this.state = state;
            finished = state.isFinished();
            if (state == State.CANCELLED && reviewed) {
                visibleToAnswerer = true;
            } else {
                visibleToAnswerer = state.isVisibleToAnswerer();
            }
        }
    }

    public void setExpireTime(ZonedDateTime expireTime) {
        this.expireTime = expireTime;
        if (state == State.REVIEWED || state == State.ACCEPTED) {
            this.notifyTime = expireTime.minusHours(1);
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

    @RequiredArgsConstructor
    public enum State {
        // 储存时使用编号，添加时务必加在最后
        CREATED(false, false),
        REVIEWED(false, true),
        REJECTED_BY_REVIEWER(true, false),
        ACCEPTED(false, true),
        REJECTED_BY_ANSWERER(true, true),
        RESPOND_TIMEOUT(true, true),
        ANSWERED(false, true),
        ANSWER_TIMEOUT(true, true),
        CHAT_ENDED(true, true),
        FULFILLED(true, true),
        CANCELLED(true, false);

        @Getter
        private final boolean finished;
        @Getter
        private final boolean visibleToAnswerer;

        public static final List<State> completedOrderStates = List.of(State.CHAT_ENDED, State.FULFILLED);
    }

    public enum EndReason {
        // 储存时使用编号，添加时务必加在最后
        UNKNOWN, ASKER, ANSWERER, TIME_LIMIT, MESSAGE_LIMIT, SYSTEM
    }
}
