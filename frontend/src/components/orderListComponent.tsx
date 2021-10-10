import React, { useEffect, useState } from "react";
import Avatar from "@mui/material/Avatar";
import Skeleton from "@mui/material/Skeleton";
import { Redirect } from "react-router-dom";
import _ from "lodash";
import { OrderInfo, OrderList, UserInfo } from "../services/definations";
import questionService from "../services/order.service";
import userService from "../services/user.service";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import OrderStateChip from "./orderStateChip";
import { formatTimestamp } from "../util";
import Stack from "@mui/material/Stack";

const QuestionList: React.FC<{ userId: number }> = (props) => {
    const [questionList, setQuestionList] = useState<OrderList>();
    const [userMap, setUserMap] = useState<Map<number, UserInfo>>();

    // const prevented = authService.getCurrentUser()?.id !== props.userId;
    const prevented = false;

    useEffect(() => {
        if (prevented) return;
        questionService
            .getOrdersOfUser(props.userId)
            .then((response) => {
                setQuestionList(response);
                return userService.getUsersByIdList(
                    _.uniq(response.map((question) => question.answerer))
                );
            })
            .then((users) => {
                setUserMap(new Map(users.map((user) => [user.id, user])));
            });
    }, []);

    const renderCardPlaceholder = () => (
        <Card>
            <CardContent>
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <Skeleton variant="text" height={30} width={120} />
                    <Skeleton variant="rectangular" height={100} />
                    <Box sx={{ display: "flex", flexDirection: "row", mt: 1 }}>
                        <Skeleton variant="circular" height={30} width={30} />
                        <Skeleton
                            variant="text"
                            height={30}
                            width={60}
                            sx={{ ml: 1 }}
                        />
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );

    const renderPlaceholder = () => (
        <>
            {renderCardPlaceholder()}
            {renderCardPlaceholder()}
        </>
    );

    const AvatarWrapper: React.FC<{ id: number }> = (props) => {
        const user = userMap?.get(props.id);
        return user === undefined ? (
            <Skeleton variant="circular" height={30} width={30} />
        ) : (
            <Avatar
                alt={user.username}
                src={user.ava_url}
                sx={{ width: 30, height: 30 }}
            />
        );
    };

    const renderQuestionList = () => (
        <>
            {questionList!.map((question: OrderInfo, index: number) => (
                <Card key={index}>
                    <CardContent>
                        <Box sx={{ display: "flex", flexDirection: "column" }}>
                            <Box sx={{ display: "flex", flexDirection: "row" }}>
                                <Typography
                                    variant="h6"
                                    noWrap
                                    style={{ fontWeight: 600 }}
                                >
                                    {question.stem}
                                </Typography>
                                <Box sx={{ flexGrow: 1 }} />
                                <OrderStateChip state={question.state} />
                            </Box>
                            <Typography
                                variant="body1"
                                sx={{ wordBreak: "break-all" }}
                                gutterBottom
                            >
                                {question.question}
                            </Typography>
                            <Box
                                sx={{ display: "flex", flexDirection: "row" }}
                                mt={1}
                            >
                                <AvatarWrapper id={question.answerer} />
                                <Typography variant="h6" sx={{ ml: 1 }}>
                                    User {question.asker}
                                </Typography>
                            </Box>
                            <Typography variant="caption" mb={-1} mt={1}>
                                创建时间：{formatTimestamp(question.createTime)}
                            </Typography>
                        </Box>
                    </CardContent>
                </Card>
            ))}
        </>
    );

    return prevented ? (
        <Redirect to={"/login"} />
    ) : (
        <Stack spacing={2} mt={4}>
            {questionList == null ? renderPlaceholder() : renderQuestionList()}
        </Stack>
    );
};

export default QuestionList;
