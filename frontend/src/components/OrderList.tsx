import React, { useEffect, useState } from "react";
import Avatar from "@mui/material/Avatar";
import Skeleton from "@mui/material/Skeleton";
import { Link as RouterLink } from "react-router-dom";
import { OrderInfo, PagedList } from "../services/definations";
import questionService from "../services/orderService";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { formatTimestamp } from "../util";
import Stack from "@mui/material/Stack";

import CardActionArea from "@mui/material/CardActionArea";
import Pagination from "./Pagination";
import _ from "lodash";

interface OrderListProps {
    userId: number;
    showAnswerer: boolean;
    filterFinished?: boolean;
    initCurrentPage?: number;
    itemPrePage?: number;
}

const OrderList: React.FC<OrderListProps> = (props) => {
    const [questionList, setQuestionList] = useState<Array<OrderInfo>>();
    const [currentPage, setCurrentPage] = useState(
        _.defaultTo(props.initCurrentPage, 1)
    );
    const [itemPrePage] = useState(_.defaultTo(props.itemPrePage, 20));
    const [maxPage, setMaxPage] = useState(0);
    const [totalCount, setTotalCount] = useState(0);

    const acceptOrderList: (list: PagedList<OrderInfo>) => void = (list) => {
        setQuestionList(list.data);
        setMaxPage(list.totalPages);
        setTotalCount(list.totalCount);
    };

    useEffect(() => {
        if (props.showAnswerer) {
            questionService
                .getOrdersOfUser(
                    undefined,
                    props.userId,
                    currentPage,
                    itemPrePage,
                    props.filterFinished
                )
                .then(acceptOrderList);
        } else {
            questionService
                .getOrdersOfUser(
                    props.userId,
                    undefined,
                    currentPage,
                    itemPrePage,
                    props.filterFinished
                )
                .then(acceptOrderList);
        }
    }, [currentPage, itemPrePage, props.showAnswerer, props.userId]);

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
    return <Stack spacing={2}>{renderQuestionList()}</Stack>;
};

export default OrderList;
