import React, { Component } from "react";
import AuthService from "../services/auth.service";
// mui
import Snackbar from "@mui/material/Snackbar";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import FormControlLabel from "@mui/material/FormControlLabel";
import Checkbox from "@mui/material/Checkbox";
import Link from "@mui/material/Link";
import Grid from "@mui/material/Grid";
import Box from "@mui/material/Box";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import Alert from "@mui/material/Alert";

import { Link as RouterLink } from "react-router-dom";

// some validators
// not empty
export const validate_required = (value: any) => {
    if (!value) {
        return "此处不能为空";
    } else {
        return "";
    }
};
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
}

export default class Login extends Component<any, LoginState> {
    constructor(props: any) {
        super(props);
        // handle login info
        this.handleLogin = this.handleLogin.bind(this);
        // state
        this.state = {
            username: "",
            password: "",
            error_msg_username: "",
            error_msg_password: "",
            alert: false,
            alertContent: "",
            alertType: "error",
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
            // login request
            AuthService.login(this.state.username, this.state.password).then(
                () => {
                    // login success
                    // todo jump to sw
                    // alert
                    this.setState({
                        alert: true,
                        alertType: "success",
                        alertContent: "Login success!",
                    });
                },
                (error) => {
                    // retrieve error
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();
                    // show the error message
                    this.setState({
                        alert: true,
                        alertType: "error",
                        alertContent: resMessage,
                    });
                }
            );
        }
    }

    render() {
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
                        <LockOutlinedIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        登录
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
                            label="用户名"
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
                            label="密码"
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
                        <FormControlLabel
                            control={
                                <Checkbox value="remember" color="primary" />
                            }
                            label="记住我"
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            登录
                        </Button>
                        <Grid container>
                            <Grid item xs>
                                <Link href="#" variant="body2">
                                    忘记密码？
                                </Link>
                            </Grid>
                            <Grid item>
                                <Link
                                    variant="body2"
                                    component={RouterLink}
                                    to="/register"
                                >
                                    还没有账号？快速注册
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
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
