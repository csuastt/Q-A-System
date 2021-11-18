import React, { useEffect, useState } from "react";
import { Link as RouterLink } from "react-router-dom";
import { formatTimestamp, parseIntWithDefault, useQuery } from "../util";
import {
    OrderInfo,
    OrderState,
    PagedList,
    SortDirection,
} from "../services/definations";
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
import OrderStateChip from "./OrderStateChip";
import userService from "../services/userService";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";
import { ToggleButton } from "@mui/material";
import Button from "@mui/material/Button";
import ArrowDownwardIcon from "@mui/icons-material/ArrowDownward";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";

interface AdminOrderListProps {
    orderState?: OrderState;
    filterFinished?: boolean;
    initCurrentPage?: number;
    itemPrePage?: number;
}
const AdminOrderList: React.FC<AdminOrderListProps> = (props) => {
    const query = useQuery();
    const [orderList, setOrderList] = useState<Array<OrderInfo>>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const [itemPrePage] = useState(
        parseIntWithDefault(query.get("prepage"), 10)
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [longPending, setLongPending] = useState(false);
    const [totalCount, setTotalCount] = useState(0);
    const [filterReviewed, setFilterReviewed] = useState(false);

    const acceptOrderList: (list: PagedList<OrderInfo>) => void = (list) => {
        setOrderList(list.data);
        setMaxPage(list.totalPages);
        setTotalCount(list.totalCount);
    };

    useEffect(() => {
        props.orderState
            ? orderService.getOrderListByAdmin(
                  props.orderState,
                  currentPage,
                  itemPrePage
              )
            : orderService
                  .getAllOrderListByAdmin(currentPage, itemPrePage)
                  .then(acceptOrderList);
        setTimeout(() => {
            setLongPending(true);
        }, 500);
    }, [currentPage, itemPrePage, props.orderState]);

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };

    const timeAsc = () => {
        if (filterReviewed) {
            orderService
                .getAllOrderListByAdmin(
                    currentPage,
                    itemPrePage,
                    SortDirection.ASC,
                    OrderState.CREATED
                )
                .then(acceptOrderList);
        } else {
            orderService
                .getAllOrderListByAdmin(
                    currentPage,
                    itemPrePage,
                    SortDirection.ASC
                )
                .then(acceptOrderList);
        }
    };
    const timeDesc = () => {
        if (filterReviewed) {
            orderService
                .getAllOrderListByAdmin(
                    currentPage,
                    itemPrePage,
                    SortDirection.DESC,
                    OrderState.CREATED
                )
                .then(acceptOrderList);
        } else {
            orderService
                .getAllOrderListByAdmin(
                    currentPage,
                    itemPrePage,
                    SortDirection.DESC
                )
                .then(acceptOrderList);
        }
    };

    const onFilterChanged = (
        event: React.MouseEvent<HTMLElement>,
        value: boolean
    ) => {
        if (value) {
            setFilterReviewed(value);
            orderService
                .getAllOrderListByAdmin(
                    currentPage,
                    itemPrePage,
                    SortDirection.ASC,
                    OrderState.CREATED
                )
                .then(acceptOrderList);
        } else {
            setFilterReviewed(value);
            orderService
                .getAllOrderListByAdmin(
                    currentPage,
                    itemPrePage,
                    SortDirection.ASC
                )
                .then(acceptOrderList);
        }
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
                    <CardActionArea
                        component={RouterLink}
                        to={`/admins/orders/${order.id}`}
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
                                        {order.questionTitle}
                                    </Typography>
                                    <Box sx={{ flexGrow: 1 }} />
                                    <OrderStateChip state={order.state} />
                                </Box>
                                <Box
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                    }}
                                    mt={1}
                                >
                                    <Avatar
                                        src={userService.getAvatarUrl(
                                            order.answerer.id
                                        )}
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
    if (longPending && orderList == null) {
        return (
            <Stack spacing={2} mt={4}>
                {renderCardPlaceholder()}
            </Stack>
        );
    }
    if (orderList && totalCount === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有订单
            </Typography>
        );
    }
    return (
        <>
            <Stack direction="row" spacing={3} mt={3}>
                <ToggleButtonGroup
                    value={filterReviewed}
                    exclusive
                    onChange={onFilterChanged}
                    size="small"
                >
                    <ToggleButton value={true}>等待审核</ToggleButton>
                    <ToggleButton value={false}>显示全部</ToggleButton>
                </ToggleButtonGroup>
                <Button
                    onClick={timeAsc}
                    startIcon={<ArrowUpwardIcon />}
                    color="success"
                    variant="outlined"
                    size="small"
                >
                    时间顺序
                </Button>
                <Button
                    onClick={timeDesc}
                    startIcon={<ArrowDownwardIcon />}
                    color="warning"
                    variant="outlined"
                    size="small"
                >
                    时间倒序
                </Button>
            </Stack>

            <Box marginTop={1.4}>
                {orderList && <Stack spacing={2}>{renderOrderList()}</Stack>}
            </Box>
        </>
    );
};

export default AdminOrderList;
