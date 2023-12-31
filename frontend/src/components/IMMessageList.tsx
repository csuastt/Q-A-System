import React, { useContext, useEffect, useState } from "react";
import {
    IMMessage,
    Notification,
    NotificationType,
    OrderInfo,
    OrderState,
    UserBasicInfo,
} from "../services/definations";
import imService from "../services/imService";
import websocketService from "../services/websocketService";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import { formatTimestamp } from "../util";
import CardContent from "@mui/material/CardContent";
import Markdown from "./Markdown";
import Stack from "@mui/material/Stack";
import { NotifHandlerResult, useNotification } from "./NotificationController";
import userService from "../services/userService";
import _ from "lodash";
import AuthContext from "../AuthContext";

interface IMMessageListProps {
    orderInfo: OrderInfo;
    onNewMessage?: (msg: IMMessage) => void;
}

const IMMessageList: React.FC<IMMessageListProps> = (props) => {
    const [msgList, setMsgList] = useState<Array<IMMessage>>();
    const { setNotifHandler, resetNotifHandler } = useNotification();
    const { user } = useContext(AuthContext);

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
        const chatEnded =
            props.orderInfo.state === OrderState.CHAT_ENDED ||
            props.orderInfo.state === OrderState.FULFILLED;
        if (chatEnded) {
            return;
        }
        const onNewMessage = props.onNewMessage;
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
            if (onNewMessage) {
                onNewMessage(newMsg);
            }
        };
        return () => {
            websocketService.onNewMessage = () => null;
        };
    }, [msgList, props.onNewMessage, props.orderInfo.state]);

    useEffect(() => {
        const chatEnded =
            props.orderInfo.state === OrderState.CHAT_ENDED ||
            props.orderInfo.state === OrderState.FULFILLED;
        const needSubscribe =
            !chatEnded &&
            (user?.id === props.orderInfo.asker.id ||
                user?.id === props.orderInfo.answerer.id);
        imService
            .getOrderIMHistory(props.orderInfo.id)
            .then(setMsgList)
            .then(() => {
                if (needSubscribe) {
                    websocketService.subscribeIM(props.orderInfo.id);
                }
            });
        return needSubscribe
            ? () => {
                  websocketService.unsubscribeIM();
              }
            : () => null;
    }, [
        props.orderInfo.answerer.id,
        props.orderInfo.asker.id,
        props.orderInfo.id,
        props.orderInfo.state,
        user?.id,
    ]);

    const SingleMessage: React.FC<{ msg: IMMessage }> = React.memo(
        (subProps) => {
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
                                src={userService.getAvatarUrl(userInfo.id)}
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
        },
        (prevProps, nextProps) => _.isEqual(prevProps.msg, nextProps.msg)
    );

    return msgList ? (
        <Stack spacing={2}>
            {msgList.map((msg) => (
                <SingleMessage msg={msg} key={msg.messageId} />
            ))}
        </Stack>
    ) : (
        <></>
    );
};

export default React.memo(IMMessageList);
