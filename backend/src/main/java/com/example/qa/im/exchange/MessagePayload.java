package com.example.qa.im.exchange;

import com.example.qa.im.model.Message;

import java.time.ZonedDateTime;
import java.util.Objects;

public record MessagePayload(
        long senderId,
        ZonedDateTime sendTime,
        String msgBody
) {
    public MessagePayload(Message msg) {
        this(Objects.requireNonNull(msg.getSender()).getId(), msg.getSendTime(), msg.getBody());
    }
}
