package com.example.qa.im.exchange;

import com.example.qa.im.model.Message;
import org.apache.commons.text.StringEscapeUtils;

import java.time.ZonedDateTime;
import java.util.StringJoiner;

public record MessagePayload(
        long messageId,
        long senderId,
        ZonedDateTime sendTime,
        String msgBody
) {
    public MessagePayload(Message msg) {
        this(msg.getId(), msg.getSender() == null ? -1 : msg.getSender().getId(), msg.getSendTime(), msg.getBody());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MessagePayload.class.getSimpleName() + "[", "]")
                .add("messageId=" + messageId)
                .add("senderId=" + senderId)
                .add("sendTime=" + sendTime)
                .add("msgBody='" + StringEscapeUtils.escapeJava(msgBody) + "'")
                .toString();
    }
}
