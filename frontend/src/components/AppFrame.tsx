import * as React from "react";
import { useContext } from "react";
import { Link as RouterLink } from "react-router-dom";
import { CSSObject, styled, Theme, useTheme } from "@mui/material/styles";
import MuiAppBar, { AppBarProps as MuiAppBarProps } from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import MenuIcon from "@mui/icons-material/Menu";
import MuiDrawer from "@mui/material/Drawer";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import CssBaseline from "@mui/material/CssBaseline";
import FaceRetouchingNaturalIcon from "@mui/icons-material/FaceRetouchingNatural";
import {
    Badge,
    FormControlLabel,
    ListItemButton,
    SwipeableDrawer,
    Switch,
} from "@mui/material";
import LoginIcon from "@mui/icons-material/Login";
import { makeStyles } from "@mui/styles";
import HomeIcon from "@mui/icons-material/Home";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import LogoutIcon from "@mui/icons-material/Logout";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import GroupIcon from "@mui/icons-material/Group";
import AddCommentIcon from "@mui/icons-material/AddComment";
import EqualizerIcon from "@mui/icons-material/Equalizer";
import FactCheckIcon from "@mui/icons-material/FactCheck";
import LibraryBooksIcon from "@mui/icons-material/LibraryBooks";
import SettingsIcon from "@mui/icons-material/Settings";
import SchoolIcon from "@mui/icons-material/School";
import NotificationsIcon from "@mui/icons-material/Notifications";
import LocalMallIcon from "@mui/icons-material/LocalMall";
import AuthContext from "../AuthContext";

import RateReviewIcon from "@mui/icons-material/RateReview";
import { ManagerRole, UserRole } from "../services/definations";
import VpnKeyIcon from "@mui/icons-material/VpnKey";
import QuestionAnswerIcon from "@mui/icons-material/QuestionAnswer";
import HelpIcon from "@mui/icons-material/Help";
import HowToRegIcon from "@mui/icons-material/HowToReg";
import { useNotification } from "./NotificationController";
import { ColorModeContext } from "../App";
import LocalLibraryIcon from "@mui/icons-material/LocalLibrary";
import useMediaQuery from "@mui/material/useMediaQuery";

const drawerWidth = 240;

