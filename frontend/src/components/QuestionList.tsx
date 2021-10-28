import React, { useContext, useEffect, useState } from "react";
import Avatar from "@mui/material/Avatar";
import Skeleton from "@mui/material/Skeleton";
import { Link as RouterLink, Redirect } from "react-router-dom";
import { OrderInfo, OrderList, UserRole } from "../services/definations";
import questionService from "../services/orderService";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { formatTimestamp, parseIntWithDefault, useQuery } from "../util";
import Stack from "@mui/material/Stack";
import UserContext from "../AuthContext";
import CardActionArea from "@mui/material/CardActionArea";
import Pagination from "./Pagination";

const QuestionList: React.FC<{ userId?: number }> = (props) => {
    const query = useQuery();
    const [questionList, setQuestionList] = useState<OrderList>();
    const [shouldRedirect, setShouldRedirect] = useState<string>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const [itemPrePage] = useState(
        parseIntWithDefault(query.get("prepage"), 20)
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [totalCount, setTotalCount] = useState(0);

    const { user } = useContext(UserContext);

    useEffect(() => {
        if (user == null) {
            setShouldRedirect("/login");
            return;
        }
        (user.role === UserRole.USER
            ? questionService.getOrdersOfUser(
                  user.id,
                  undefined,
                  currentPage,
                  itemPrePage
              )
            : questionService.getOrdersOfUser(
                  undefined,
                  user.id,
                  currentPage,
                  itemPrePage
              )
        ).then((response) => {
            setQuestionList(response.data);
            setMaxPage(response.totalPages);
            setTotalCount(response.totalCount);
        });
    }, [currentPage, itemPrePage, props.userId, user?.id]);

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };

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
                    <CardActionArea
                        component={RouterLink}
                        to={`/orders/${order.id}`}
                    >
                        <CardContent>
                            <Box
                                sx={{
                                    display: "flex",
                                    flexDirection: "column",
                                }}
                            >
                                <Box
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                    }}
                                >
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
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                    }}
                                    mt={1}
                                >
                                    <Avatar
                                        src={order.answerer.avatar}
                                        alt={order.answerer.username}
                                        sx={{ width: 30, height: 30 }}
                                    />
                                    <Typography
                                        variant="subtitle1"
                                        sx={{ ml: 1 }}
                                    >
                                        {order.answerer.username}
                                    </Typography>
                                </Box>
                                <Typography variant="caption" mb={-1} mt={1}>
                                    创建时间：
                                    {formatTimestamp(order.createTime)}
                                </Typography>
                            </Box>
                        </CardContent>
                    </CardActionArea>
                </Card>
            ))}
            {maxPage > 1 && (
                <Pagination
                    currentPage={currentPage}
                    maxPage={maxPage}
                    totalCount={totalCount}
                    itemPrePage={itemPrePage}
                    onPageChanged={onPageChanged}
                />
            )}
        </>
    );

    if (shouldRedirect) {
        return <Redirect to={shouldRedirect} />;
    }
    if (questionList == null) {
        return (
            <Stack spacing={2} mt={4}>
                {renderPlaceholder()}
            </Stack>
        );
    }
    if (totalCount === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有订单
            </Typography>
        );
    }
    return (
        <Stack spacing={2} mt={4}>
            {renderQuestionList()}
        </Stack>
    );
};

export default QuestionList;
