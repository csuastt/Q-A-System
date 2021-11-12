import React, { useEffect, useState } from "react";
import { OrderInfo, OrderState } from "../services/definations";
import orderService from "../services/orderService";
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
import userService from "../services/userService";

const OrderDetailForAdmin: React.FC<{ orderId: number }> = (props) => {
    const [orderInfo, setOrderInfo] = useState<OrderInfo>();

    useEffect(() => {
        orderService.getOrderInfo(props.orderId).then((order) => {
            setOrderInfo(order);
        });
    }, [props.orderId]);

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
                            src={userService.getAvatarUrl(orderInfo.asker.id)}
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
                <CardContent>
                    <Markdown
                        value={
                            orderInfo.state === OrderState.ANSWERED
                                ? orderInfo.answer
                                : "该问题还未回答"
                        }
                        viewOnly
                    />
                </CardContent>
            </Card>
        </Stack>
    );
};
export default OrderDetailForAdmin;
