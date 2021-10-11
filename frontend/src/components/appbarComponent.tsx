import * as React from "react";
import { Link as RouterLink } from "react-router-dom";
import { alpha, styled } from "@mui/material/styles";
import AppBar from "@mui/material/AppBar";
import Box from "@mui/material/Box";
import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import Typography from "@mui/material/Typography";
import InputBase from "@mui/material/InputBase";
import MenuItem from "@mui/material/MenuItem";
import Menu from "@mui/material/Menu";
import MenuIcon from "@mui/icons-material/Menu";
import SearchIcon from "@mui/icons-material/Search";
import AccountCircle from "@mui/icons-material/AccountCircle";
import NotificationsIcon from "@mui/icons-material/Notifications";
import MoreIcon from "@mui/icons-material/MoreVert";
import LoginIcon from "@mui/icons-material/Login";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import LogoutIcon from "@mui/icons-material/Logout";
import authService from "../services/auth.service";

const Search = styled("div")(({ theme }) => ({
    position: "relative",
    borderRadius: theme.shape.borderRadius,
    backgroundColor: alpha(theme.palette.common.white, 0.15),
    "&:hover": {
        backgroundColor: alpha(theme.palette.common.white, 0.25),
    },
    marginRight: theme.spacing(2),
    marginLeft: 0,
    width: "100%",
    [theme.breakpoints.up("sm")]: {
        marginLeft: theme.spacing(3),
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
        [theme.breakpoints.up("md")]: {
            width: "20ch",
        },
    },
}));

const Appbar: React.FC<{ isAuthenticated: boolean }> = (props) => {
    const [mobileMoreAnchorEl, setMobileMoreAnchorEl] =
        React.useState<null | HTMLElement>(null);

    const isMobileMenuOpen = Boolean(mobileMoreAnchorEl);

    const handleMobileMenuClose = () => {
        setMobileMoreAnchorEl(null);
    };

    const handleMobileMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
        setMobileMoreAnchorEl(event.currentTarget);
    };

    const IconButtonWrapper: React.FC<{ to: string }> = (props) => {
        return (
            <IconButton
                size="large"
                color="inherit"
                component={RouterLink}
                to={props.to}
            >
                {props.children}
            </IconButton>
        );
    };

    const renderUserRelatedButtons = () => {
        const user = authService.getCurrentUser();
        return !props.isAuthenticated ? (
            <>
                <IconButtonWrapper to="/login">
                    <LoginIcon />
                </IconButtonWrapper>
                <IconButtonWrapper to="/register">
                    <PersonAddIcon />
                </IconButtonWrapper>
            </>
        ) : (
            <>
                <Typography
                    variant="h6"
                    noWrap
                    component="div"
                    sx={{
                        margin: "auto",
                        marginRight: 1.5
                    }}
                >
                    欢迎，{user.username}
                </Typography>
                <IconButtonWrapper to={"/user/" + user.id + "/notifications"}>
                    <NotificationsIcon />
                </IconButtonWrapper>
                <IconButtonWrapper to={"/user/" + user.id + "/info"}>
                    <AccountCircle />
                </IconButtonWrapper>
                <IconButtonWrapper to={"/logout"}>
                    <LogoutIcon />
                </IconButtonWrapper>
            </>
        );
    };

    const mobileMenuId = "primary-search-account-menu-mobile";
    const renderMobileMenu = () => {
        const user = authService.getCurrentUser();
        return (
            <Menu
                anchorEl={mobileMoreAnchorEl}
                anchorOrigin={{
                    vertical: "top",
                    horizontal: "right",
                }}
                id={mobileMenuId}
                keepMounted
                transformOrigin={{
                    vertical: "top",
                    horizontal: "right",
                }}
                open={isMobileMenuOpen}
                onClose={handleMobileMenuClose}
            >
                {!props.isAuthenticated ? (
                    <>
                        <MenuItem>
                            <IconButtonWrapper to="/login">
                                <LoginIcon />
                            </IconButtonWrapper>
                            <p>登陆</p>
                        </MenuItem>
                        <MenuItem>
                            <IconButtonWrapper to="/register">
                                <PersonAddIcon />
                            </IconButtonWrapper>
                            <p>注册</p>
                        </MenuItem>
                    </>
                ) : (
                    <>
                        <MenuItem>
                            <IconButtonWrapper
                                to={"/user/" + user.id + "/notifications"}
                            >
                                <NotificationsIcon />
                            </IconButtonWrapper>
                            <p>通知中心</p>
                        </MenuItem>
                        <MenuItem>
                            <IconButtonWrapper to={"/profile"}>
                                <AccountCircle />
                            </IconButtonWrapper>
                            <p>用户信息</p>
                        </MenuItem>
                        <MenuItem>
                            <IconButtonWrapper to={"/logout"}>
                                <LogoutIcon />
                            </IconButtonWrapper>
                            <p>退出登录</p>
                        </MenuItem>
                    </>
                )}
            </Menu>
        );
    };

    return (
        <Box sx={{ flexGrow: 1 }}>
            <AppBar>
                <Toolbar>
                    <IconButton
                        size="large"
                        edge="start"
                        color="inherit"
                        aria-label="open drawer"
                        sx={{ mr: 2 }}
                        component={RouterLink}
                        to="/"
                    >
                        <MenuIcon />
                    </IconButton>
                    <Typography
                        variant="h6"
                        noWrap
                        component="div"
                        sx={{ display: { xs: "none", sm: "block" } }}
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
                    <Box sx={{ flexGrow: 1 }} />
                    <Box sx={{ display: { xs: "none", md: "flex" } }}>
                        {renderUserRelatedButtons()}
                    </Box>
                    <Box sx={{ display: { xs: "flex", md: "none" } }}>
                        <IconButton
                            size="large"
                            aria-label="show more"
                            aria-controls={mobileMenuId}
                            aria-haspopup="true"
                            onClick={handleMobileMenuOpen}
                            color="inherit"
                        >
                            <MoreIcon />
                        </IconButton>
                    </Box>
                </Toolbar>
            </AppBar>
            {renderMobileMenu()}
            <Toolbar />
        </Box>
    );
}

export default Appbar;