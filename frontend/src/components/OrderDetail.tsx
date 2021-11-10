import React, { useContext, useEffect, useState } from "react";
import { OrderInfo, OrderState } from "../services/definations";
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
import CancelIcon from "@mui/icons-material/Cancel";
import ThumbDownIcon from "@mui/icons-material/ThumbDown";
import SmsFailedIcon from "@mui/icons-material/SmsFailed";
import Stack from "@mui/material/Stack";
import Skeleton from "@mui/material/Skeleton";
import Markdown from "./Markdown";
import Typography from "@mui/material/Typography";
import IMMessageList from "./IMMessageList";

const OrderDetail: React.FC<{ orderId: number }> = (props) => {
    const { user } = useContext(AuthContext);

    const [needReload, setNeedReload] = useState(true);

    const [orderInfo, setOrderInfo] = useState<OrderInfo>();
    const [needLogin, setNeedLogin] = useState(user == null);
    const [noPermission, setNoPermission] = useState(false);

    const [answering, setAnswering] = useState(false);

    const [answer, setAnswer] = useState<string>("");

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
                    该问题被审核员驳回
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
                    {isAnswerer ? "您已拒绝接单此问题" : "回答者拒绝接单此问题"}
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
        <Stack spacing={2} mt={4}>
            <Card>
                <CardHeader
                    avatar={
                        <Avatar
                            alt={orderInfo.asker.username}
                            src={orderInfo.asker.avatar}
                            sx={{
                                height: 40,
                                width: 40,
                            }}
                        />
                    }
                    title={orderInfo.asker.username}
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
                </CardContent>
            </Card>
            <Card>
                <CardHeader
                    avatar={
                        <Avatar
                            alt={orderInfo.answerer.username}
                            src={orderInfo.answerer.avatar}
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
                                orderInfo.state === OrderState.ANSWERED
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
        </Stack>
    );
};

export default OrderDetail;
