import React, { useEffect, useState } from "react";
import {
    IMMessage,
    Notification,
    NotificationType,
    OrderInfo,
    UserBasicInfo,
} from "../services/definations";
import imHistoryService from "../services/imHistoryService";
import websocketService from "../services/websocketService";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import { formatTimestamp } from "../util";
import CardContent from "@mui/material/CardContent";
import Markdown from "./Markdown";
import Stack from "@mui/material/Stack";
import { NotifHandlerResult, useNotification } from "./NotificationController";

interface IMMessageListProps {
    orderInfo: OrderInfo;
}

const IMMessageList: React.FC<IMMessageListProps> = (props) => {
    const [msgList, setMsgList] = useState<Array<IMMessage>>();
    const { setNotifHandler, resetNotifHandler } = useNotification();

    const notifHandler = React.useCallback(
        () => (notif: Notification) =>
            notif.type === NotificationType.NEW_MESSAGE &&
            notif.targetId === props.orderInfo.id
                ? NotifHandlerResult.AUTO_READ
                : NotifHandlerResult.PASS,
        [props.orderInfo.id]
    );

    useEffect(() => {
        setNotifHandler(notifHandler);
        return () => {
            resetNotifHandler();
        };
    }, [notifHandler, resetNotifHandler, setNotifHandler]);

    useEffect(() => {
        websocketService.onNewMessage = (newMsg) => {
            const newMsgList = [...msgList!];
            // Find insert position for new message.
            // Because message order is not guaranteed.
            let insertPos = newMsgList.length;
            for (let i = 0; i < newMsgList.length; i++) {
                if (
                    new Date(newMsg.sendTime).getTime() <
                    new Date(newMsgList[i].sendTime).getTime()
                ) {
                    insertPos = i;
                }
            }
            newMsgList.splice(insertPos, 0, newMsg);
            setMsgList(newMsgList);
        };
    }, [msgList]);

    useEffect(() => {
        imHistoryService
            .getOrderIMHistory(props.orderInfo.id)
            .then(setMsgList)
            .then(() => websocketService.subscribeIM(props.orderInfo.id));
        return () => {
            websocketService.unsubscribeIM();
        };
    }, [props.orderInfo.id]);

    const SingleMessage: React.FC<{ msg: IMMessage }> = (subProps) => {
        const isAsker: boolean =
            subProps.msg.senderId === props.orderInfo.asker.id;
        const userInfo: UserBasicInfo = isAsker
            ? props.orderInfo.asker
            : props.orderInfo.answerer;
        return (
            <Card>
                <CardHeader
                    avatar={
                        <Avatar
                            alt={userInfo.username}
                            src={userInfo.avatar}
                            sx={{
                                height: 40,
                                width: 40,
                            }}
                        />
                    }
                    title={userInfo.username}
                    subheader={formatTimestamp(subProps.msg.sendTime)}
                />
                <CardContent>
                    <Markdown value={subProps.msg.msgBody} viewOnly />
                </CardContent>
            </Card>
        );
    };

    return msgList ? (
        <Stack spacing={2}>
            {msgList.map((msg) => (
                <SingleMessage msg={msg} />
            ))}
        </Stack>
    ) : (
        <></>
    );
};

export default IMMessageList;
