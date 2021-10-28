import * as React from "react";
import { useContext } from "react";
import { Link as RouterLink } from "react-router-dom";
import {
    alpha,
    CSSObject,
    styled,
    Theme,
    useTheme,
} from "@mui/material/styles";
import MuiAppBar, { AppBarProps as MuiAppBarProps } from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import InputBase from "@mui/material/InputBase";
import MenuIcon from "@mui/icons-material/Menu";
import SearchIcon from "@mui/icons-material/Search";
import MuiDrawer from "@mui/material/Drawer";
import ChevronRightIcon from "@mui/icons-material/ChevronRight";
import ChevronLeftIcon from "@mui/icons-material/ChevronLeft";
import Divider from "@mui/material/Divider";
import List from "@mui/material/List";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import CssBaseline from "@mui/material/CssBaseline";
import { ListItemButton } from "@mui/material";
import SvgIcon from "@mui/material/SvgIcon/SvgIcon";
import LoginIcon from "@mui/icons-material/Login";
import HomeIcon from "@mui/icons-material/Home";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import LogoutIcon from "@mui/icons-material/Logout";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import HelpOutlineIcon from "@mui/icons-material/HelpOutline";
import GroupIcon from "@mui/icons-material/Group";
import FormatListBulletedIcon from "@mui/icons-material/FormatListBulleted";
import FactCheckIcon from "@mui/icons-material/FactCheck";
import LibraryBooksIcon from "@mui/icons-material/LibraryBooks";
import SettingsIcon from '@mui/icons-material/Settings';
import SchoolIcon from '@mui/icons-material/School';

import AuthContext from "../AuthContext";

const Search = styled("div")(({ theme }) => ({
    position: "relative",
    borderRadius: theme.shape.borderRadius,
    backgroundColor: alpha(theme.palette.common.white, 0.15),
    "&:hover": {
        backgroundColor: alpha(theme.palette.common.white, 0.25),
    },
    marginLeft: 0,
    width: "100%",
    [theme.breakpoints.up("sm")]: {
        marginLeft: theme.spacing(1),
        width: "auto",
    },
}));

const SearchIconWrapper = styled("div")(({ theme }) => ({
    padding: theme.spacing(0, 2),
    height: "100%",
    position: "absolute",
    pointerEvents: "none",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
}));

const StyledInputBase = styled(InputBase)(({ theme }) => ({
    color: "inherit",
    "& .MuiInputBase-input": {
        padding: theme.spacing(1, 1, 1, 0),
        // vertical padding + font size from searchIcon
        paddingLeft: `calc(1em + ${theme.spacing(4)})`,
        transition: theme.transitions.create("width"),
        width: "100%",
        [theme.breakpoints.up("sm")]: {
            width: "12ch",
            "&:focus": {
                width: "20ch",
            },
        },
    },
}));

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
    const { user,manager } = useContext(AuthContext);

    const theme = useTheme();

    const handleDrawerOpen = () => {
        setDrawerOpen(true);
    };

    const handleDrawerClose = () => {
        setDrawerOpen(false);
    };

    const renderListItem = (
        key: string,
        text: string,
        to: string,
        IconType: typeof SvgIcon
    ) => (
        <ListItemButton key={key} component={RouterLink} to={to}>
            <ListItemIcon>
                <IconType />
            </ListItemIcon>
            <ListItemText primary={text} />
        </ListItemButton>
    );

    const renderDrawerList = (
        data: Array<[string, string, typeof SvgIcon]>
    ) => (
        <List>
            {data.map(([text, to, IconType]) =>
                renderListItem(text, text, to, IconType)
            )}
        </List>
    );

    return (
        <Box sx={{ display: "flex" }}>
            <CssBaseline />{" "}
            {props.isAdmin ? (
                <AppBar open={drawerOpen} style={{ background: "#714288" }}>
                    <Toolbar>
                        <IconButton
                            size="large"
                            edge="start"
                            color="inherit"
                            aria-label="open drawer"
                            sx={{
                                mr: 5,
                                ...(drawerOpen && { display: "none" }),
                            }}
                            onClick={handleDrawerOpen}
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
                            付费问答管理员系统
                        </Typography>
                    </Toolbar>
                </AppBar>
            ) : (
                <AppBar open={drawerOpen}>
                    <Toolbar>
                        <IconButton
                            size="large"
                            edge="start"
                            color="inherit"
                            aria-label="open drawer"
                            sx={{
                                mr: 5,
                                ...(drawerOpen && { display: "none" }),
                            }}
                            onClick={handleDrawerOpen}
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
                            付费问答系统
                        </Typography>
                        <Search>
                            <SearchIconWrapper>
                                <SearchIcon />
                            </SearchIconWrapper>
                            <StyledInputBase
                                placeholder="搜索问题…"
                                inputProps={{ "aria-label": "search" }}
                            />
                        </Search>
                    </Toolbar>
                </AppBar>
            )}
            <Drawer variant="permanent" open={drawerOpen}>
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
                        ? [["管理员主页", "/admins/", HomeIcon]]
                        : [["主页", "/", HomeIcon]]
                )}
                <Divider />
                {renderDrawerList(
                    props.isAdmin
                        ? manager
                            ? [
                                  [
                                      "管理员信息",
                                      "/admins/profile",
                                      AccountCircleIcon,
                                  ],
                                  ["登出", "/admins/logout", LogoutIcon],
                              ]
                            : [
                                  ["登录", "/admins/login", LoginIcon],
                                  ["创建", "/admins/create", PersonAddIcon],
                              ]
                        : user
                        ? [
                              ["个人信息", "/profile", AccountCircleIcon],
                              ["登出", "/logout", LogoutIcon],
                          ]
                        : [
                              ["登录", "/login", LoginIcon],
                              ["注册", "/register", PersonAddIcon],
                          ]
                )}
                <Divider />
                {renderDrawerList(
                    props.isAdmin
                        ? [
                              ["审核列表", "/admins/review", FactCheckIcon],
                              ["用户列表", "/admins/users", GroupIcon],
                              ["回答者列表", "/admins/answerers", SchoolIcon],
                              ["订单列表", "/admins/orders", LibraryBooksIcon],
                          ]
                        : [
                              ["回答者列表", "/answerers", GroupIcon],
                              ["订单列表", "/orders", FormatListBulletedIcon],
                              ["提出问题", "/order/create", HelpOutlineIcon],
                          ]
                )}
                <Divider />
                {renderDrawerList(
                    props.isAdmin
                        ? [["系统参数", "/admins/settings", SettingsIcon]]
                        :[["设置", "/settings", SettingsIcon]]

                )}

            </Drawer>

            <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
                <DrawerHeader />
                {props.children}
            </Box>
        </Box>
    );
};

export default AppFrame;
