import React, { useContext, useEffect, useState } from "react";
import Avatar from "@mui/material/Avatar";
import Skeleton from "@mui/material/Skeleton";
import { Redirect } from "react-router-dom";
import { OrderInfo, OrderList } from "../services/definations";
import questionService from "../services/orderService";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { formatTimestamp } from "../util";
import Stack from "@mui/material/Stack";
import UserContext from "../UserContext";

const QuestionList: React.FC<{ userId?: number }> = (props) => {
    const [questionList, setQuestionList] = useState<OrderList>();
    const [shouldLogin, setShouldLogin] = useState<boolean>(false);
    const { user } = useContext(UserContext);

    useEffect(() => {
        let userId = props.userId;
        if (userId === undefined) {
            userId = user?.id;
        }
        if (userId === undefined) {
            setShouldLogin(true);
            return;
        }
        questionService.getOrdersOfUser(props.userId!).then((response) => {
            setQuestionList(response);
        });
    }, [props.userId, user?.id]);

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

    const renderQuestionList = () => (
        <>
            {questionList!.map((order: OrderInfo, index: number) => (
                <Card key={index}>
                    <CardContent>
                        <Box sx={{ display: "flex", flexDirection: "column" }}>
                            <Box sx={{ display: "flex", flexDirection: "row" }}>
                                <Typography
                                    variant="h6"
                                    noWrap
                                    style={{ fontWeight: 600 }}
                                >
                                    {order.question}
                                </Typography>
                                <Box sx={{ flexGrow: 1 }} />
                                {/*<OrderStateChip state={order.state} />*/}
                            </Box>
                            <Box
                                sx={{ display: "flex", flexDirection: "row" }}
                                mt={1}
                            >
                                <Avatar
                                    src={order.answerer.ava_url}
                                    alt={order.answerer.username}
                                    sx={{ width: 30, height: 30 }}
                                />
                                <Typography variant="subtitle1" sx={{ ml: 1 }}>
                                    {order.answerer.username}
                                </Typography>
                            </Box>
                            <Typography variant="caption" mb={-1} mt={1}>
                                创建时间：{formatTimestamp(order.createTime)}
                            </Typography>
                        </Box>
                    </CardContent>
                </Card>
            ))}
        </>
    );

    return shouldLogin ? (
        <Redirect to={"/login"} />
    ) : (
        <Stack spacing={2} mt={4}>
            {questionList == null ? renderPlaceholder() : renderQuestionList()}
        </Stack>
    );
};

export default QuestionList;
