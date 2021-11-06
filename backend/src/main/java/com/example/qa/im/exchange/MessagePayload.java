package com.example.qa.im.exchange;

import com.example.qa.im.model.Message;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
public class MessagePayload {
    private long senderId;
    private ZonedDateTime sendTime;
    private String msgBody;

    public MessagePayload(Message msg) {
        this.senderId = Objects.requireNonNull(msg.getSender()).getId();
        this.sendTime = msg.getSendTime();
        this.msgBody = msg.getBody();
    }
}
