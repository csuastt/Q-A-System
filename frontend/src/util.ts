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
        case NotificationType.ANSWERER_APPLICATION_PASSED:
            return "您的回答者申请已通过";
        case NotificationType.ANSWERER_APPLICATION_REJECTED:
            return "您的回答者申请被驳回";
    }
}

// size: the size of the file, unit: Byte
// pointLength: decimal places
export function formatSize(size: number, pointLength: number | undefined) {
    let unit;
    let units = ["B", "K", "M", "G", "TB"];
    while ((unit = units.shift()) && size > 1024) {
        size = size / 1024;
    }
    return (
        (unit === "B"
            ? size.toString()
            : size.toFixed(pointLength === undefined ? 2 : pointLength)) + unit
    );
}

// seconds: interval, unit (seconds)
export function formatInterval(seconds: number) {
    let interval = seconds / 31536000;

    if (interval > 1) {
        return Math.floor(interval) + "年";
    }
    interval = seconds / 2592000;
    if (interval > 1) {
        return Math.floor(interval) + "月";
    }
    interval = seconds / 86400;
    if (interval > 1) {
        return Math.floor(interval) + "天";
    }
    interval = seconds / 3600;
    if (interval > 1) {
        return Math.floor(interval) + "小时";
    }
    interval = seconds / 60;
    if (interval > 1) {
        return Math.floor(interval) + "分钟";
    }
    return Math.floor(seconds) + "秒";
}

export function checkSensitiveWords(text: string) {
    let words: Array<string> = require("./sensitive_word.json");
    console.log(text);
    let dirty = "";
    words.forEach((word) => {
        if (text.indexOf(word) !== -1 && dirty.length === 0) dirty = word;
    });
    return dirty;
}
