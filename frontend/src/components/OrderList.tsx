import React, { useEffect, useState } from "react";
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

interface OrderListProps {
    userId?: number;
    showAnswerer?: boolean;
    keywords?: string;
    filterFinished?: boolean;
    initCurrentPage?: number;
    itemPrePage?: number;
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

    const acceptOrderList: (list: PagedList<OrderInfo>) => void = (list) => {
        setQuestionList(list.data);
        setMaxPage(list.totalPages);
        setTotalCount(list.totalCount);
    };

    useEffect(() => {
        let fetchPromise: Promise<PagedList<OrderInfo>>;
        if (typeof props.keywords !== "undefined") {
            fetchPromise = questionService.getPublicOrderListBySearch(
                props.keywords,
                currentPage,
                itemPrePage
            );
        } else if (props.showAnswerer) {
            fetchPromise = questionService.getOrdersOfUser(
                undefined,
                props.userId,
                currentPage,
                itemPrePage,
                props.filterFinished
            );
        } else {
            fetchPromise = questionService.getOrdersOfUser(
                props.userId,
                undefined,
                currentPage,
                itemPrePage,
                props.filterFinished
            );
        }
        fetchPromise.then(acceptOrderList, () => {
            setErrorFlag(true);
        });
        setTimeout(() => {
            setLongPending(true);
        }, 500);
    }, [
        currentPage,
        itemPrePage,
        props.filterFinished,
        props.showAnswerer,
        props.userId,
        props.keywords,
    ]);

    useEffect(() => {
        systemConfigService.getSystemConfig().then(
            (config) => {
                setMaxMsgCount(config.maxChatMessages);
            },
            () => {}
        );
    }, []);

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
                <>
                    <AlarmIcon fontSize="small" color="warning" />
                    <Typography variant="caption" color="warning">
                        {formatTimestamp(order.expireTime)}
                    </Typography>
                </>
            );
        }
    };

    const renderMsgCount = (order: OrderInfo) => {
        if (order.state === OrderState.ANSWERED) {
            return (
                <>
                    <ChatIcon fontSize="small" sx={{ ml: 1 }} color="info" />
                    <Typography variant="caption" color="info">
                        {maxMsgCount
                            ? `${order.messageCount}/${maxMsgCount}`
                            : `${order.messageCount}`}
                    </Typography>
                </>
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
                                    {order.showPublic ? (
                                        <PublicIcon color={"primary"} />
                                    ) : (
                                        <PrivacyTipIcon color={"secondary"} />
                                    )}
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
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                        mt: 1,
                                        mb: -1,
                                    }}
                                    alignItems="center"
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
                                            <>
                                                <Typography variant="caption">
                                                    评分：
                                                </Typography>
                                                <Rating
                                                    value={order.rating}
                                                    readOnly
                                                    size="small"
                                                />
                                            </>
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
            {questionList && <Stack spacing={2}>{renderQuestionList()}</Stack>}
        </Box>
    );
};

export default OrderList;
