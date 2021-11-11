import React, {
    Dispatch,
    SetStateAction,
    useContext,
    useEffect,
    useState,
} from "react";
import { SnackbarKey, useSnackbar, VariantType } from "notistack";
import {
    Notification,
    NotificationType,
    OrderState,
} from "../services/definations";
import websocketService from "../services/websocketService";
import Button from "@mui/material/Button";
import { useHistory } from "react-router-dom";
import notificationService from "../services/notificationService";
import AuthContext from "../AuthContext";
import { describeNotification } from "../util";

export enum NotifHandlerResult {
    PASS,
    MUTE,
    AUTO_READ,
}

export type NotifHandler = (notif: Notification) => NotifHandlerResult;

interface NotificationControllerContextType {
    setNotifHandler: Dispatch<SetStateAction<NotifHandler>>;
    resetNotifHandler: () => void;
    unreadCount: number;
    setUnreadCount: (count: number) => void;
}

export const NotificationControllerContext =
    React.createContext<NotificationControllerContextType>({
        setNotifHandler: () => null,
        resetNotifHandler: () => null,
        unreadCount: -1,
        setUnreadCount: () => null,
    });

export function useNotification(): NotificationControllerContextType {
    return useContext(NotificationControllerContext);
}

const NotificationController: React.FC<{ wsAvailable: boolean }> = (props) => {
    const { enqueueSnackbar, closeSnackbar } = useSnackbar();
    const routerHistory = useHistory();
    const { user } = useContext(AuthContext);
    const [unreadCount, setUnreadCount] = useState(0);
    const [notifHandler, setNotifHandler] = useState<NotifHandler>(
        () => () => NotifHandlerResult.PASS
    );

    const resetNotifHandler = React.useCallback(
        () => setNotifHandler(() => () => NotifHandlerResult.PASS),
        []
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

            const notifStr = describeNotification(notif);
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
                    enqueueSnackbar(notifStr, {
                        variant: "info",
                        action: (key) => (
                            <ToOrderAction
                                orderId={notif.targetId}
                                snackbarKey={key}
                            />
                        ),
                    });
                    break;
                case NotificationType.ORDER_STATE_CHANGED:
                    enqueueSnackbar(notifStr, {
                        variant: getSnackbarVariant(notif.newState!),
                        action: (key) => (
                            <ToOrderAction
                                orderId={notif.targetId}
                                snackbarKey={key}
                            />
                        ),
                    });
                    break;
                case NotificationType.ACCEPT_DEADLINE:
                    enqueueSnackbar(notifStr, {
                        variant: "warning",
                        action: (key) => (
                            <ToOrderAction
                                orderId={notif.targetId}
                                snackbarKey={key}
                            />
                        ),
                    });
                    break;
                case NotificationType.ACCEPT_TIMEOUT:
                    enqueueSnackbar(notifStr, {
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
                    enqueueSnackbar(notifStr, {
                        variant: "warning",
                        action: (key) => (
                            <ToOrderAction
                                orderId={notif.targetId}
                                snackbarKey={key}
                            />
                        ),
                    });
                    break;
                case NotificationType.ANSWER_TIMEOUT:
                    enqueueSnackbar(notifStr, {
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
        if (user) {
            notificationService
                .getUnreadCount(user.id)
                .then((res) => setUnreadCount(res.count));
        }
    }, [user]);

    useEffect(() => {
        websocketService.onNewNotification = (notif) => {
            const res = notifHandler(notif);
            switch (res) {
                case NotifHandlerResult.PASS:
                    enqueueNotification(notif);
                    setUnreadCount(unreadCount + 1);
                    return false;
                case NotifHandlerResult.MUTE:
                    setUnreadCount(unreadCount + 1);
                    return false;
                case NotifHandlerResult.AUTO_READ:
                    return true;
            }
        };
    }, [enqueueNotification, notifHandler, props.wsAvailable, unreadCount]);

    return (
        <NotificationControllerContext.Provider
            value={{
                setNotifHandler: setNotifHandler,
                resetNotifHandler: resetNotifHandler,
                unreadCount: unreadCount,
                setUnreadCount: setUnreadCount,
            }}
        >
            {props.children}
        </NotificationControllerContext.Provider>
    );
};

export default NotificationController;
