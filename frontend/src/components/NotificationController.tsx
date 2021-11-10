import React, { useContext, useEffect, useState } from "react";
import { SnackbarKey, useSnackbar, VariantType } from "notistack";
import {
    Notification,
    NotificationType,
    OrderState,
    OrderStateMsg,
} from "../services/definations";
import websocketService from "../services/websocketService";
import Button from "@mui/material/Button";
import { useHistory } from "react-router-dom";

export enum NotifHandlerResult {
    PASS,
    MUTE,
    AUTO_READ,
}

export type NotifHandler = (notif: Notification) => NotifHandlerResult;

interface NotificationControllerContextType {
    setNotifFilter: (filter: NotifHandler) => void;
    unreadCount: number;
    setUnreadCount: (count: number) => void;
}

export const NotificationControllerContext =
    React.createContext<NotificationControllerContextType>({
        setNotifFilter: () => null,
        unreadCount: -1,
        setUnreadCount: () => null,
    });

export function useNotification(): NotificationControllerContextType {
    return useContext(NotificationControllerContext);
}

const NotificationController: React.FC<{ wsAvailable: boolean }> = (props) => {
    const { enqueueSnackbar, closeSnackbar } = useSnackbar();
    const routerHistory = useHistory();
    const [unreadCount, setUnreadCount] = useState(0);
    const [notifHandler, setNotifHandler] = useState<NotifHandler>(
        () => () => NotifHandlerResult.PASS
    );

    const redirect = React.useCallback(
        (to: string) => routerHistory.push(to),
        [routerHistory]
    );

    const enqueueNotification = React.useCallback(
        (notif: Notification) => {
            const ToOrderAction: React.FC<{
                orderId: number;
                snackbarKey: SnackbarKey;
            }> = (props) => (
                <Button
                    onClick={() => {
                        closeSnackbar(props.snackbarKey);
                        redirect(`/orders/${props.orderId}`);
                    }}
                >
                    查看
                </Button>
            );

            const successOrderState: Array<OrderState> = [
                OrderState.CREATED,
                OrderState.REVIEWED,
                OrderState.ACCEPTED,
                OrderState.ANSWERED,
                OrderState.FULFILLED,
            ];
            const warningOrderState: Array<OrderState> = [
                OrderState.PAY_TIMEOUT,
                OrderState.REJECTED_BY_REVIEWER,
                OrderState.REJECTED_BY_ANSWERER,
                OrderState.RESPOND_TIMEOUT,
                OrderState.ANSWER_TIMEOUT,
            ];
            const getSnackbarVariant = (state: OrderState): VariantType =>
                successOrderState.indexOf(state) >= 0
                    ? "success"
                    : warningOrderState.indexOf(state) >= 0
                    ? "warning"
                    : "info";

            switch (notif.type) {
                case NotificationType.PLAIN:
                    enqueueSnackbar(notif.msgSummary, {
                        action: (key) => (
                            <Button onClick={() => closeSnackbar(key)}>
                                OK
                            </Button>
                        ),
                    });
                    break;
                case NotificationType.NEW_MESSAGE:
                    console.log("new message");
                    enqueueSnackbar(
                        `您编号为${notif.targetId}的订单有新的未读消息`,
                        {
                            variant: "info",
                            action: (key) => (
                                <ToOrderAction
                                    orderId={notif.targetId}
                                    snackbarKey={key}
                                />
                            ),
                        }
                    );
                    break;
                case NotificationType.ORDER_STATE_CHANGED:
                    enqueueSnackbar(
                        `您编号为${
                            notif.targetId
                        }的订单的状态变为：${OrderStateMsg.get(
                            notif.newState!
                        )}`,
                        {
                            variant: getSnackbarVariant(notif.newState!),
                            action: (key) => (
                                <ToOrderAction
                                    orderId={notif.targetId}
                                    snackbarKey={key}
                                />
                            ),
                        }
                    );
                    break;
                case NotificationType.ACCEPT_DEADLINE:
                    enqueueSnackbar(
                        `编号为${notif.targetId}的订单即将超时，请尽快接单`,
                        {
                            variant: "warning",
                            action: (key) => (
                                <ToOrderAction
                                    orderId={notif.targetId}
                                    snackbarKey={key}
                                />
                            ),
                        }
                    );
                    break;
                case NotificationType.ACCEPT_TIMEOUT:
                    enqueueSnackbar(`编号为${notif.targetId}的订单超时未接单`, {
                        variant: "error",
                        action: (key) => (
                            <ToOrderAction
                                orderId={notif.targetId}
                                snackbarKey={key}
                            />
                        ),
                    });
                    break;
                case NotificationType.ANSWER_DEADLINE:
                    enqueueSnackbar(
                        `编号为${notif.targetId}的订单即将超时，请尽快回答`,
                        {
                            variant: "warning",
                            action: (key) => (
                                <ToOrderAction
                                    orderId={notif.targetId}
                                    snackbarKey={key}
                                />
                            ),
                        }
                    );
                    break;
                case NotificationType.ANSWER_TIMEOUT:
                    enqueueSnackbar(`编号为${notif.targetId}的订单超时未回答`, {
                        variant: "error",
                        action: (key) => (
                            <ToOrderAction
                                orderId={notif.targetId}
                                snackbarKey={key}
                            />
                        ),
                    });
                    break;
            }
        },
        [closeSnackbar, enqueueSnackbar, redirect]
    );

    useEffect(() => {
        websocketService.onNewNotification = (notif) => {
            const res = notifHandler(notif);
            switch (res) {
                case NotifHandlerResult.PASS:
                    enqueueNotification(notif);
                    return false;
                case NotifHandlerResult.MUTE:
                    return false;
                case NotifHandlerResult.AUTO_READ:
                    return true;
            }
        };
    }, [enqueueNotification, notifHandler, props.wsAvailable]);

    return (
        <NotificationControllerContext.Provider
            value={{
                setNotifFilter: setNotifHandler,
                unreadCount: unreadCount,
                setUnreadCount: setUnreadCount,
            }}
        >
            {props.children}
        </NotificationControllerContext.Provider>
    );
};

export default NotificationController;
