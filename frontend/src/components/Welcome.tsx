import { useTheme } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import CardActionArea from "@mui/material/CardActionArea";
import { Link as RouterLink } from "react-router-dom";
import React, { useContext, useEffect, useState } from "react";
import SvgIcon from "@mui/material/SvgIcon/SvgIcon";
import AuthContext from "../AuthContext";
import { UserRole } from "../services/definations";
import HomeIcon from "@mui/icons-material/Home";
import IncomeStatistics from "./IncomeStatistics";
import Avatar from "@mui/material/Avatar";
import CardHeader from "@mui/material/CardHeader";
import Grid from "@mui/material/Grid";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import Stack from "@mui/material/Stack";
import AssignmentIndIcon from "@mui/icons-material/AssignmentInd";
import AssignmentIcon from "@mui/icons-material/Assignment";
import AssignmentTurnedInIcon from "@mui/icons-material/AssignmentTurnedIn";
import CreditCardIcon from "@mui/icons-material/CreditCard";
import Link from "@mui/material/Link";
import AddCommentIcon from "@mui/icons-material/AddComment";
import QuestionAnswerIcon from "@mui/icons-material/QuestionAnswer";
import RateReviewIcon from "@mui/icons-material/RateReview";
import { AccountCircle } from "@mui/icons-material";
import LoginIcon from "@mui/icons-material/Login";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import userService from "../services/userService";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import MoneyIcon from "@mui/icons-material/Money";
import AnswererList from "./AnswererList";
import Library from "./Library";
import NotificationList from "./NotificationList";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";

