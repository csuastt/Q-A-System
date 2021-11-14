import { useLocation } from "react-router-dom";
import {
    Notification,
    NotificationType,
    OrderStateMsg,
} from "./services/definations";

export function timestampToDate(timestamp: number): Date {
    return new Date(timestamp * 1000);
}

const dateTimeFormat = Intl.DateTimeFormat("zh-CN", {
    dateStyle: "short",
    timeStyle: "short",
});

export function formatTimestamp(timestamp: string): string {
    return dateTimeFormat.format(new Date(timestamp));
}

export function useQuery() {
    return new URLSearchParams(useLocation().search);
}

export function parseIntWithDefault(
    str: string | null | undefined,
    def: number
): number {
    if (str === null || str === undefined || isNaN(parseInt(str))) {
        return def;
    }
    return parseInt(str);
}

export function describeNotification(notif: Notification) {
    switch (notif.type) {
        case NotificationType.PLAIN:
            return notif.msgSummary;
        case NotificationType.NEW_MESSAGE:
            return `您编号为${notif.targetId}的订单有新的未读消息`;
        case NotificationType.ORDER_STATE_CHANGED:
            return `您编号为${
                notif.targetId
            }的订单的状态变为：${OrderStateMsg.get(notif.newState!)}`;
        case NotificationType.ACCEPT_DEADLINE:
            return `编号为${notif.targetId}的订单即将超时，请尽快接单`;
        case NotificationType.ACCEPT_TIMEOUT:
            return `编号为${notif.targetId}的订单超时未接单`;
        case NotificationType.ANSWER_DEADLINE:
            return `编号为${notif.targetId}的订单即将超时，请尽快回答`;
        case NotificationType.ANSWER_TIMEOUT:
            return `编号为${notif.targetId}的订单超时未回答`;
    }
}
