import React, { Component, useContext } from "react";
import AuthService from "../services/authService";
import AdminAuthService from "../services/adminAuthService";
import { Link as RouterLink, Redirect } from "react-router-dom";

// mui
import Snackbar from "@mui/material/Snackbar";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import SupervisedUserCircleIcon from "@mui/icons-material/SupervisedUserCircle";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import Alert from "@mui/material/Alert";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import UserContext from "../UserContext";
import ManagerContext from "../ManagerContext";

// some validators
// not empty
export const validate_required = (value: any) => {
    if (!value) {
        return "此处不能为空";
    } else {
        return "";
    }
};
// eslint-disable-next-line react-hooks/rules-of-hooks
const managerContext = useContext(ManagerContext);
// eslint-disable-next-line react-hooks/rules-of-hooks
const userContext = useContext(UserContext);

// 6 to 12 in length
export const validate_length = (value: any) => {
    if (value.toString().trim().length < 6) {
        // too long
        return "长度不能小于6个字符";
    } else if (value.toString().trim().length > 12) {
        // too short
        return "长度不能大于12个字符";
    } else {
        return "";
    }
};

// state interface
interface LoginState {
    username: string;
    password: string;
    error_msg_username: string;
    error_msg_password: string;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
    redirect: string | null;
    openDialog: boolean;
}

// props interface
interface LoginProps {
    isAdmin: boolean;
    redirect: string;
}

export default class Login extends Component<LoginProps, LoginState> {
    constructor(props: any) {
        super(props);
        // handle login info
        this.handleLogin = this.handleLogin.bind(this);
        this.handleCloseDialog = this.handleCloseDialog.bind(this);
        this.handleOpenDialog = this.handleOpenDialog.bind(this);
        // state
        this.state = {
            username: "",
            password: "",
            error_msg_username: "",
            error_msg_password: "",
            alert: false,
            alertContent: "",
            alertType: "error",
            redirect: null,
            openDialog: false,
        };
    }

    // listener on username/password
    onChangeValue(e: any, type: "username" | "password") {
        const value = e.target.value;
        // first validate not empty
        let error = validate_required(value);
        if (error === "") {
            error = validate_length(value);
        }
        // set new state
        const nextState = {};
        // @ts-ignore
        nextState[type] = value;
        // @ts-ignore
        nextState["error_msg_" + type] = error;
        this.setState(nextState);
        return error === "";
    }

    handleLogin(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();

        // validate all the info
        if (
            this.onChangeValue(
                { target: { value: this.state.username } },
                "username"
            ) &&
            this.onChangeValue(
                { target: { value: this.state.password } },
                "password"
            )
        ) {
            let service;
            if (this.props.isAdmin) {
                service = AdminAuthService;
                // login request
                service.login(this.state.username, this.state.password).then(
                    (manager) => {
                        // login success
                        // alert
                        this.setState({
                            alert: true,
                            alertType: "success",
                            alertContent: "管理员登录成功",
                        });
                        // update state
                        managerContext.setManager(manager);

                        // redirect
                        this.setState({
                            redirect: this.props.redirect,
                        });
                    },
                    (error) => {
                        // show the error message
                        if (error.response.status === 403) {
                            this.setState({
                                alert: true,
                                alertType: "error",
                                alertContent: "管理员名称或密码错误",
                            });
                        } else {
                            this.setState({
                                alert: true,
                                alertType: "error",
                                alertContent: "网络错误",
                            });
                        }
                    }
                );
            } else {
                service = AuthService;
                // login request
                service.login(this.state.username, this.state.password).then(
                    (user) => {
                        // login success
                        // alert
                        this.setState({
                            alert: true,
                            alertType: "success",
                            alertContent: "登录成功",
                        });
                        // update state
                        userContext.setUser(user);

                        // redirect
                        this.setState({
                            redirect: this.props.redirect,
                        });
                    },
                    (error) => {
                        // show the error message
                        if (error.response.status === 403) {
                            this.setState({
                                alert: true,
                                alertType: "error",
                                alertContent: "用户名或密码错误",
                            });
                        } else {
                            this.setState({
                                alert: true,
                                alertType: "error",
                                alertContent: "网络错误",
                            });
                        }
                    }
                );
            }
        }
    }