export default function Welcome() {
    const theme = useTheme();
    const { user } = useContext(AuthContext);
    const [askCount, setAskCount] = useState<number>(0);
    const [answerCount, setAnswerCount] = useState<number>(0);

    useEffect(() => {
        if (user) {
            userService.getUserStats(user.id).then((info) => {
                setAskCount(info.askCount);
                setAnswerCount(info.answerCount);
            });
        }
    }, [user]);

    const ButtonCardWrapper: React.FC<{
        to: string;
        Icon: typeof SvgIcon;
        title1: string;
        title2: string;
    }> = (props) => (
        <Card
            sx={{
                display: "flex",
            }}
            style={{ border: "none", boxShadow: "none" }}
        >
            <CardActionArea component={RouterLink} to={props.to}>
                <CardContent sx={{ padding: 1 }}>
                    <Box sx={{ display: "flex", flexDirection: "row" }}>
                        <props.Icon
                            sx={{
                                margin: theme.spacing(2, 2, 2, 0),
                            }}
                        />
                        <Box sx={{ flexDirection: "column" }}>
                            <Typography
                                component="div"
                                variant="body1"
                                fontWeight={600}
                            >
                                {props.title1}
                            </Typography>
                            <Typography
                                variant="subtitle1"
                                color="text.secondary"
                                component="div"
                            >
                                {props.title2}
                            </Typography>
                        </Box>
                    </Box>
                </CardContent>
            </CardActionArea>
        </Card>
    );

    const drawAboutMe = () => {
        return (
            <Card>
                <CardHeader
                    title={
                        <>
                            <Typography align="left" variant="h6">
                                关于我
                            </Typography>
                        </>
                    }
                    subheader={
                        <>
                            <Typography align="left" variant="body2">
                                下方显示了您的基本情况
                            </Typography>
                        </>
                    }
                />
                <CardContent>
                    <Box
                        sx={{
                            alignItems: "center",
                            display: "flex",
                            flexDirection: "column",
                        }}
                    >
                        <Avatar
                            src={user ? userService.getAvatarUrl(user.id) : ""}
                            alt={user ? user.username : ""}
                            sx={{
                                height: 80,
                                width: 80,
                                fontSize: 40,
                            }}
                        />
                        <Box mt={2}>
                            <Typography color="textPrimary" variant="h5">
                                {user
                                    ? user.nickname === ""
                                        ? "匿名用户"
                                        : user.nickname
                                    : "尚未登录"}
                            </Typography>
                        </Box>
                    </Box>
                    <Box
                        sx={{
                            alignItems: "center",
                            display: "flex",
                            flexDirection: "column",
                        }}
                    >
                        <List dense={true} sx={{ paddingBottom: 0 }}>
                            <ListItem sx={{ paddingBottom: 0 }}>
                                <ListItemIcon sx={{ minWidth: 30 }}>
                                    <AssignmentIndIcon />
                                </ListItemIcon>
                                <ListItemText
                                    primary={
                                        <Typography
                                            color="textPrimary"
                                            variant="body1"
                                            gutterBottom
                                        >
                                            您的身份：
                                            <Box
                                                component="span"
                                                fontWeight="fontWeightBold"
                                                fontSize={22}
                                            >
                                                {user
                                                    ? user.role ===
                                                      UserRole.ANSWERER
                                                        ? "回答者"
                                                        : "提问者"
                                                    : "游客"}
                                            </Box>
                                        </Typography>
                                    }
                                />
                            </ListItem>
                            {user ? (
                                <>
                                    <ListItem
                                        sx={{ paddingTop: 0, paddingBottom: 0 }}
                                    >
                                        <ListItemIcon sx={{ minWidth: 30 }}>
                                            <AssignmentIcon />
                                        </ListItemIcon>
                                        <ListItemText
                                            primary={
                                                <Typography
                                                    color="textPrimary"
                                                    variant="body1"
                                                    gutterBottom
                                                >
                                                    您提问了：
                                                    <Box
                                                        component="span"
                                                        fontWeight="fontWeightBold"
                                                        fontSize={22}
                                                    >
                                                        {askCount}
                                                    </Box>{" "}
                                                    单
                                                </Typography>
                                            }
                                        />
                                    </ListItem>
                                    <ListItem
                                        sx={{ paddingTop: 0, paddingBottom: 0 }}
                                    >
                                        <ListItemIcon sx={{ minWidth: 30 }}>
                                            <AssignmentTurnedInIcon />
                                        </ListItemIcon>
                                        <ListItemText
                                            primary={
                                                <Typography
                                                    color="textPrimary"
                                                    variant="body1"
                                                    gutterBottom
                                                >
                                                    您回答了：
                                                    <Box
                                                        component="span"
                                                        fontWeight="fontWeightBold"
                                                        fontSize={22}
                                                    >
                                                        {answerCount}
                                                    </Box>{" "}
                                                    单
                                                </Typography>
                                            }
                                        />
                                    </ListItem>
                                    <ListItem
                                        sx={{ paddingTop: 0, paddingBottom: 0 }}
                                    >
                                        <ListItemIcon sx={{ minWidth: 30 }}>
                                            <CreditCardIcon />
                                        </ListItemIcon>
                                        <ListItemText
                                            primary={
                                                <Typography
                                                    color="textPrimary"
                                                    variant="body1"
                                                    gutterBottom
                                                >
                                                    您的余额为：￥
                                                    <Box
                                                        component="span"
                                                        fontWeight="fontWeightBold"
                                                        fontSize={22}
                                                    >
                                                        {user.balance}
                                                    </Box>
                                                </Typography>
                                            }
                                        />
                                    </ListItem>
                                    {user.role === UserRole.ANSWERER && (
                                        <ListItem
                                            sx={{
                                                paddingTop: 0,
                                                paddingBottom: 0,
                                            }}
                                        >
                                            <ListItemIcon sx={{ minWidth: 30 }}>
                                                <MoneyIcon />
                                            </ListItemIcon>
                                            <ListItemText
                                                primary={
                                                    <Typography
                                                        color="textPrimary"
                                                        variant="body1"
                                                        gutterBottom
                                                    >
                                                        您的定价为：￥
                                                        <Box
                                                            component="span"
                                                            fontWeight="fontWeightBold"
                                                            fontSize={22}
                                                        >
                                                            {user.price}
                                                        </Box>
                                                    </Typography>
                                                }
                                            />
                                        </ListItem>
                                    )}
                                </>
                            ) : (
                                <ListItem
                                    sx={{ paddingTop: 0, paddingBottom: 1.1 }}
                                >
                                    <ListItemIcon sx={{ minWidth: 30 }}>
                                        <LockOpenIcon />
                                    </ListItemIcon>
                                    <ListItemText
                                        primary={
                                            <Typography
                                                color="textPrimary"
                                                variant="body1"
                                            >
                                                请
                                                <Link
                                                    variant="body1"
                                                    component={RouterLink}
                                                    to="/login"
                                                >
                                                    登录
                                                </Link>
                                                后解锁更多信息
                                            </Typography>
                                        }
                                    />
                                </ListItem>
                            )}
                        </List>
                    </Box>
                </CardContent>
            </Card>
        );
    };

    const drawMsgList = () => {
        return (
            <Card>
                <CardHeader
                    title={
                        <>
                            <Typography align="left" variant="h6">
                                我的消息
                            </Typography>
                        </>
                    }
                    subheader={
                        <>
                            {user ? (
                                <>
                                    <Typography align="left" variant="body2">
                                        这是您的消息列表，
                                        <Link
                                            variant="body2"
                                            component={RouterLink}
                                            to="/notif"
                                        >
                                            点此查看更多
                                        </Link>
                                    </Typography>
                                </>
                            ) : (
                                <Typography align="left" variant="body2">
                                    请
                                    <Link
                                        variant="body2"
                                        component={RouterLink}
                                        to="/login"
                                    >
                                        登录
                                    </Link>
                                    后查看您的消息
                                </Typography>
                            )}
                        </>
                    }
                />
                <CardContent>
                    {user ? (
                        <NotificationList compact />
                    ) : (
                        <Stack
                            direction="column"
                            justifyContent="center"
                            alignItems="center"
                            spacing={2}
                        >
                            <ErrorOutlineIcon
                                color="warning"
                                sx={{ fontSize: 60 }}
                            />
                            <Typography variant={"h6"} mt={1} mb={4}>
                                {"请登录后重试"}
                            </Typography>
                        </Stack>
                    )}
                </CardContent>
            </Card>
        );
    };

    const drawFastJump = () => {
        return (
            <Card>
                <CardHeader
                    title={
                        <>
                            <Typography align="left" variant="h6">
                                快速跳转
                            </Typography>
                        </>
                    }
                    subheader={
                        <>
                            <Typography align="left" variant="body2">
                                您可以随意探索平台~
                            </Typography>
                        </>
                    }
                    sx={{ paddingBottom: 0 }}
                />
                <CardContent sx={{ paddingTop: 1 }}>
                    {user ? (
                        <>
                            <ButtonCardWrapper
                                to="/order/create"
                                Icon={AddCommentIcon}
                                title1="提出问题"
                                title2="吾将上下而求索"
                            />
                            <ButtonCardWrapper
                                to="/orders"
                                Icon={QuestionAnswerIcon}
                                title1="我的提问"
                                title2="查看历史提问"
                            />
                            {user.role === UserRole.ANSWERER ? (
                                <>
                                    <ButtonCardWrapper
                                        to="/orders?answerer=true"
                                        Icon={RateReviewIcon}
                                        title1="我的回答"
                                        title2="查看历史回答"
                                    />
                                </>
                            ) : (
                                <></>
                            )}
                            <ButtonCardWrapper
                                to="/profile"
                                Icon={AccountCircle}
                                title1="个人信息"
                                title2="查看、修改个人信息"
                            />
                        </>
                    ) : (
                        <>
                            <ButtonCardWrapper
                                to="/login"
                                Icon={LoginIcon}
                                title1="登录"
                                title2="分享知识，马上开始"
                            />
                            <ButtonCardWrapper
                                to="/register"
                                Icon={PersonAddIcon}
                                title1="注册"
                                title2="受助与助人"
                            />
                        </>
                    )}
                </CardContent>
            </Card>
        );
    };

    const drawLibrary = () => {
        return (
            <Card>
                <CardHeader
                    title={
                        <>
                            <Typography align="left" variant="h6">
                                问答库
                            </Typography>
                        </>
                    }
                    subheader={
                        <>
                            <Typography align="left" variant="body2">
                                请浏览问答库，
                                <Link
                                    variant="body2"
                                    component={RouterLink}
                                    to="/lib"
                                >
                                    点此查看完整列表
                                </Link>
                                {!user && (
                                    <>
                                        {"（"}
                                        <Link
                                            variant="body2"
                                            component={RouterLink}
                                            to="/login"
                                        >
                                            登录
                                        </Link>
                                        {"后才可查看详情哦~）"}
                                    </>
                                )}
                            </Typography>
                        </>
                    }
                    sx={{ paddingBottom: 0 }}
                />
                <CardContent sx={{ paddingTop: 1 }}>
                    <Library briefMsg={true} />
                </CardContent>
            </Card>
        );
    };

    const drawAnswererList = () => {
        return (
            <Card>
                <CardHeader
                    title={
                        <>
                            <Typography align="left" variant="h6">
                                回答者列表
                            </Typography>
                        </>
                    }
                    subheader={
                        <>
                            <Typography align="left" variant="body2">
                                您可以随意提问，
                                <Link
                                    variant="body2"
                                    component={RouterLink}
                                    to="/answerers"
                                >
                                    点此查看完整列表
                                </Link>
                                {!user && (
                                    <>
                                        {"（"}
                                        <Link
                                            variant="body2"
                                            component={RouterLink}
                                            to="/login"
                                        >
                                            登录
                                        </Link>
                                        {"后才可提问哦~）"}
                                    </>
                                )}
                            </Typography>
                        </>
                    }
                    sx={{ paddingBottom: 0 }}
                />
                <CardContent sx={{ paddingTop: 1 }}>
                    <AnswererList
                        userRole={UserRole.ANSWERER}
                        briefMsg={true}
                        selectModel={typeof user !== "undefined"}
                    />
                </CardContent>
            </Card>
        );
    };

    return (
        <>
            <Typography
                align="left"
                variant="h6"
                sx={{
                    margin: theme.spacing(1, 2, 1, 0),
                    fontWeight: 600,
                }}
            >
                Hi，是求知让我们相聚于此
            </Typography>
            <Box>
                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        flexWrap: "wrap",
                    }}
                >
                    <HomeIcon color={"primary"} />
                    <span style={{ marginLeft: 5 }}>/ 主页</span>
                </div>
            </Box>
            <Grid container spacing={4}>
                <Grid item md={8} xs={12} mt={2}>
                    <Grid container spacing={4} direction={"column"}>
                        {user && user.role === UserRole.ANSWERER && (
                            <Grid item>
                                <IncomeStatistics
                                    briefMsg={true}
                                    user={user}
                                    isAdmin={false}
                                />
                            </Grid>
                        )}
                        <Grid item>{drawLibrary()}</Grid>
                        <Grid item>{drawAnswererList()}</Grid>
                    </Grid>
                </Grid>
                <Grid item md={4} xs={12} mt={2}>
                    <Grid container spacing={4} direction={"column"}>
                        <Grid item>{drawAboutMe()}</Grid>
                        <Grid item>{drawFastJump()}</Grid>
                        <Grid item>{drawMsgList()}</Grid>
                    </Grid>
                </Grid>
            </Grid>
        </>
    );
}
