import { useTheme } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import LoginIcon from "@mui/icons-material/Login";
import CardActionArea from "@mui/material/CardActionArea";
import { Link as RouterLink } from "react-router-dom";
import React, { useContext, useEffect, useState } from "react";
import SvgIcon from "@mui/material/SvgIcon/SvgIcon";
import AuthContext from "../AuthContext";
import FactCheckIcon from "@mui/icons-material/FactCheck";
import GroupIcon from "@mui/icons-material/Group";
import SchoolIcon from "@mui/icons-material/School";
import LibraryBooksIcon from "@mui/icons-material/LibraryBooks";
import SupervisorAccountIcon from "@mui/icons-material/SupervisorAccount";
import LibraryAddIcon from "@mui/icons-material/LibraryAdd";
import Avatar from "@mui/material/Avatar";
import CardHeader from "@mui/material/CardHeader";
import Grid from "@mui/material/Grid";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import AssignmentIndIcon from "@mui/icons-material/AssignmentInd";
import { AdminStatsInfo, ManagerRole, UserRole } from "../services/definations";
import AssignmentIcon from "@mui/icons-material/Assignment";
import LockOpenIcon from "@mui/icons-material/LockOpen";
import Link from "@mui/material/Link";
import HowToRegIcon from "@mui/icons-material/HowToReg";
import PersonIcon from "@mui/icons-material/Person";
import HomeIcon from "@mui/icons-material/Home";
import IncomeStatistics from "./IncomeStatistics";
import AnswererList from "./AnswererList";
import configService from "../services/configService";

