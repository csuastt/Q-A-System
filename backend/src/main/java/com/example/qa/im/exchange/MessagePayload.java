package com.example.qa.im.exchange;

import com.example.qa.im.model.Message;

import java.time.ZonedDateTime;

public record MessagePayload(
        long messageId,
        long senderId,
        ZonedDateTime sendTime,
        String msgBody
) {
    public MessagePayload(Message msg) {
        this(msg.getId(), msg.getSender() == null ? -1 : msg.getSender().getId(), msg.getSendTime(), msg.getBody());
    }
}
