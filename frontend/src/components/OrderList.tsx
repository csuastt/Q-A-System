import React, { Dispatch, SetStateAction, useEffect, useState } from "react";
import Avatar from "@mui/material/Avatar";
import Skeleton from "@mui/material/Skeleton";
import { Link as RouterLink } from "react-router-dom";
import { OrderInfo, OrderState, PagedList } from "../services/definations";
import questionService from "../services/orderService";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { formatTimestamp } from "../util";
import Stack from "@mui/material/Stack";
import AlarmIcon from "@mui/icons-material/Alarm";
import ChatIcon from "@mui/icons-material/Chat";
import CardActionArea from "@mui/material/CardActionArea";
import Pagination from "./Pagination";
import _ from "lodash";
import OrderStateChip from "./OrderStateChip";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import userService from "../services/userService";
import PrivacyTipIcon from "@mui/icons-material/PrivacyTip";
import PublicIcon from "@mui/icons-material/Public";
import styled from "@emotion/styled";
import systemConfigService from "../services/systemConfigService";
import Rating from "@mui/material/Rating";
import useMediaQuery from "@mui/material/useMediaQuery";
import {useTheme} from "@mui/material/styles";
import {Button, FormControl, InputLabel, MenuItem, Select} from "@mui/material";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import TrendingDownIcon from "@mui/icons-material/TrendingDown";

interface OrderListProps {
    userId?: number;
    showAnswerer?: boolean;
    keywords?: string;
    setMillis?: Dispatch<SetStateAction<number>>;
    setCount?: Dispatch<SetStateAction<number>>;
    setCurrentPage?: Dispatch<SetStateAction<number>>;
    filterFinished?: boolean;
    initCurrentPage?: number;
    itemPrePage?: number;
    initSortOrder?: string;
    initSortProperty?: string;
    listMode?: boolean;
}