    handleOpenDialog() {
        this.setState({ openDialog: true });
    }

    handleCloseDialog() {
        this.setState({ openDialog: false });
    }

    render() {
        if (this.state.redirect) {
            return <Redirect to={this.state.redirect} />;
        }

        return (
            <Container component="main" maxWidth="xs">
                <CssBaseline />
                <Box
                    sx={{
                        marginTop: 3,
                        marginBottom: 4,
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                    }}
                >
                    <Avatar sx={{ m: 1, bgcolor: "primary.main" }}>
                        {this.props.isAdmin ? (
                            <SupervisedUserCircleIcon />
                        ) : (
                            <LockOutlinedIcon />
                        )}
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        {this.props.isAdmin ? "管理员登录" : "登录"}
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={this.handleLogin}
                        noValidate
                        sx={{ mt: 0 }}
                    >
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="username"
                            label={this.props.isAdmin ? "管理员名称" : "用户名"}
                            name="username"
                            autoComplete="username"
                            autoFocus
                            onChange={(e) => this.onChangeValue(e, "username")}
                            // @ts-ignore
                            error={this.state.error_msg_username.length !== 0}
                            // @ts-ignore
                            helperText={this.state.error_msg_username}
                            inputProps={{ maxLength: 15 }}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label={this.props.isAdmin ? "管理员密码" : "密码"}
                            type="password"
                            id="password"
                            autoComplete="current-password"
                            onChange={(e) => this.onChangeValue(e, "password")}
                            // @ts-ignore
                            error={this.state.error_msg_password.length !== 0}
                            // @ts-ignore
                            helperText={this.state.error_msg_password}
                            inputProps={{ maxLength: 15 }}
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            登录
                        </Button>
                    </Box>
                    <Grid container justifyContent="flex-end">
                        <Grid item>
                            {this.props.isAdmin ? (
                                <Link
                                    variant="body2"
                                    component="button"
                                    onClick={this.handleOpenDialog}
                                >
                                    还没有账号？请联系超级管理员
                                </Link>
                            ) : (
                                <Link
                                    variant="body2"
                                    component={RouterLink}
                                    to="/register"
                                >
                                    还没有账号？快速注册
                                </Link>
                            )}
                        </Grid>
                    </Grid>
                </Box>
                <Dialog
                    open={this.state.openDialog}
                    onClose={this.handleCloseDialog}
                    aria-labelledby="alert-dialog-title"
                    aria-describedby="alert-dialog-description"
                >
                    <DialogTitle id="alert-dialog-title">
                        {"如何成为管理员"}
                    </DialogTitle>
                    <DialogContent>
                        <DialogContentText id="alert-dialog-description">
                            在当前机制下，管理员无法自行注册。只有超级管理员才能添加其他管理员。
                            如果您想成为管理员，请联系超级管理员将您添加到管理员列表中。
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseDialog} autoFocus>
                            我知道了
                        </Button>
                    </DialogActions>
                </Dialog>
                <Snackbar
                    autoHideDuration={2000}
                    open={this.state.alert}
                    onClose={() => {
                        this.setState({ alert: false });
                    }}
                    anchorOrigin={{
                        vertical: "bottom",
                        horizontal: "center",
                    }}
                    sx={{ width: "30%" }}
                >
                    <Alert
                        severity={this.state.alertType}
                        sx={{ width: "100%" }}
                    >
                        {this.state.alertContent}
                    </Alert>
                </Snackbar>
            </Container>
        );
    }
}

//Login.contextType = UserContext;
