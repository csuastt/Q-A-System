import React, { useContext, useEffect, useState } from "react";
import { OrderInfo, OrderState } from "../services/definations";
import orderService from "../services/orderService";
import { Redirect } from "react-router-dom";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import CardContent from "@mui/material/CardContent";
import Stack from "@mui/material/Stack";
import Skeleton from "@mui/material/Skeleton";
import Markdown from "./Markdown";
import Typography from "@mui/material/Typography";
import Divider from "@mui/material/Divider";
import Box from "@mui/material/Box";
import AuthContext from "../AuthContext";

const OrderDetailForAdmin: React.FC<{ orderId: number }> = (props) => {
    const { manager } = useContext(AuthContext);

    const [needReload, setNeedReload] = useState(true);

    const [orderInfo, setOrderInfo] = useState<OrderInfo>();
    const [needLogin, setNeedLogin] = useState(manager == null);
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
                setOrderInfo(order);
            })
            .catch((err) => {
                if (err.response.status === 401) {
                    setNeedLogin(true);
                } else if (err.response.status === 403) {
                    setNoPermission(true);
                }
            })
            .finally(() => setNeedReload(false));
    }, [needReload, props.orderId]);

    // Answerering helper functions
    const handleAnswerChange = (newValue: string) => {
        setAnswer(newValue);
    };

    const respondOrder = (ok: boolean) => {
        setOrderInfo(undefined);
        orderService
            .respondOrder(orderInfo!.id, ok)
            .then(() => setNeedReload(true));
    };


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
                <CardContent>
                    <Divider textAlign="left">问题标题</Divider>
                    <Typography
                        variant="h6"
                        sx={{ mb: orderInfo.questionDescription && 2 }}
                    >
                        {orderInfo.questionTitle}
                    </Typography>
                    {orderInfo.questionDescription && (
                        <>
                            <Divider textAlign="left">问题描述</Divider>
                            <Box>
                                <Markdown
                                    value={orderInfo.questionDescription}
                                    viewOnly
                                />
                            </Box>
                        </>
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
                <CardContent>
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
            </Card>
        </Stack>
    );
};
export default OrderDetailForAdmin;
