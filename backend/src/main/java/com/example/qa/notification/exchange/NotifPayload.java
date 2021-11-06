package com.example.qa.notification.exchange;

import com.example.qa.notification.model.Notification;

import java.time.ZonedDateTime;

public record NotifPayload(
        long notifId,
        ZonedDateTime createTime,
        Notification.Type type,
        long receiverId,
        long targetId,
        boolean haveRead,
        String msgSummary
) {
    public NotifPayload(Notification notif) {
        this(notif.getId(), notif.getCreateTime(), notif.getType(), notif.getReceiver().getId(), notif.getTarget().getId(), notif.isHaveRead(), notif.getMsgSummary());
    }
}
