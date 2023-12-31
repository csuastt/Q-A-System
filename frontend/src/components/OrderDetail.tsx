import React, { useContext, useEffect, useState } from "react";
import {
    AttachmentInfo,
    IMMessage,
    OrderInfo,
    OrderState,
} from "../services/definations";
import orderService from "../services/orderService";
import AuthContext from "../AuthContext";
import { Redirect } from "react-router-dom";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import CardContent from "@mui/material/CardContent";
import CardActions from "@mui/material/CardActions";
import Button, { ButtonProps } from "@mui/material/Button";
import SettingsIcon from "@mui/icons-material/Settings";
import DeleteIcon from "@mui/icons-material/Delete";
import CheckIcon from "@mui/icons-material/Check";
import CloseIcon from "@mui/icons-material/Close";
import CancelIcon from "@mui/icons-material/Cancel";
import ThumbDownIcon from "@mui/icons-material/ThumbDown";
import SmsFailedIcon from "@mui/icons-material/SmsFailed";
import SendIcon from "@mui/icons-material/Send";
import Stack from "@mui/material/Stack";
import Skeleton from "@mui/material/Skeleton";
import Markdown from "./Markdown";
import Typography from "@mui/material/Typography";
import IMMessageList from "./IMMessageList";
import imService from "../services/imService";
import userService from "../services/userService";
import AccessTimeIcon from "@mui/icons-material/AccessTime";
import HourglassEmptyIcon from "@mui/icons-material/HourglassEmpty";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import CancelOutlinedIcon from "@mui/icons-material/CancelOutlined";
import AttachFileIcon from "@mui/icons-material/AttachFile";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Dialog from "@mui/material/Dialog";
import DialogTitle from "@mui/material/DialogTitle";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import ListItemText from "@mui/material/ListItemText";
import Rating from "@mui/material/Rating";
import FolderIcon from "@mui/icons-material/Folder";
import { formatSize, formatTimestamp } from "../util";
import PublicIcon from "@mui/icons-material/Public";
import PrivacyTipIcon from "@mui/icons-material/PrivacyTip";
import AlarmIcon from "@mui/icons-material/Alarm";
import ChatIcon from "@mui/icons-material/Chat";
import systemConfigService from "../services/systemConfigService";
import DialogActions from "@mui/material/DialogActions";
import DialogContentText from "@mui/material/DialogContentText";
import DialogContent from "@mui/material/DialogContent";
import TextField from "@mui/material/TextField";
import Badge from "@mui/material/Badge";
import IconButton from "@mui/material/IconButton";
import Tooltip from "@mui/material/Tooltip";
import { Image } from "mdast";
import { useTheme } from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";

const AutoSizeButton: React.FC<
    Omit<ButtonProps, "startIcon"> & { icon: React.ReactNode }
> = (props) => {
    const theme = useTheme();
    const isLargeScreen = useMediaQuery(theme.breakpoints.up("md"));
    return isLargeScreen ? (
        <Button {...props} startIcon={props.icon}>
            {props.children}
        </Button>
    ) : (
        <Tooltip title={`${props.children}`}>
            <IconButton {...props}>{props.icon}</IconButton>
        </Tooltip>
    );
};

