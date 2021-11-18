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
import Button from "@mui/material/Button";
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
import {
    Box,
    Dialog,
    DialogTitle,
    List,
    ListItem,
    ListItemAvatar,
    ListItemText,
    Rating,
} from "@mui/material";
import FolderIcon from "@mui/icons-material/Folder";
import { formatSize } from "../util";
import PublicIcon from "@mui/icons-material/Public";
import PrivacyTipIcon from "@mui/icons-material/PrivacyTip";
import DialogActions from "@mui/material/DialogActions";
import DialogContentText from "@mui/material/DialogContentText";
import DialogContent from "@mui/material/DialogContent";

const OrderDetail: React.FC<{ orderId: number }> = (props) => {
    const { user } = useContext(AuthContext);

    const [needReload, setNeedReload] = useState(true);

    const [orderInfo, setOrderInfo] = useState<OrderInfo>();
    const [needLogin, setNeedLogin] = useState(user == null);
    const [noPermission, setNoPermission] = useState(false);
    const [answering, setAnswering] = useState(false);
    const [answer, setAnswer] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [open, setOpen] = useState(false);
    const [openRatingDialog, setOpenRatingDialog] = useState(false);
    const [attachments, setAttachments] = useState<Array<AttachmentInfo>>([]);
    const [rating, setRating] = useState<number>(0);

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
                // if (order.id !== user?.id && order.id !== user?.id) {
                //     setNoPermission(true);
                // } else {
                setOrderInfo(order);
                // }
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
                    该问题被审核员驳回，已全额退款
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
                    该问题因支付超时而失效
                </Button>
            );
        } else if (state === OrderState.RESPOND_TIMEOUT) {
            return (
                <Button
                    variant="text"
                    startIcon={<AccessTimeIcon />}
                    color="error"
                >
                    该问题因接单超时而失效，已全额退款
                </Button>
            );
        } else if (state === OrderState.ANSWER_TIMEOUT) {
            return (
                <Button
                    variant="text"
                    startIcon={<AccessTimeIcon />}
                    color="error"
                >
                    该问题因回答超时而失效，已全额退款
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
                        >
                            确认接单
                        </Button>
                        <Button
                            variant="outlined"
                            startIcon={<DeleteIcon />}
                            onClick={() => respondOrder(false)}
                            color="error"
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
                        ? "您已拒绝接受此问题"
                        : "回答者拒绝接受此问题，已全额退款"}
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
                            >
                                确认回答
                            </Button>
                            <Button
                                variant="outlined"
                                startIcon={<CancelIcon />}
                                onClick={cancelAnswering}
                                color="warning"
                            >
                                取消回答
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button
                                variant="outlined"
                                startIcon={<SettingsIcon />}
                                onClick={startAnswering}
                            >
                                开始回答
                            </Button>
                        </>
                    )}
                </>
            );
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
            .rateOrder(orderInfo!.id, rating)
            .then(() => setNeedReload(true));
    };
    const renderRating = () => {
        if (!orderInfo)
            return null;
        if (orderInfo.rating === 0) {
            return (
                <Stack spacing={1} sx={{ alignItems: "center" }}>
                    <Typography variant="h5">
                        {ratingMsg[rating]}
                    </Typography>
                    <Rating
                        value={rating}
                        onChange={(_, newValue) =>
                            setRating(newValue ? newValue : 0)
                        }
                        size="large"
                    />
                </Stack>
            );
        } else {
            return (
                <Stack spacing={1} sx={{ alignItems: "center" }}>
                    <Typography variant="h5">
                        {ratingMsg[orderInfo.rating]}
                    </Typography>
                    <Rating
                        value={orderInfo.rating}
                        size="large"
                        readOnly
                    />
                    <Typography variant="subtitle1">已评价</Typography>
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
                        <Box
                            sx={{
                                display: "flex",
                                flexDirection: "row",

                            }}
                            mb={-1} mt={1}
                        >
                            <Box sx={{ flexGrow: 1 }} />
                            { orderInfo &&
                                (
                                    orderInfo.state === OrderState.CHAT_ENDED ||
                                    orderInfo.state === OrderState.FULFILLED
                                ) && (
                                    orderInfo.rating === 0 ?
                                        (
                                            orderInfo.answerer.id === user!.id ?
                                                <></>:
                                                <Button variant="contained" onClick={handleOpenRatingDialog}>
                                                    评价
                                                </Button>
                                        )
                                        :
                                        <Button variant="contained" onClick={handleOpenRatingDialog}>
                                            查看评价
                                        </Button>
                                )
                            }
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
                <IMMessageList orderInfo={orderInfo} />
                {orderInfo.state === OrderState.ANSWERED && (
                    <Card>
                        <Markdown value={message} onChange={setMessage} />
                        <CardActions>
                            <Button
                                onClick={sendMessage}
                                startIcon={<SendIcon />}
                            >
                                发送消息
                            </Button>
                            <Button
                                onClick={endOrder}
                                startIcon={<CloseIcon />}
                                color={"error"}
                            >
                                结束订单
                            </Button>
                        </CardActions>
                    </Card>
                )}
            </Stack>
            <Dialog onClose={handleClose} open={open}>
                <DialogTitle>附件列表</DialogTitle>
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
            <Dialog onClose={handleCloseRatingDialog} open={openRatingDialog} fullWidth>
                <DialogTitle>订单评价</DialogTitle>
                <DialogContent>
                    <DialogContentText mb={1}>
                        {
                            orderInfo && (
                                orderInfo.rating === 0 ?
                                    "请对本次服务进行评价。您的评价是对回答者最好的鼓励和肯定。非常感谢您的支持！":
                                    "请查看您的订单评价。"
                            )
                        }
                    </DialogContentText>
                    {renderRating()}
                </DialogContent>
                <DialogActions>
                    {
                        orderInfo && (
                            orderInfo.rating === 0 ?
                                <Button
                                    disabled={rating === 0}
                                    onClick={confirmRating}
                                >
                                    确认评价
                                </Button>:
                                <Button
                                    onClick={handleCloseRatingDialog}
                                >
                                    知道了
                                </Button>
                        )
                    }
                </DialogActions>
            </Dialog>
        </>
    );
};

export default OrderDetail;
