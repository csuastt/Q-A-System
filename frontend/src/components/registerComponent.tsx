import React, { Component } from "react";
import AuthService from "../services/auth.service";
import { Redirect } from "react-router-dom";
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
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import Alert from "@mui/material/Alert";
// email checker
// @ts-ignore
import { isEmail } from "validator";
// other validators
import { validate_required, validate_length } from "./loginComponent";
import { Link as RouterLink } from "react-router-dom";

// some validators
// email validator
// to ensure a valid email
export const validate_email = (value: any) => {
    if (!isEmail(value.toString().trim())) {
        return "请输入合法的邮箱";
    } else {
        return "";
    }
};
// second password validator
// to ensure the second password is identical to the first one
export const validate_second_password = (value: any, old_password: string) => {
    if (value !== old_password) {
        return "两次密码输入不一致";
    } else {
        return "";
    }
};

// state interface
interface RegisterState {
    username: string;
    password: string;
    email: string;
    ensure_password: string;
    error_msg_username: string;
    error_msg_password: string;
    error_msg_email: string;
    error_msg_ensure_password: string;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
    readPolicy: boolean;
    redirect: string | null;
}

export default class Register extends Component<any, RegisterState> {
    constructor(props: any) {
        super(props);
        // handle register info
        this.handleRegister = this.handleRegister.bind(this);
        // state
        this.state = {
            username: "",
            password: "",
            email: "",
            ensure_password: "",
            error_msg_username: "",
            error_msg_password: "",
            error_msg_email: "",
            error_msg_ensure_password: "",
            alert: false,
            alertContent: "",
            alertType: "error",
            readPolicy: false,
            redirect: null,
        };
    }

    // listener on email/username/password
    onChangeValue(
        e: any,
        type: "username" | "password" | "ensure_password" | "email"
    ) {
        const value = e.target.value;
        // first validate not empty
        let error = validate_required(value);
        // then validate other requirements
        if (error === "" && type === "email") {
            error = validate_email(value);
        } else if (error === "" && type === "ensure_password") {
            // @ts-ignore
            error = validate_second_password(value, this.state.password);
        } else if (error === "") {
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

    handleRegister(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();

        // validate all the info
        if (
            this.onChangeValue(
                { target: { value: this.state.username } },
                "username"
            ) &&
            this.onChangeValue(
                { target: { value: this.state.email } },
                "email"
            ) &&
            this.onChangeValue(
                { target: { value: this.state.password } },
                "password"
            ) &&
            this.onChangeValue(
                { target: { value: this.state.ensure_password } },
                "ensure_password"
            )
        ) {
            // make sure the user has read about policy
            if (!this.state.readPolicy) {
                // alert
                this.setState({
                    alert: true,
                    alertType: "error",
                    alertContent: "请阅读我们的政策",
                });
                return;
            }
            // register request
            AuthService.register(
                this.state.username,
                this.state.email,
                this.state.password
            ).then(
                () => {
                    // register success
                    // alert
                    this.setState({
                        alert: true,
                        alertType: "success",
                        alertContent: "注册成功",
                    });
                    // redirect
                    this.setState({
                        redirect: "/login",
                    });
                },
                (error) => {
                    // show the error message
                    if (error.response.status === 403) {
                        this.setState({
                            alert: true,
                            alertType: "error",
                            alertContent: "用户名已注册",
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
                        <AccountCircleIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        注册
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={this.handleRegister}
                        noValidate
                        sx={{ mt: 0 }}
                    >
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="reg_username"
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
                            id="reg_email"
                            label="邮箱"
                            name="email"
                            autoComplete="email"
                            autoFocus
                            onChange={(e) => this.onChangeValue(e, "email")}
                            // @ts-ignore
                            error={this.state.error_msg_email.length !== 0}
                            // @ts-ignore
                            helperText={this.state.error_msg_email}
                            inputProps={{ maxLength: 30 }}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="密码"
                            type="password"
                            id="reg_password"
                            autoComplete="new-password"
                            onChange={(e) => this.onChangeValue(e, "password")}
                            // @ts-ignore
                            error={this.state.error_msg_password.length !== 0}
                            // @ts-ignore
                            helperText={this.state.error_msg_password}
                            inputProps={{ maxLength: 15 }}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="ensure_password"
                            label="确认密码"
                            type="password"
                            id="reg_ensure_password"
                            autoComplete="new-password"
                            onChange={(e) =>
                                this.onChangeValue(e, "ensure_password")
                            }
                            // @ts-ignore
                            error={
                                this.state.error_msg_ensure_password.length !==
                                0
                            }
                            // @ts-ignore
                            helperText={this.state.error_msg_ensure_password}
                            inputProps={{ maxLength: 15 }}
                        />
                        <FormControlLabel
                            control={
                                <Checkbox
                                    value="agree"
                                    color="primary"
                                    checked={this.state.readPolicy}
                                    onChange={() => {
                                        this.setState({
                                            readPolicy: !this.state.readPolicy,
                                        });
                                    }}
                                />
                            }
                            label={
                                <div>
                                    <span>我已阅读并同意</span>
                                    <Link
                                        href={
                                            "https://www.kuaishou.com/about/policy"
                                        }
                                    >
                                        《快手用户服务协议》
                                    </Link>
                                </div>
                            }
                        />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            注册
                        </Button>
                        <Grid container justifyContent="flex-end">
                            <Grid item>
                                <Link
                                    variant="body2"
                                    component={RouterLink}
                                    to="/login"
                                >
                                    已有账号？快速登录
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
