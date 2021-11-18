import AuthContext from "../AuthContext";
import React, { useContext, useEffect, useState } from "react";
import { NotifHandlerResult, useNotification } from "./NotificationController";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import ListItemButton from "@mui/material/ListItemButton";

import HourglassEmptyIcon from "@mui/icons-material/HourglassEmpty";
import InfoIcon from "@mui/icons-material/Info";
import ChatIcon from "@mui/icons-material/Chat";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import CancelIcon from "@mui/icons-material/Cancel";
import AlarmIcon from "@mui/icons-material/Alarm";
import DoneAllIcon from "@mui/icons-material/DoneAll";
import TimerOffIcon from "@mui/icons-material/TimerOff";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import Skeleton from "@mui/material/Skeleton";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";
import {
    Notification,
    NotificationType,
    OrderState,
    PagedList,
} from "../services/definations";
import { describeNotification, formatTimestamp } from "../util";
import { Redirect, useHistory } from "react-router-dom";
import notificationService from "../services/notificationService";
import Stack from "@mui/material/Stack";
import {
    Alert,
    FormControl,
    IconButton,
    MenuItem,
    Select,
    SelectChangeEvent,
    ToggleButton,
} from "@mui/material";
import Button from "@mui/material/Button";
import Pagination from "./Pagination";
import Typography from "@mui/material/Typography";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import RefreshIcon from "@mui/icons-material/Refresh";
import Box from "@mui/material/Box";

