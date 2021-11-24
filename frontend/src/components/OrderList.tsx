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
import { useTheme } from "@mui/material/styles";
import {
    Button,
    Dialog,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
} from "@mui/material";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import TrendingDownIcon from "@mui/icons-material/TrendingDown";
import DialogContent from "@mui/material/DialogContent";
import DialogActions from "@mui/material/DialogActions";

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
    purchased?: boolean;
    alertHandler?: (msg: string) => void;
    setRedirect?: (path: string) => void;
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
    const [sortProperty, setSortProperty] = useState(
        _.defaultTo(props.initSortProperty, "createTime")
    );
    const [sortOrder, setSortOrder] = useState(
        _.defaultTo(props.initSortOrder, "DESC")
    );
    const [open, setOpen] = useState(false);
    const [chosenOrder, setChosenOrder] = useState(-1);

    // Sync props change
    useEffect(() => {
        setCurrentPage(_.defaultTo(props.initCurrentPage, 1));
        setSortProperty(_.defaultTo(props.initSortProperty, "createTime"));
        setSortOrder(_.defaultTo(props.initSortOrder, "DESC"));
    }, [props.initCurrentPage, props.initSortOrder, props.initSortProperty]);

    const handleOpen = (id: number) => {
        setOpen(true);
        setChosenOrder(id);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleSubmit = () => {
        handleClose();
        questionService.purchaseOrder(chosenOrder).then(
            () => {
                if (props.setRedirect)
                    props.setRedirect(`/orders/${chosenOrder}`);
            },
            (error) => {
                if (error.response.status === 403) {
                    if (props.alertHandler) props.alertHandler("余额不足");
                }
            }
        );
    };

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
            if (typeof props.purchased !== "undefined") {
                fetchPromise = questionService.getOrderListPurchased(
                    currentPage,
                    itemPrePage,
                    sortOrder,
                    sortProperty
                );
            } else if (props.showAnswerer) {
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
    }, [
        currentPage,
        itemPrePage,
        props.filterFinished,
        props.showAnswerer,
        props.userId,
        props.keywords,
        props.purchased,
        props.setMillis,
        props.setCount,
        sortOrder,
        sortProperty,
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
        if (props.setCurrentPage) props.setCurrentPage(newPage);
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

    const CardActionWrapper: React.FC<{ order: OrderInfo }> = (
        wrapperProps
    ) => {
        return wrapperProps.order.publicPrice === 0 ||
            typeof props.userId === "undefined" ||
            props.userId === wrapperProps.order.answerer.id ||
            props.userId === wrapperProps.order.asker.id ||
            props.purchased ||
            (typeof wrapperProps.order.purchased !== "undefined" &&
                wrapperProps.order.purchased) ? (
            <CardActionArea
                component={RouterLink}
                to={`/orders/${wrapperProps.order.id}`}
            >
                {wrapperProps.children}
            </CardActionArea>
        ) : (
            <CardActionArea
                onClick={() => {
                    handleOpen(wrapperProps.order.id);
                }}
            >
                {wrapperProps.children}
            </CardActionArea>
        );
    };

    const renderExpireTime = (order: OrderInfo) => {
        if (
            [
                OrderState.REVIEWED,
                OrderState.ACCEPTED,
                OrderState.ANSWERED,
                OrderState.CHAT_ENDED,
            ].indexOf(order.state) !== -1 &&
            matches
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
        if (order.state === OrderState.ANSWERED && matches) {
            return (
                <Stack direction={"row"} alignItems="center" spacing={1}>
                    <ChatIcon
                        fontSize="small"
                        sx={matches ? { ml: 1 } : { ml: 0 }}
                        color="info"
                    />
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
                    <CardActionWrapper order={order}>
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
                                        noWrap={matches}
                                        style={{ fontWeight: 600 }}
                                    >
                                        {order.questionTitle}
                                    </Typography>
                                    <Box sx={{ paddingRight: 1 }} />
                                    {matches &&
                                        (order.showPublic ? (
                                            <PublicIcon color={"primary"} />
                                        ) : (
                                            <PrivacyTipIcon
                                                color={"secondary"}
                                            />
                                        ))}
                                    <Box sx={{ flexGrow: 1 }} />
                                    {!props.listMode && matches && (
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
                                    {typeof props.keywords !== "undefined" && (
                                        <>
                                            <Box sx={{ flexGrow: 1 }} />
                                            <Typography
                                                variant="body1"
                                                color="primary"
                                                style={{ fontWeight: 600 }}
                                                sx={{ mr: 1 }}
                                            >
                                                {"￥" +
                                                    order.publicPrice +
                                                    "/次"}
                                            </Typography>
                                        </>
                                    )}
                                </Box>
                                <Box
                                    sx={
                                        matches
                                            ? {
                                                  display: "flex",
                                                  flexDirection: "row",
                                                  mt: 1,
                                                  mb: -1,
                                              }
                                            : {
                                                  display: "flex",
                                                  flexDirection: "column",
                                                  mt: 1,
                                                  mb: -1,
                                              }
                                    }
                                    alignItems={
                                        matches ? "center" : "flex-start"
                                    }
                                >
                                    <Typography variant="caption">
                                        创建时间：
                                        {formatTimestamp(order.createTime)}
                                    </Typography>
                                    <Box ml={1} />
                                    {order.state === OrderState.CHAT_ENDED ||
                                    order.state === OrderState.FULFILLED ? (
                                        order.rating === 0 ? (
                                            <Typography
                                                variant="caption"
                                                color="error"
                                            >
                                                未评价
                                            </Typography>
                                        ) : (
                                            <Box
                                                sx={{
                                                    display: "flex",
                                                    flexDirection: "row",
                                                }}
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
                                        )
                                    ) : (
                                        <Typography variant="caption">
                                            暂无评分
                                        </Typography>
                                    )}
                                    <Box sx={{ flexGrow: 1 }} />
                                    {renderExpireTime(order)}
                                    {renderMsgCount(order)}
                                </Box>
                            </Box>
                        </CardContentWrapper>
                    </CardActionWrapper>
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
                        ? typeof props.purchased === "undefined"
                            ? "您还没有订单"
                            : "您还没有已购买的问题"
                        : "没有找到匹配的结果"}
                </Typography>
            </Box>
        );
    }
    return (
        <Box>
            {questionList && (
                <>
                    {!props.listMode && typeof props.keywords === "undefined" && (
                        <Stack
                            direction={"row"}
                            justifyContent="flex-start"
                            alignItems="center"
                            spacing={2}
                            mb={2}
                        >
                            <FormControl
                                variant="outlined"
                                sx={{ minWidth: 120 }}
                                size={"small"}
                            >
                                <InputLabel id="demo-simple-select-label">
                                    排序依据
                                </InputLabel>
                                <Select
                                    labelId="demo-simple-select-label"
                                    id="demo-simple-select"
                                    value={sortProperty}
                                    label="sort-property"
                                    onChange={(e) => {
                                        setSortProperty(e.target.value);
                                    }}
                                >
                                    <MenuItem value={"createTime"}>
                                        创建时间
                                    </MenuItem>
                                    <MenuItem value={"expireTime"}>
                                        超时时间
                                    </MenuItem>
                                    <MenuItem value={"price"}>
                                        成交价格
                                    </MenuItem>
                                    <MenuItem value={"messageCount"}>
                                        聊天条数
                                    </MenuItem>
                                    <MenuItem value={"rating"}>
                                        订单评分
                                    </MenuItem>
                                    <MenuItem value={"state"}>
                                        订单状态
                                    </MenuItem>
                                </Select>
                            </FormControl>
                            <Button
                                startIcon={
                                    sortOrder === "ASC" ? (
                                        <TrendingUpIcon />
                                    ) : (
                                        <TrendingDownIcon />
                                    )
                                }
                                onClick={() => {
                                    sortOrder === "ASC"
                                        ? setSortOrder("DESC")
                                        : setSortOrder("ASC");
                                }}
                                size={"large"}
                            >
                                {sortOrder === "ASC" ? "升序" : "降序"}
                            </Button>
                        </Stack>
                    )}
                    <Stack spacing={2}>{renderQuestionList()}</Stack>
                    <Dialog fullWidth open={open} onClose={handleClose}>
                        <DialogTitle>确认支付</DialogTitle>
                        <DialogContent>您是否确认支付此问题？</DialogContent>
                        <DialogActions>
                            <Button onClick={handleSubmit}>确定</Button>
                            <Button onClick={handleClose} color={"error"}>
                                取消
                            </Button>
                        </DialogActions>
                    </Dialog>
                </>
            )}
        </Box>
    );
};

export default OrderList;