const OrderList: React.FC<OrderListProps> = (props) => {
    const [questionList, setQuestionList] = useState<Array<OrderInfo>>();
    const [currentPage, setCurrentPage] = useState(
        _.defaultTo(props.initCurrentPage, 1)
    );
    const [itemPrePage] = useState(_.defaultTo(props.itemPrePage, 20));
    const [maxPage, setMaxPage] = useState(0);
    const [totalCount, setTotalCount] = useState(0);
    const [longPending, setLongPending] = useState(false);
    const [errorFlag, setErrorFlag] = useState(false);
    const [maxMsgCount, setMaxMsgCount] = useState<number>();
    const [sortProperty, setSortProperty] = useState(_.defaultTo(props.initSortProperty, "createTime"));
    const [sortOrder, setSortOrder] = useState(_.defaultTo(props.initSortOrder, "DESC"));

    // if match the mobile size
    const theme = useTheme();
    const matches = useMediaQuery(theme.breakpoints.up("md"));

    const acceptOrderList: (list: PagedList<OrderInfo>) => void = (list) => {
        setQuestionList(list.data);
        setMaxPage(list.totalPages);
        setTotalCount(list.totalCount);
    };

    useEffect(() => {
        if (typeof props.keywords !== "undefined") {
            const setMillis = props.setMillis;
            const setCount = props.setCount;
            questionService
                .getPublicOrderListBySearch(
                    props.keywords,
                    currentPage,
                    itemPrePage,
                    sortOrder,
                    sortProperty
                )
                .then(
                    (res) => {
                        acceptOrderList({
                            data: res.data,
                            pageSize: res.pageSize,
                            page: res.page,
                            totalPages: res.totalPages,
                            totalCount: res.totalCount,
                        } as PagedList<OrderInfo>);
                        if (setMillis) setMillis(res.timeMillis);
                        if (setCount) setCount(res.totalCount);
                    },
                    () => {
                        setErrorFlag(true);
                    }
                );
        } else {
            let fetchPromise: Promise<PagedList<OrderInfo>>;
            if (props.showAnswerer) {
                fetchPromise = questionService.getOrdersOfUser(
                    undefined,
                    props.userId,
                    currentPage,
                    itemPrePage,
                    props.filterFinished,
                    sortOrder,
                    sortProperty
                );
            } else {
                fetchPromise = questionService.getOrdersOfUser(
                    props.userId,
                    undefined,
                    currentPage,
                    itemPrePage,
                    props.filterFinished,
                    sortOrder,
                    sortProperty
                );
            }
            fetchPromise.then(acceptOrderList, () => {
                setErrorFlag(true);
            });
        }
        setTimeout(() => {
            setLongPending(true);
        }, 500);
    }, [currentPage, itemPrePage, props.filterFinished, props.showAnswerer, props.userId, props.keywords, props.setMillis, props.setCount, sortOrder, sortProperty]);

    useEffect(() => {
        systemConfigService.getSystemConfig().then(
            (config) => {
                setMaxMsgCount(config.maxChatMessages);
            },
            () => {}
        );
    }, []);

    const onPageChanged = (newPage: number) => {
        if (props.setCurrentPage)
            props.setCurrentPage(newPage);
        else {
            setCurrentPage(newPage);
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

    const renderPlaceholder = () => (
        <>
            {renderCardPlaceholder()}
            {renderCardPlaceholder()}
        </>
    );

    const CardContentNoPadding = styled(CardContent)(`
      padding: 10px;
      &:last-child {
        padding-bottom: 0;
      }
    `);

    const CardContentWrapper: React.FC<{}> = (wrapperProps) => {
        return props.listMode ? (
            <CardContentNoPadding>{wrapperProps.children}</CardContentNoPadding>
        ) : (
            <CardContent>{wrapperProps.children}</CardContent>
        );
    };

    const renderExpireTime = (order: OrderInfo) => {
        if (
            [
                OrderState.REVIEWED,
                OrderState.ACCEPTED,
                OrderState.ANSWERED,
                OrderState.CHAT_ENDED,
            ].indexOf(order.state) !== -1
        ) {
            return (
                <Stack direction={"row"} alignItems="center" spacing={1}>
                    <AlarmIcon fontSize="small" color="warning" />
                    <Typography variant="caption" color="warning">
                        {formatTimestamp(order.expireTime)}
                    </Typography>
                </Stack>
            );
        }
    };

    const renderMsgCount = (order: OrderInfo) => {
        if (order.state === OrderState.ANSWERED) {
            return (
                <Stack direction={"row"} alignItems="center" spacing={1}>
                    <ChatIcon fontSize="small" sx={
                        matches? { ml: 1 } : { ml: 0 }
                    } color="info" />
                    <Typography variant="caption" color="info">
                        {maxMsgCount
                            ? `${order.messageCount}/${maxMsgCount}`
                            : `${order.messageCount}`}
                    </Typography>
                </Stack>
            );
        }
    };

    const renderQuestionList = () => (
        <>
            {questionList!.map((order: OrderInfo, index: number) => (
                <Card
                    key={index}
                    style={
                        props.listMode
                            ? { border: "none", boxShadow: "none" }
                            : {}
                    }
                >
                    <CardActionArea
                        component={RouterLink}
                        to={`/orders/${order.id}`}
                    >
                        <CardContentWrapper>
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
                                    alignItems="center"
                                >
                                    <Typography
                                        variant={
                                            props.listMode ? "body1" : "h6"
                                        }
                                        noWrap
                                        style={{ fontWeight: 600 }}
                                    >
                                        {order.questionTitle}
                                    </Typography>
                                    <Box sx={{ paddingRight: 1 }} />
                                    {matches && (order.showPublic ? (
                                        <PublicIcon color={"primary"} />
                                    ) : (
                                        <PrivacyTipIcon color={"secondary"} />
                                    ))}
                                    <Box sx={{ flexGrow: 1 }} />
                                    {!props.listMode && (
                                        <OrderStateChip state={order.state} />
                                    )}
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
                                        sx={{
                                            width: 30,
                                            height: 30,
                                            fontSize: 15,
                                        }}
                                    />
                                    <Typography
                                        variant="subtitle1"
                                        sx={{ ml: 1 }}
                                    >
                                        {order.answerer.nickname}
                                    </Typography>
                                </Box>
                                <Box
                                    sx={
                                        matches ?{
                                        display: "flex",
                                        flexDirection: "row",
                                        mt: 1,
                                        mb: -1,
                                        }:
                                            {
                                                display: "flex",
                                                flexDirection: "column",
                                                mt: 1,
                                                mb: -1,
                                            }
                                    }
                                    alignItems={matches ? "center": "flex-start"}
                                >
                                    <Typography variant="caption">
                                        创建时间：
                                        {formatTimestamp(order.createTime)}
                                    </Typography>
                                    <Box ml={1} />
                                    {(order.state === OrderState.CHAT_ENDED ||
                                        order.state === OrderState.FULFILLED) &&
                                        (order.rating === 0 ? (
                                            <Typography
                                                variant="caption"
                                                color="error"
                                            >
                                                未评价
                                            </Typography>
                                        ) : (
                                            <Box sx={{display: "flex",
                                                flexDirection: "row",}}
                                                 justifyContent="center"
                                            >
                                                <Typography variant="caption">
                                                    评分：
                                                </Typography>
                                                <Rating
                                                    value={order.rating}
                                                    readOnly
                                                    size="small"
                                                />
                                            </Box>
                                        ))}
                                    <Box sx={{ flexGrow: 1 }} />
                                    {renderExpireTime(order)}
                                    {renderMsgCount(order)}
                                </Box>
                            </Box>
                        </CardContentWrapper>
                    </CardActionArea>
                </Card>
            ))}
            {maxPage > 1 && !props.listMode && (
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

    if (errorFlag) {
        return <Typography>加载失败</Typography>;
    }
    if (longPending && questionList == null) {
        return (
            <Stack spacing={2} mt={4}>
                {renderPlaceholder()}
            </Stack>
        );
    }
    if (questionList && totalCount === 0) {
        return (
            <Box
                textAlign={"center"}
                mt={typeof props.keywords === "undefined" ? 6 : 3}
            >
                <ErrorOutlineIcon color="warning" sx={{ fontSize: 80 }} />
                <Typography variant={"h5"} mt={1} mb={4}>
                    {typeof props.keywords === "undefined"
                        ? "您还没有订单"
                        : "没有找到匹配的结果"}
                </Typography>
            </Box>
        );
    }
    return (
        <Box>
            {questionList &&
                <>
                    {
                        !props.listMode && typeof props.keywords === "undefined" &&
                        <Stack
                            direction={"row"}
                            justifyContent="flex-start"
                            alignItems="center"
                            spacing={2}
                            mb={2}
                        >
                            <FormControl variant="outlined" sx={{ minWidth: 120 }} size={"small"}>
                                <InputLabel id="demo-simple-select-label">排序依据</InputLabel>
                                <Select
                                    labelId="demo-simple-select-label"
                                    id="demo-simple-select"
                                    value={sortProperty}
                                    label="sort-property"
                                    onChange={(e) => {setSortProperty(e.target.value);}}
                                >
                                    <MenuItem value={"createTime"}>创建时间</MenuItem>
                                    <MenuItem value={"expireTime"}>超时时间</MenuItem>
                                    <MenuItem value={"price"}>成交价格</MenuItem>
                                    <MenuItem value={"messageCount"}>聊天条数</MenuItem>
                                    <MenuItem value={"rating"}>订单评分</MenuItem>
                                    <MenuItem value={"state"}>订单状态</MenuItem>
                                </Select>
                            </FormControl>
                            <Button
                                startIcon={sortOrder === "ASC" ? <TrendingUpIcon/> : <TrendingDownIcon/>}
                                onClick={() => {(sortOrder === "ASC") ? setSortOrder("DESC") : setSortOrder("ASC");}}
                                size={"large"}
                            >
                                {sortOrder === "ASC" ? "升序" : "降序"}
                            </Button>
                        </Stack>
                    }
                    <Stack spacing={2}>{renderQuestionList()}</Stack>
                </>
            }
        </Box>
    );
};

export default OrderList;