const OrderDetail: React.FC<{ orderId: number }> = (props) => {
    const { user } = useContext(AuthContext);

    const [needReload, setNeedReload] = useState(true);

    const [orderInfo, setOrderInfo] = useState<OrderInfo>();
    const [needLogin, setNeedLogin] = useState(user == null);
    const [noPermission, setNoPermission] = useState(false);
    const [answering, setAnswering] = useState(false);
    const [answer, setAnswer] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [msgCount, setMsgCount] = useState<number>();
    const [maxMsgCount, setMaxMsgCount] = useState<number>();
    const [open, setOpen] = React.useState(false);
    const [openRatingDialog, setOpenRatingDialog] = useState(false);
    const [attachments, setAttachments] = useState<Array<AttachmentInfo>>([]);
    const [rating, setRating] = useState<number>(0);
    const [ratingText, setRatingText] = useState<string>("");

    const theme = useTheme();
    const isLargeScreen = useMediaQuery(theme.breakpoints.up("md"));

    const handleOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleOpenRatingDialog = () => {
        setOpenRatingDialog(true);
    };

    const handleCloseRatingDialog = () => {
        setOpenRatingDialog(false);
    };

    useEffect(() => {
        if (!needReload) {
            return;
        }
        orderService
            .getOrderInfo(props.orderId)
            .then((order) => {
                setOrderInfo(order);
                setMsgCount(order.messageCount);
            })
            .catch((err) => {
                if (err.response.status === 401) {
                    setNeedLogin(true);
                } else if (err.response.status === 403) {
                    setNoPermission(true);
                }
            })
            .finally(() => setNeedReload(false));
        orderService.getAttachments(props.orderId).then((attachmentList) => {
            setAttachments(attachmentList);
        });
    }, [needReload, props.orderId, user?.id]);

    useEffect(() => {
        systemConfigService.getSystemConfig().then((config) => {
            setMaxMsgCount(config.maxChatMessages);
        });
    }, []);

    // Upload Image
    const uploadImage = React.useCallback<
        (files: File[]) => Promise<Pick<Image, "url" | "alt" | "title">[]>
    >(
        (files: File[]) =>
            Promise.all(
                files.map((file) =>
                    orderService
                        .uploadPicture(props.orderId, file)
                        .then((uuid) =>
                            orderService.getPictureUrl(props.orderId, uuid)
                        )
                        .then((url) => ({
                            url: url,
                            alt: file.name,
                            title: file.name,
                        }))
                )
            ),
        [props.orderId]
    );
    // Upload Attachment
    const uploadAttachments = React.useCallback(
        (event: React.ChangeEvent<HTMLInputElement>) => {
            const files = event.target.files;
            let promises: Promise<any>[] = [];
            if (files) {
                for (let i = 0; i < files.length; i++) {
                    if (files.item(i)) {
                        promises.push(
                            orderService.uploadAttachment(
                                props.orderId,
                                files.item(i)!
                            )
                        );
                    }
                }
            }
            Promise.all(promises).then(() => setNeedReload(true));
        },
        [props.orderId]
    );

    // Answerering helper functions
    const handleAnswerChange = (newValue: string) => {
        setAnswer(newValue);
    };
    const startAnswering = () => {
        setAnswering(true);
    };
    const commitAnswer = () => {
        setOrderInfo(undefined);
        setAnswering(false);
        orderService
            .answerOrder(orderInfo!.id, answer)
            .then(() => setNeedReload(true));
    };
    const cancelAnswering = () => {
        setAnswering(false);
    };
    const respondOrder = (ok: boolean) => {
        setOrderInfo(undefined);
        orderService
            .respondOrder(orderInfo!.id, ok)
            .then(() => setNeedReload(true));
    };

    const increaseMsgCount = React.useCallback(() => {
        if (msgCount && msgCount > 0) {
            setMsgCount(msgCount + 1);
        }
    }, [msgCount]);

    // IM helper function
    const sendMessage = () => {
        if (message.length === 0) {
            return;
        }
        const msg: Omit<IMMessage, "messageId"> = {
            senderId: user!.id,
            sendTime: new Date().toISOString(),
            msgBody: message,
        };
        imService.sendMessage(props.orderId, msg);
        setMessage("");
    };
    const endOrder = () => {
        orderService.endOrder(props.orderId).then(() => setNeedReload(true));
    };

    // ANSWERER helper functions
    const renderAnswererActions = () => {
        const state = orderInfo!.state;
        const isAnswerer = orderInfo!.answerer.id === user!.id;
        if (state === OrderState.REJECTED_BY_REVIEWER) {
            return (
                <Button
                    variant="text"
                    startIcon={<ThumbDownIcon />}
                    color="error"
                >
                    订单审核不通过，已全额退款
                </Button>
            );
        } else if (state === OrderState.CREATED) {
            return (
                <Button
                    variant="text"
                    startIcon={<HourglassEmptyIcon />}
                    color="warning"
                >
                    订单已创建，等待平台审核
                </Button>
            );
        } else if (state === OrderState.PAY_TIMEOUT) {
            return (
                <Button
                    variant="text"
                    startIcon={<AccessTimeIcon />}
                    color="error"
                >
                    订单因支付超时而失效
                </Button>
            );
        } else if (state === OrderState.RESPOND_TIMEOUT) {
            return (
                <Button
                    variant="text"
                    startIcon={<AccessTimeIcon />}
                    color="error"
                >
                    订单因接单超时而失效，已退款
                </Button>
            );
        } else if (state === OrderState.ANSWER_TIMEOUT) {
            return (
                <Button
                    variant="text"
                    startIcon={<AccessTimeIcon />}
                    color="error"
                >
                    订单因回答超时而失效，已退款
                </Button>
            );
        } else if (state === OrderState.CHAT_ENDED) {
            return (
                <Button
                    variant="text"
                    startIcon={<HourglassEmptyIcon />}
                    color="warning"
                >
                    交流已完成，等待平台结算
                </Button>
            );
        } else if (state === OrderState.FULFILLED) {
            return (
                <Button
                    variant="text"
                    startIcon={<CheckCircleOutlineIcon />}
                    color="success"
                >
                    交易完成
                </Button>
            );
        } else if (state === OrderState.CANCELLED) {
            return (
                <Button
                    variant="text"
                    startIcon={<CancelOutlinedIcon />}
                    color="error"
                >
                    提问者主动取消订单
                </Button>
            );
        } else if (state === OrderState.REVIEWED) {
            return (
                isAnswerer && (
                    <>
                        <Button
                            variant="contained"
                            startIcon={<CheckIcon />}
                            onClick={() => respondOrder(true)}
                            color="success"
                            sx={{ marginLeft: 1, marginBottom: 1 }}
                        >
                            确认接单
                        </Button>
                        <Button
                            variant="outlined"
                            startIcon={<DeleteIcon />}
                            onClick={() => respondOrder(false)}
                            color="error"
                            sx={{ marginLeft: 1, marginBottom: 1 }}
                        >
                            拒绝接单
                        </Button>
                    </>
                )
            );
        } else if (state === OrderState.REJECTED_BY_ANSWERER) {
            return (
                <Button
                    variant="text"
                    startIcon={<SmsFailedIcon />}
                    color="error"
                >
                    {isAnswerer
                        ? "您已拒绝接受此订单"
                        : "回答者拒绝接受此订单，已退款"}
                </Button>
            );
        } else if (state === OrderState.ACCEPTED) {
            return (
                <>
                    {answering ? (
                        <>
                            <Button
                                variant="contained"
                                startIcon={<CheckIcon />}
                                onClick={commitAnswer}
                                sx={{ marginLeft: 1, marginBottom: 1 }}
                            >
                                确认回答
                            </Button>
                            <AutoSizeButton
                                variant="outlined"
                                icon={<CancelIcon />}
                                onClick={cancelAnswering}
                                color="warning"
                                sx={{ marginLeft: 1, marginBottom: 1 }}
                            >
                                取消回答
                            </AutoSizeButton>
                        </>
                    ) : (
                        <>
                            <Button
                                variant="outlined"
                                startIcon={<SettingsIcon />}
                                onClick={startAnswering}
                                sx={{ marginLeft: 1, marginBottom: 1 }}
                            >
                                开始回答
                            </Button>
                        </>
                    )}
                </>
            );
        }
    };

    const stateHasExpireTime: Array<OrderState> = [
        OrderState.REVIEWED,
        OrderState.ACCEPTED,
        OrderState.ANSWERED,
        OrderState.CHAT_ENDED,
    ];
    const renderExpireTime = (order: OrderInfo) => {
        const expireTimeType = stateHasExpireTime.indexOf(order.state);
        const expireTimeMsg = [
            "接单超时时间：",
            "首次回答超时时间：",
            "聊天自动结束时间：",
            "结算时间：",
        ];
        if (expireTimeType !== -1) {
            return (
                <Alert
                    variant="outlined"
                    severity="warning"
                    icon={<AlarmIcon />}
                    sx={{ mt: 3, mb: -1 }}
                >
                    {expireTimeMsg[expireTimeType] +
                        formatTimestamp(order.expireTime)}
                </Alert>
            );
        }
    };

    const renderMessageInput = (order: OrderInfo) => {
        if (order.state === OrderState.ANSWERED) {
            if (msgCount && maxMsgCount && msgCount === maxMsgCount) {
                return <Alert security="warning">聊天次数已用完</Alert>;
            } else {
                const msgCountUsage =
                    msgCount != null
                        ? "聊天次数使用：" +
                          (maxMsgCount
                              ? `${msgCount}/${maxMsgCount}`
                              : `${msgCount}`)
                        : "";
                return (
                    <Card>
                        <Markdown
                            value={message}
                            onChange={setMessage}
                            uploadImages={uploadImage}
                        />
                        <CardActions>
                            <Button
                                onClick={sendMessage}
                                startIcon={<SendIcon />}
                            >
                                发送消息
                            </Button>
                            <label htmlFor="upload-attachment">
                                <input
                                    style={{ display: "none" }}
                                    accept="*"
                                    id="upload-attachment"
                                    type="file"
                                    name="upload-attachment"
                                    multiple={true}
                                    onChange={uploadAttachments}
                                />
                                {isLargeScreen ? (
                                    <Button
                                        startIcon={<UploadFileIcon />}
                                        component="span"
                                    >
                                        上传附件
                                    </Button>
                                ) : (
                                    <IconButton component="span">
                                        <UploadFileIcon />
                                    </IconButton>
                                )}
                            </label>
                            <AutoSizeButton
                                onClick={endOrder}
                                icon={<CloseIcon />}
                                color={"error"}
                            >
                                结束订单
                            </AutoSizeButton>
                            {msgCountUsage &&
                                (isLargeScreen ? (
                                    <>
                                        <Box sx={{ flexGrow: 1 }} />
                                        <ChatIcon sx={{ mr: 1 }} color="info" />
                                        <Typography
                                            variant="subtitle2"
                                            color="info"
                                        >
                                            {msgCountUsage}
                                        </Typography>
                                    </>
                                ) : (
                                    <>
                                        <Box sx={{ flexGrow: 1 }} />
                                        <Tooltip
                                            title={msgCountUsage}
                                            sx={{ mr: 1 }}
                                        >
                                            <IconButton>
                                                <Badge
                                                    badgeContent={
                                                        maxMsgCount! - msgCount!
                                                    }
                                                    color="secondary"
                                                >
                                                    <ChatIcon color="info" />
                                                </Badge>
                                            </IconButton>
                                        </Tooltip>
                                    </>
                                ))}
                        </CardActions>
                    </Card>
                );
            }
        }
    };

    const ratingMsg: Array<string> = [
        "还未评价",
        "毫无作用",
        "不满意",
        "差强人意",
        "还不错～",
        "太棒了！",
    ];
    const confirmRating = () => {
        orderService
            .rateOrder(orderInfo!.id, rating, ratingText)
            .then(() => setNeedReload(true));
    };
    const renderRating = () => {
        if (!orderInfo) return null;
        if (orderInfo.rating === 0) {
            return (
                <Stack spacing={1} sx={{ alignItems: "center" }}>
                    <Typography variant="h5">{ratingMsg[rating]}</Typography>
                    <Rating
                        value={rating}
                        onChange={(_, newValue) =>
                            setRating(newValue ? newValue : 0)
                        }
                        size="large"
                    />
                    <Box mt={1} />
                    <TextField
                        fullWidth
                        label="留言反馈"
                        name="ratingText"
                        multiline
                        onChange={(e) => {
                            setRatingText(e.target.value);
                        }}
                        rows={4}
                        value={ratingText}
                        placeholder="请留下您的宝贵意见~"
                        variant="outlined"
                        inputProps={{ maxLength: 200 }}
                    />
                </Stack>
            );
        } else {
            return (
                <Stack spacing={1} sx={{ alignItems: "center" }}>
                    <Typography variant="h5">
                        {ratingMsg[orderInfo.rating]}
                    </Typography>
                    <Rating value={orderInfo.rating} size="large" readOnly />
                    <Box mt={1} />
                    <TextField
                        fullWidth
                        label="留言反馈"
                        name="ratingText"
                        multiline
                        InputProps={{
                            readOnly: true,
                        }}
                        rows={4}
                        value={
                            orderInfo.ratingText
                                ? orderInfo.ratingText
                                : "暂无评价~"
                        }
                        variant="outlined"
                        inputProps={{ maxLength: 200 }}
                    />
                </Stack>
            );
        }
    };

    if (needLogin) {
        console.log("needLogin");
        return <Redirect to="/login" />;
    }
    if (noPermission) {
        console.log("noPermission");
        return <Redirect to="/orders" />;
    }
    if (orderInfo == null) {
        // Loading order info
        return (
            <Stack spacing={2}>
                <Card>
                    <CardHeader
                        avatar={
                            <Skeleton
                                animation="wave"
                                variant="circular"
                                width={40}
                                height={40}
                            />
                        }
                        title={
                            <Skeleton
                                animation="wave"
                                height={10}
                                width="80%"
                                style={{ marginBottom: 6 }}
                            />
                        }
                        subheader={"Loading..."}
                    />
                    <CardContent>
                        <React.Fragment>
                            <Skeleton
                                animation="wave"
                                height={10}
                                style={{ marginBottom: 6 }}
                            />
                            <Skeleton
                                animation="wave"
                                height={10}
                                width="80%"
                            />
                        </React.Fragment>
                    </CardContent>
                </Card>
            </Stack>
        );
    }

    return (
        <>
            <Stack spacing={2} mt={4}>
                <Card>
                    <CardHeader
                        avatar={
                            <Avatar
                                alt={orderInfo.asker.username}
                                src={userService.getAvatarUrl(
                                    orderInfo.asker.id
                                )}
                                sx={{
                                    height: 40,
                                    width: 40,
                                }}
                            />
                        }
                        title={orderInfo.asker.nickname}
                        action={
                            <Box
                                sx={{
                                    display: "flex",
                                    flexDirection: "row",
                                }}
                                alignItems="center"
                            >
                                {orderInfo.showPublic ? (
                                    <Typography
                                        variant={"body2"}
                                        color={"primary"}
                                    >
                                        {"公开问题"}
                                    </Typography>
                                ) : (
                                    <Typography
                                        variant={"body2"}
                                        color={"secondary"}
                                    >
                                        {"私密问题"}
                                    </Typography>
                                )}
                                <Box sx={{ paddingRight: 0.5 }} />
                                {orderInfo.showPublic ? (
                                    <PublicIcon color={"primary"} />
                                ) : (
                                    <PrivacyTipIcon color={"secondary"} />
                                )}
                                <Box sx={{ paddingRight: 1 }} />
                            </Box>
                        }
                        subheader="提问者"
                    />
                    <CardContent sx={{ paddingTop: 1 }}>
                        <Typography
                            variant="h5"
                            sx={{
                                mb: orderInfo.questionDescription && 2,
                                fontWeight: 600,
                            }}
                        >
                            {orderInfo.questionTitle}
                        </Typography>
                        {orderInfo.questionDescription && (
                            <Markdown
                                value={orderInfo.questionDescription}
                                viewOnly
                            />
                        )}
                        {attachments.length > 0 && (
                            <Box mt={2} mb={0}>
                                <Button
                                    variant="text"
                                    startIcon={<AttachFileIcon />}
                                    onClick={handleOpen}
                                >
                                    查看附件
                                </Button>
                            </Box>
                        )}
                        {renderExpireTime(orderInfo)}
                        <Box
                            sx={{
                                display: "flex",
                                flexDirection: "row",
                            }}
                            mb={-1}
                            mt={3}
                        >
                            <Box sx={{ flexGrow: 1 }} />
                            {orderInfo &&
                                (orderInfo.state === OrderState.CHAT_ENDED ||
                                    orderInfo.state === OrderState.FULFILLED) &&
                                (orderInfo.rating === 0 ? (
                                    orderInfo.asker.id === user!.id && (
                                        <Button
                                            variant="contained"
                                            onClick={handleOpenRatingDialog}
                                        >
                                            评价
                                        </Button>
                                    )
                                ) : (
                                    <Button
                                        variant="contained"
                                        onClick={handleOpenRatingDialog}
                                    >
                                        查看评价
                                    </Button>
                                ))}
                        </Box>
                    </CardContent>
                </Card>
                <Card>
                    <CardHeader
                        avatar={
                            <Avatar
                                alt={orderInfo.answerer.username}
                                src={userService.getAvatarUrl(
                                    orderInfo.answerer.id
                                )}
                                sx={{
                                    height: 40,
                                    width: 40,
                                }}
                            />
                        }
                        title={orderInfo.answerer.username}
                        subheader="回答者"
                    />
                    <CardContent sx={{ paddingTop: 0 }}>
                        {answering ? (
                            <Markdown
                                value={answer}
                                onChange={handleAnswerChange}
                            />
                        ) : (
                            <Markdown
                                value={
                                    [
                                        OrderState.ANSWERED,
                                        OrderState.CHAT_ENDED,
                                        OrderState.FULFILLED,
                                    ].indexOf(orderInfo.state) >= 0
                                        ? orderInfo.answer
                                        : "该问题还未回答"
                                }
                                viewOnly
                            />
                        )}
                    </CardContent>
                    <CardActions>{renderAnswererActions()}</CardActions>
                </Card>
                <IMMessageList
                    orderInfo={orderInfo}
                    onNewMessage={increaseMsgCount}
                />
                {renderMessageInput(orderInfo)}
            </Stack>
            <Dialog onClose={handleClose} open={open}>
                <DialogTitle>附件列表</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        附件的大小不得大于300M，超过此大小的附件将会被忽略。
                    </DialogContentText>
                </DialogContent>
                <List sx={{ pt: 0 }}>
                    {attachments.map((attachmentInfo) => (
                        <ListItem
                            button
                            component="a"
                            href={orderService.getAttachmentUrl(
                                props.orderId,
                                attachmentInfo.uuid
                            )}
                            key={attachmentInfo.uuid}
                        >
                            <ListItemAvatar>
                                <Avatar>
                                    <FolderIcon />
                                </Avatar>
                            </ListItemAvatar>
                            <ListItemText
                                primary={attachmentInfo.filename}
                                secondary={formatSize(attachmentInfo.size, 2)}
                            />
                        </ListItem>
                    ))}
                </List>
            </Dialog>
            <Dialog
                onClose={handleCloseRatingDialog}
                open={openRatingDialog}
                fullWidth
            >
                <DialogTitle>订单评价</DialogTitle>
                <DialogContent>
                    <DialogContentText mb={1}>
                        {orderInfo &&
                            (orderInfo.rating === 0
                                ? "请对本次服务进行评价。您的评价是对回答者最好的鼓励和肯定。非常感谢您的支持！"
                                : "请查看此订单的评价。")}
                    </DialogContentText>
                    {renderRating()}
                </DialogContent>
                <DialogActions>
                    {orderInfo &&
                        (orderInfo.rating === 0 ? (
                            <Button
                                disabled={rating === 0}
                                onClick={confirmRating}
                            >
                                确认评价
                            </Button>
                        ) : (
                            <Button onClick={handleCloseRatingDialog}>
                                知道了
                            </Button>
                        ))}
                </DialogActions>
            </Dialog>
        </>
    );
};

export default OrderDetail;