const drawerOpenedMixin = (theme: Theme): CSSObject => ({
    width: drawerWidth,
    transition: theme.transitions.create("width", {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
    overflowX: "hidden",
});

const drawerClosedMixin = (theme: Theme): CSSObject => ({
    transition: theme.transitions.create("width", {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    overflowX: "hidden",
    width: `calc(${theme.spacing(7)} + 1px)`,
    [theme.breakpoints.up("sm")]: {
        width: `calc(${theme.spacing(9)} + 1px)`,
    },
});

const DrawerHeader = styled("div")(({ theme }) => ({
    display: "flex",
    alignItems: "center",
    justifyContent: "flex-end",
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
}));

const Drawer = styled(MuiDrawer, {
    shouldForwardProp: (prop) => prop !== "open",
})(({ theme, open }) => ({
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: "nowrap",
    boxSizing: "border-box",
    ...(open && {
        ...drawerOpenedMixin(theme),
        "& .MuiDrawer-paper": drawerOpenedMixin(theme),
    }),
    ...(!open && {
        ...drawerClosedMixin(theme),
        "& .MuiDrawer-paper": drawerClosedMixin(theme),
    }),
}));

const useStyles = makeStyles({
    paper: {
        width: 240,
    },
});

interface AppBarProps extends MuiAppBarProps {
    open?: boolean;
}

const AppBar = styled(MuiAppBar, {
    shouldForwardProp: (prop) => prop !== "open",
})<AppBarProps>(({ theme, open }) => ({
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(["width", "margin"], {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    ...(open && {
        marginLeft: drawerWidth,
        width: `calc(100% - ${drawerWidth}px)`,
        transition: theme.transitions.create(["width", "margin"], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.enteringScreen,
        }),
    }),
}));

const AppFrame: React.FC<{ isAdmin: boolean }> = (props) => {
    const [drawerOpen, setDrawerOpen] = React.useState(false);
    const { user, manager } = useContext(AuthContext);
    const { toggleColorMode } = useContext(ColorModeContext);
    const { unreadCount } = useNotification();

    const theme = useTheme();
    const matches = useMediaQuery(theme.breakpoints.up("md"));
    const classes = useStyles();
    const handleDrawerOpen = () => {
        setDrawerOpen(true);
    };

    const handleDrawerClose = () => {
        setDrawerOpen(false);
    };

    const handleDrawerToggle = () => {
        setDrawerOpen(!drawerOpen);
    };

    const renderListItem = (
        key: string,
        text: string,
        to: string,
        icon: React.ReactNode
    ) => (
        <ListItemButton key={key} component={RouterLink} to={to}>
            <ListItemIcon>{icon}</ListItemIcon>
            <ListItemText primary={text} />
        </ListItemButton>
    );

    const renderDrawerList = (
        data: Array<[string, string, React.ReactNode]>
    ) => (
        <List>
            {data.map(([text, to, icon]) =>
                renderListItem(text, text, to, icon)
            )}
        </List>
    );

    const drawerList3: () => Array<[string, string, React.ReactNode]> = () => {
        if (user == null) {
            return [
                ["问答库", "/lib", <LocalLibraryIcon />],
                ["回答者列表", "/answerers", <SchoolIcon />],
            ];
        }
        if (user.role === UserRole.USER) {
            return [
                ["问答库", "/lib", <LocalLibraryIcon />],
                ["已购买提问", "/purchased", <LocalMallIcon />],
                ["回答者列表", "/answerers", <SchoolIcon />],
                ["我的提问", "/orders", <QuestionAnswerIcon />],
                ["提出问题", "/order/create", <AddCommentIcon />],
            ];
        }
        return [
            ["问答库", "/lib", <LocalLibraryIcon />],
            ["已购买提问", "/purchased", <LocalMallIcon />],
            ["回答者列表", "/answerers", <SchoolIcon />],
            ["我的提问", "/orders", <QuestionAnswerIcon />],
            ["我的回答", "/orders?answerer=true", <RateReviewIcon />],
            ["收入统计", "/income", <EqualizerIcon />],
            ["提出问题", "/order/create", <AddCommentIcon />],
        ];
    };

    const renderDrawerContent = (
        <>
            <DrawerHeader>
                <IconButton onClick={handleDrawerClose}>
                    {theme.direction === "rtl" ? (
                        <ChevronRightIcon />
                    ) : (
                        <ChevronLeftIcon />
                    )}
                </IconButton>
            </DrawerHeader>
            <Divider />
            {renderDrawerList(
                props.isAdmin
                    ? [["管理员主页", "/admins/", <HomeIcon />]]
                    : [["主页", "/", <HomeIcon />]]
            )}
            <Divider />
            {renderDrawerList(
                props.isAdmin
                    ? manager
                        ? [
                              [
                                  "修改密码",
                                  "/admins/change_password",
                                  <VpnKeyIcon />,
                              ],
                              ["登出", "/admins/logout", <LogoutIcon />],
                          ]
                        : [["登录", "/admins/login", <LoginIcon />]]
                    : user
                    ? [["登出", "/logout", <LogoutIcon />]]
                    : [
                          ["登录", "/login", <LoginIcon />],
                          ["注册", "/register", <PersonAddIcon />],
                      ]
            )}
            <Divider />
            {props.isAdmin
                ? renderDrawerList([
                      ["审核列表", "/admins/review", <FactCheckIcon />],
                      [
                          "审核回答者列表",
                          "/admins/usersReview",
                          <FaceRetouchingNaturalIcon />,
                      ],
                      ["用户列表", "/admins/users", <GroupIcon />],
                      ["回答者列表", "/admins/answerers", <SchoolIcon />],
                      ["订单列表", "/admins/orders", <LibraryBooksIcon />],
                  ])
                : renderDrawerList(drawerList3())}
            <Divider />
            {manager?.role === ManagerRole.SUPER_ADMIN
                ? renderDrawerList([
                      ["系统参数", "/admins/settings", <SettingsIcon />],
                      ["管理员列表", "/admins/managers", <HowToRegIcon />],
                      ["创建", "/admins/create", <PersonAddIcon />],
                      ["收入统计", "/admins/income", <EqualizerIcon />],
                  ])
                : renderDrawerList([["平台须知", "/help", <HelpIcon />]])}
        </>
    );

    return (
        <Box sx={{ display: "flex" }}>
            <CssBaseline />{" "}
            <AppBar open={matches ? drawerOpen : undefined}>
                <Toolbar>
                    <IconButton
                        size="large"
                        edge="start"
                        color="inherit"
                        aria-label="open drawer"
                        sx={
                            matches
                                ? {
                                      mr: 5,
                                      ...(drawerOpen && { display: "none" }),
                                  }
                                : { mr: 5 }
                        }
                        onClick={handleDrawerToggle}
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography
                        variant="h6"
                        noWrap
                        component="div"
                        sx={{
                            flexGrow: 1,
                            display: { xs: "none", sm: "block" },
                        }}
                    >
                        {props.isAdmin ? "问客管理员系统" : "问客"}
                    </Typography>
                    <Box sx={{ flexGrow: 1 }} />
                    {!props.isAdmin && user && (
                        <>
                            <IconButton
                                component={RouterLink}
                                to={"/profile"}
                                color="inherit"
                                size="large"
                                sx={{ marginRight: -1 }}
                            >
                                <AccountCircleIcon />
                            </IconButton>
                            <IconButton
                                component={RouterLink}
                                to={"/notif"}
                                color="inherit"
                                size="large"
                                sx={{ marginRight: 1 }}
                            >
                                <Badge
                                    badgeContent={unreadCount}
                                    color="warning"
                                >
                                    <NotificationsIcon />
                                </Badge>
                            </IconButton>
                        </>
                    )}
                    {!props.isAdmin && (
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={theme.palette.mode === "dark"}
                                    onChange={toggleColorMode}
                                    inputProps={{ "aria-label": "controlled" }}
                                />
                            }
                            label="夜间模式"
                        />
                    )}
                </Toolbar>
            </AppBar>
            {matches ? (
                <Drawer variant="permanent" open={drawerOpen}>
                    {renderDrawerContent}
                </Drawer>
            ) : (
                <SwipeableDrawer
                    open={drawerOpen}
                    onClose={handleDrawerClose}
                    onOpen={handleDrawerOpen}
                    classes={{ paper: classes.paper }}
                >
                    {renderDrawerContent}
                </SwipeableDrawer>
            )}
            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <DrawerHeader />
                {props.children}
            </Box>
        </Box>
    );
};

export default AppFrame;
