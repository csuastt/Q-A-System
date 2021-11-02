import Button from "@mui/material/Button";
import React, { useEffect, useState } from "react";
import { Link as RouterLink } from "react-router-dom";
import { formatTimestamp, parseIntWithDefault, useQuery } from "../util";
import { OrderInfo, OrderState, PagedList } from "../services/definations";
import orderService from "../services/orderService";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Box from "@mui/material/Box";
import Skeleton from "@mui/material/Skeleton";
import CardActionArea from "@mui/material/CardActionArea";
import Typography from "@mui/material/Typography";
import Avatar from "@mui/material/Avatar";
import Pagination from "./Pagination";
import Stack from "@mui/material/Stack";

interface ReviewListProps {
    filterFinished?: boolean;
    initCurrentPage?: number;
    itemPrePage?: number;
}
const ReviewList: React.FC<ReviewListProps> = (props) => {
    const query = useQuery();
    const [orderList, setOrderList] = useState<Array<OrderInfo>>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const [itemPrePage] = useState(
        parseIntWithDefault(query.get("prepage"), 9)
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [totalCount, setTotalCount] = useState(0);

    const acceptOrderList: (list: PagedList<OrderInfo>) => void = (list) => {
        setOrderList(list.data);
        setMaxPage(list.totalPages);
        setTotalCount(list.totalCount);
    };

    useEffect(() => {
        orderService
            .getOrderListByAdmin(OrderState.CREATED, currentPage, itemPrePage)
            .then(acceptOrderList);
    }, [currentPage, itemPrePage]);

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
    const renderOrderList = () => (
        <>
            {orderList!.map((order: OrderInfo, index: number) => (
                <Card key={index}>
                    <CardContent>
                        <Box
                            sx={{
                                display: "flex",
                                justifyContent: "space-between",
                                p: 1,
                                m: 1,
                                flexDirection: "row",
                            }}
                        >
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

                            <Box
                                sx={{
                                    display: "flex",
                                    flexDirection: "row",
                                    p: 2.5,
                                    m: 1,
                                }}
                            >
                                <Button
                                    size="small"
                                    color="error"
                                    variant="outlined"
                                    onClick={() => {
                                        orderService.reviewOrder(
                                            order.id,
                                            false
                                        );
                                        window.location.reload();
                                    }}
                                >
                                    驳回
                                </Button>
                                <Button
                                    size="small"
                                    color="success"
                                    variant="outlined"
                                    onClick={() => {
                                        orderService.reviewOrder(
                                            order.id,
                                            true
                                        );
                                        window.location.reload();
                                    }}
                                >
                                    通过
                                </Button>
                            </Box>
                        </Box>
                    </CardContent>
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
    if (orderList == null) {
        return (
            <Stack spacing={2} mt={4}>
                {renderCardPlaceholder()}
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
    return <Stack spacing={2}>{renderOrderList()}</Stack>;
};

export default ReviewList;