const NotificationList: React.FC<{ compact?: boolean }> = (props) => {
    const { user } = useContext(AuthContext);
    const { setUnreadCount, setNotifHandler, resetNotifHandler } =
        useNotification();
    const routerHistory = useHistory();

    const [currentPage, setCurrentPage] = useState(1);
    const [filterUnread, setFilterUnread] = useState(true);
    const [refreshFlag, setRefreshFlag] = useState(false);
    const [loading, setLoading] = useState(true);
    const [newNotif, setNewNotif] = useState(false);
    const [notifList, setNotifList] = useState<PagedList<Notification>>();

    useEffect(() => {
        if (!user) {
            return;
        }
        notificationService
            .getNotificationList(
                user.id,
                filterUnread ? false : undefined,
                currentPage,
                props.compact ? 5 : 20
            )
            .then((list) => {
                setNotifList(list);
                setLoading(false);
                setRefreshFlag(false);
                setNewNotif(false);
            });
        notificationService
            .getUnreadCount(user.id)
            .then((value) => setUnreadCount(value.count));
    }, [
        filterUnread,
        user,
        refreshFlag,
        currentPage,
        setUnreadCount,
        props.compact,
    ]);

    useEffect(() => {
        setNotifHandler(() => () => {
            setNewNotif(true);
            return NotifHandlerResult.PASS;
        });
        return () => resetNotifHandler();
    }, [resetNotifHandler, setNotifHandler]);

    if (!user) {
        return <Redirect to={"/login"} />;
    }

    const onFilterButtonChanged = (
        event: React.MouseEvent<HTMLElement>,
        value: boolean
    ) => setFilterUnread(value);

    const onFilterSelectChanged = (event: SelectChangeEvent) =>
        setFilterUnread(event.target.value === "true");

    const readAll = () => {
        setLoading(true);
        notificationService.readAll(user!.id).then(() => setRefreshFlag(true));
    };

    const deleteRead = () => {
        setLoading(true);
        notificationService
            .deleteRead(user!.id)
            .then(() => setRefreshFlag(true));
    };

    const refresh = () => {
        setRefreshFlag(true);
    };

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };

    const renderNormalControl = () => (
        <Stack direction="row" spacing={3} mt={3}>
            <ToggleButtonGroup
                value={filterUnread}
                exclusive
                onChange={onFilterButtonChanged}
                size="small"
            >
                <ToggleButton value={true}>筛选未读</ToggleButton>
                <ToggleButton value={false}>显示全部</ToggleButton>
            </ToggleButtonGroup>
            <Button
                onClick={readAll}
                startIcon={<DoneAllIcon />}
                color="success"
                variant="contained"
                size="small"
            >
                全部已读
            </Button>
            <Button
                onClick={deleteRead}
                startIcon={<DeleteOutlineIcon />}
                color="warning"
                variant="outlined"
                size="small"
            >
                删除已读
            </Button>
            <Button
                onClick={refresh}
                startIcon={<RefreshIcon />}
                variant="outlined"
                size="small"
            >
                刷新
            </Button>
        </Stack>
    );

    const renderCompactControl = () => (
        <Stack direction="row">
            <FormControl variant="standard">
                <Select
                    value={filterUnread ? "true" : "false"}
                    onChange={onFilterSelectChanged}
                >
                    <MenuItem value={"true"}>未读消息</MenuItem>
                    <MenuItem value={"false"}>全部消息</MenuItem>
                </Select>
            </FormControl>
            <Button onClick={() => routerHistory.push("/notif")}>
                查看完整列表
            </Button>
            <IconButton onClick={refresh} size="small">
                <RefreshIcon />
            </IconButton>
        </Stack>
    );

    const renderSkeletonItem = () => (
        <ListItem>
            <ListItemIcon>
                <HourglassEmptyIcon />
            </ListItemIcon>
            <ListItemText
                primary={<Skeleton variant="text" />}
                secondary={<Skeleton variant="text" />}
            />
        </ListItem>
    );

    const notifIcon = (notif: Notification) => {
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
        switch (notif.type) {
            case NotificationType.PLAIN:
                return <InfoIcon />;
            case NotificationType.NEW_MESSAGE:
                return <ChatIcon />;
            case NotificationType.ORDER_STATE_CHANGED:
                if (successOrderState.indexOf(notif.newState!) >= 0) {
                    return <CheckCircleOutlineIcon />;
                } else if (warningOrderState.indexOf(notif.newState!) >= 0) {
                    return <CancelIcon />;
                } else {
                    return <InfoIcon />;
                }
            case NotificationType.ACCEPT_DEADLINE:
            case NotificationType.ANSWER_DEADLINE:
                return <AlarmIcon />;
            case NotificationType.ACCEPT_TIMEOUT:
            case NotificationType.ANSWER_TIMEOUT:
                return <TimerOffIcon />;
        }
    };

    const renderNotifList = () => (
        <>
            {notifList?.data.map((notif, idx) => (
                <ListItemButton
                    key={idx}
                    onClick={() => {
                        routerHistory.push(`/orders/${notif.targetId}`);
                        notificationService.readOne(user.id, notif.notifId);
                    }}
                >
                    {props.compact ? (
                        <ListItemIcon sx={{ minWidth: 30 }}>
                            {notifIcon(notif)}
                        </ListItemIcon>
                    ) : (
                        <ListItemIcon>{notifIcon(notif)}</ListItemIcon>
                    )}
                    <ListItemText
                        primary={describeNotification(notif)}
                        secondary={formatTimestamp(notif.createTime)}
                    />
                </ListItemButton>
            ))}
        </>
    );

    return (
        <>
            {props.compact ? renderCompactControl() : renderNormalControl()}
            {newNotif && (
                <Alert severity="info" sx={{ mt: 2 }}>
                    有新通知，请刷新
                </Alert>
            )}
            {loading ? (
                <List>{renderSkeletonItem()}</List>
            ) : notifList?.totalCount === 0 ? (
                <Box textAlign={"center"} mt={6}>
                    <ErrorOutlineIcon color="warning" sx={{ fontSize: 80 }} />
                    <Typography variant={"h5"} mt={1} mb={4}>
                        {filterUnread ? "您没有新的未读通知" : "您没有新的通知"}
                    </Typography>
                </Box>
            ) : (
                <List dense={props.compact}>{renderNotifList()}</List>
            )}
            {!props.compact && notifList && notifList.totalPages > 1 && (
                <Pagination
                    currentPage={currentPage}
                    maxPage={notifList.totalPages}
                    totalCount={notifList.totalCount}
                    itemPrePage={notifList.pageSize}
                    onPageChanged={onPageChanged}
                />
            )}
        </>
    );
};

export default NotificationList;