export default function HelloAdmin() {
    const theme = useTheme();
    const { manager } = useContext(AuthContext);
    const [adminStats, setAdminStats] = useState<AdminStatsInfo>();

    useEffect(() => {
        if (manager) {
            configService.getAdminStats().then((info) => {
                setAdminStats(info);
            });
        }
    }, [manager]);

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
                            sx={{
                                width: 50,
                                height: 50,
                                m: 1,
                                bgcolor: "primary.main",
                            }}
                        >
                            {manager ? (
                                manager.role === ManagerRole.SUPER_ADMIN ? (
                                    <SupervisorAccountIcon />
                                ) : manager.role === ManagerRole.ADMIN ? (
                                    <PersonIcon />
                                ) : (
                                    <HowToRegIcon />
                                )
                            ) : null}
                        </Avatar>

                        <Box mt={2}>
                            <Typography color="textPrimary" variant="h5">
                                {manager
                                    ? manager.username === ""
                                        ? "匿名用户"
                                        : manager.username
                                    : "尚未登录"}
                            </Typography>
                        </Box>
                    </Box>
                    <Box>
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
                                                fontSize={16}
                                            >
                                                {manager
                                                    ? manager.role ===
                                                      ManagerRole.SUPER_ADMIN
                                                        ? "超级管理员"
                                                        : manager.role ===
                                                          ManagerRole.ADMIN
                                                        ? "管理员"
                                                        : "审核员"
                                                    : "游客"}
                                            </Box>
                                        </Typography>
                                    }
                                />
                            </ListItem>
                            {manager ? (
                                <></>
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
                                                    to="/admins/login"
                                                >
                                                    登录
                                                </Link>
                                                后开始工作～
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
                                随意转转吧
                            </Typography>
                        </>
                    }
                    sx={{ paddingBottom: 0 }}
                />
                <CardContent sx={{ paddingTop: 1 }}>
                    {manager ? (
                        <>
                            <ButtonCardWrapper
                                to="/admins/users"
                                Icon={GroupIcon}
                                title1="用户列表"
                                title2="查看所有用户"
                            />
                            <ButtonCardWrapper
                                to="/admins/orders"
                                Icon={LibraryBooksIcon}
                                title1="订单列表"
                                title2="查看所有订单"
                            />
                            {manager.role === ManagerRole.REVIEWER ? (
                                <>
                                    <ButtonCardWrapper
                                        to="/admins/review"
                                        Icon={FactCheckIcon}
                                        title1="审核列表"
                                        title2="查看待审核订单"
                                    />
                                </>
                            ) : (
                                <></>
                            )}
                            {manager.role === ManagerRole.SUPER_ADMIN ? (
                                <>
                                    <ButtonCardWrapper
                                        to="/admins/managers"
                                        Icon={HowToRegIcon}
                                        title1="管理员列表"
                                        title2="查看所有管理员"
                                    />
                                </>
                            ) : (
                                <></>
                            )}
                            {manager.role === ManagerRole.ADMIN ? (
                                <>
                                    <ButtonCardWrapper
                                        to="/admins/answers"
                                        Icon={SchoolIcon}
                                        title1="回答者列表"
                                        title2="查看所有回答者"
                                    />
                                </>
                            ) : (
                                <></>
                            )}
                        </>
                    ) : (
                        <>
                            <ButtonCardWrapper
                                to="/admins/login"
                                Icon={LoginIcon}
                                title1="登录"
                                title2="马上开始工作"
                            />
                        </>
                    )}
                </CardContent>
            </Card>
        );
    };

    const drawSystemInfo = () => {
        return (
            <Card>
                <CardHeader
                    title={
                        <>
                            <Typography align="left" variant="h6">
                                平台信息
                            </Typography>
                        </>
                    }
                    subheader={
                        <>
                            {manager ? (
                                <>
                                    <Typography align="left" variant="body2">
                                        平台规模情况
                                    </Typography>
                                </>
                            ) : (
                                <Typography align="left" variant="body2">
                                    请
                                    <Link
                                        variant="body2"
                                        component={RouterLink}
                                        to="/admins/login"
                                    >
                                        登录
                                    </Link>
                                    后查看
                                </Typography>
                            )}
                        </>
                    }
                />

                <CardContent sx={{ paddingTop: 0 }}>
                    {manager ? (
                        <List dense={true} sx={{ paddingBottom: 0 }}>
                            <ListItem sx={{ paddingTop: 0, paddingBottom: 0 }}>
                                <ListItemIcon sx={{ minWidth: 30 }}>
                                    <GroupIcon />
                                </ListItemIcon>
                                <ListItemText
                                    primary={
                                        <Typography
                                            color="textPrimary"
                                            variant="body1"
                                            gutterBottom
                                        >
                                            用户总数：
                                            <Box
                                                component="span"
                                                fontWeight="fontWeightBold"
                                                fontSize={22}
                                            >
                                                {adminStats?.userCount}
                                            </Box>{" "}
                                        </Typography>
                                    }
                                />
                            </ListItem>
                            <ListItem sx={{ paddingTop: 0, paddingBottom: 0 }}>
                                <ListItemIcon sx={{ minWidth: 30 }}>
                                    <SchoolIcon />
                                </ListItemIcon>
                                <ListItemText
                                    primary={
                                        <Typography
                                            color="textPrimary"
                                            variant="body1"
                                            gutterBottom
                                        >
                                            回答者总数：
                                            <Box
                                                component="span"
                                                fontWeight="fontWeightBold"
                                                fontSize={22}
                                            >
                                                {adminStats?.answererCount}
                                            </Box>{" "}
                                        </Typography>
                                    }
                                />
                            </ListItem>
                            <ListItem sx={{ paddingTop: 0, paddingBottom: 0 }}>
                                <ListItemIcon sx={{ minWidth: 30 }}>
                                    <HowToRegIcon />
                                </ListItemIcon>
                                <ListItemText
                                    primary={
                                        <Typography
                                            color="textPrimary"
                                            variant="body1"
                                            gutterBottom
                                        >
                                            管理员总数：
                                            <Box
                                                component="span"
                                                fontWeight="fontWeightBold"
                                                fontSize={22}
                                            >
                                                {adminStats?.adminCount}
                                            </Box>{" "}
                                        </Typography>
                                    }
                                />
                            </ListItem>
                            <ListItem sx={{ paddingTop: 0, paddingBottom: 0 }}>
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
                                            订单总数：
                                            <Box
                                                component="span"
                                                fontWeight="fontWeightBold"
                                                fontSize={22}
                                            >
                                                {adminStats?.orderCount}
                                            </Box>{" "}
                                        </Typography>
                                    }
                                />
                            </ListItem>
                            <ListItem sx={{ paddingTop: 0, paddingBottom: 0 }}>
                                <ListItemIcon sx={{ minWidth: 30 }}>
                                    <LibraryAddIcon />
                                </ListItemIcon>
                                <ListItemText
                                    primary={
                                        <Typography
                                            color="textPrimary"
                                            variant="body1"
                                            gutterBottom
                                        >
                                            问答库收录数：
                                            <Box
                                                component="span"
                                                fontWeight="fontWeightBold"
                                                fontSize={22}
                                            >
                                                {" "}
                                                {adminStats?.publicOrderCount}
                                            </Box>{" "}
                                        </Typography>
                                    }
                                />
                            </ListItem>
                        </List>
                    ) : (
                        <></>
                    )}
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
                                <Link
                                    variant="body2"
                                    component={RouterLink}
                                    to="/admins/answerers"
                                >
                                    点此查看完整列表
                                </Link>
                            </Typography>
                        </>
                    }
                    sx={{ paddingBottom: 0 }}
                />
                <CardContent sx={{ paddingTop: 1 }}>
                    {manager?.role === ManagerRole.SUPER_ADMIN ? (
                        <AnswererList
                            userRole={UserRole.ANSWERER}
                            briefMsg={true}
                            isSuperAdmin={true}
                        />
                    ) : (
                        <AnswererList
                            userRole={UserRole.ANSWERER}
                            briefMsg={true}
                        />
                    )}
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
                打工人打工魂，打工都是人上人
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
                        {manager && manager.role === ManagerRole.SUPER_ADMIN && (
                            <Grid item>
                                <IncomeStatistics
                                    briefMsg={true}
                                    isAdmin={true}
                                />
                            </Grid>
                        )}
                        <Grid item>{drawAnswererList()}</Grid>
                    </Grid>
                </Grid>
                <Grid item md={4} xs={12} mt={2}>
                    <Grid container spacing={4} direction={"column"}>
                        <Grid item>{drawAboutMe()}</Grid>
                        <Grid item>{drawFastJump()}</Grid>
                        <Grid item>{drawSystemInfo()}</Grid>
                    </Grid>
                </Grid>
            </Grid>
        </>
    );
}
