package com.example.qa.notification.exchange;

import com.example.qa.notification.model.Notification;
import com.example.qa.order.model.Order;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.ZonedDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotifPayload(
        long notifId,
        ZonedDateTime createTime,
        Notification.Type type,
        long receiverId,
        long targetId,
        boolean haveRead,
        String msgSummary,
        Order.State newState,
        ZonedDateTime deadline
) {
    public NotifPayload(Notification notif) {
        this(notif.getId(),
                notif.getCreateTime(),
                notif.getType(),
                notif.getReceiver().getId(),
                notif.getTarget() == null ? 0 : notif.getTarget().getId(),
                notif.isHaveRead(),
                notif.getMsgSummary(),
                notif.getNewState(),
                notif.getDeadline());
    }
}
