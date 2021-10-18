import React, { Component } from "react";
import authService from "../services/auth.service";
// mui
import Snackbar from "@mui/material/Snackbar";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Box from "@mui/material/Box";
import VpnKeyIcon from "@mui/icons-material/VpnKey";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import Alert from "@mui/material/Alert";
// validators
import { validate_length, validate_required } from "./Login";
import { validate_second_password } from "./Register";
// router
import { Link as RouterLink, Redirect } from "react-router-dom";
import UserContext from "../UserContext";

// state interface
interface ChangePasswordState {
    id: number;
    old_password: string;
    password: string;
    ensure_password: string;
    error_msg_old_password: string;
    error_msg_password: string;
    error_msg_ensure_password: string;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
    redirect: null | string;
}

// props interface
interface ChangePasswordProps {
    isAdmin: boolean;
    redirectConfirm: string;
    redirectCancel: string;
}

// password validator
const validate_origin_password = (value: any, origin: string) => {
    if (value === origin) {
        return "新密码不能和旧密码相同";
    } else {
        return "";
    }
};

export default class ChangePassword extends Component<
    ChangePasswordProps,
    ChangePasswordState
> {
    constructor(props: any) {
        super(props);
        // handle changing password
        this.handleChanging = this.handleChanging.bind(this);
        // state
        this.state = {
            id: 0,
            old_password: "",
            password: "",
            ensure_password: "",
            error_msg_old_password: "",
            error_msg_password: "",
            error_msg_ensure_password: "",
            alert: false,
            alertContent: "",
            alertType: "error",
            redirect: null,
        };
    }

    // get user info first
    // if user not found
    // redirect
    componentDidMount() {
        if (this.props.isAdmin) {
            // todo
            // you need to init admin id first
            // refer to what I've done below
        } else {
            const currentUser = this.context.user;

            if (!currentUser) {
                // that means a bug occur
                console.error("Try to change password without login!");
                return;
            }
            this.setState({
                id: currentUser.id,
            });
        }
    }

    // listener on old_password/password/ensure_password
    onChangeValue(
        e: any,
        type: "old_password" | "password" | "ensure_password"
    ) {
        const value = e.target.value;
        // first validate not empty
        let error = validate_required(value);
        // password should not equal to the origin one
        if (error === "" && type === "password") {
            error = validate_origin_password(value, this.state.old_password);
        }
        // then validate other requirements
        if (error === "" && type === "ensure_password") {
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

    // handle password change submit
    handleChanging(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();

        // validate all the info
        if (
            this.onChangeValue(
                { target: { value: this.state.old_password } },
                "old_password"
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
            let service;
            if (this.props.isAdmin) {
                // todo substitute it with admin service
                service = authService;
            } else {
                service = authService;
            }

            // changing request
            service
                .modifyPassword(
                    this.state.id,
                    this.state.old_password,
                    this.state.password
                )
                .then(
                    () => {
                        // modify success
                        // alert
                        this.setState({
                            alert: true,
                            alertType: "success",
                            alertContent: "修改成功",
                        });
                        // then logout
                        this.setState({
                            redirect: this.props.redirectConfirm,
                        });
                    },
                    (error) => {
                        // show the error message
                        if (error.response.status === 403) {
                            this.setState({
                                alert: true,
                                alertType: "error",
                                alertContent: "原密码不正确",
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
                        <VpnKeyIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        {this.props.isAdmin ? "修改管理员密码" : "修改密码"}
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={this.handleChanging}
                        noValidate
                        sx={{ mt: 1 }}
                    >
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="old_password"
                            label="旧密码"
                            type="password"
                            id="cp_old_password"
                            autoComplete="new-password"
                            onChange={(e) =>
                                this.onChangeValue(e, "old_password")
                            }
                            // @ts-ignore
                            error={
                                this.state.error_msg_old_password.length !== 0
                            }
                            // @ts-ignore
                            helperText={this.state.error_msg_old_password}
                            inputProps={{ maxLength: 15 }}
                        />
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="新密码"
                            type="password"
                            id="cp_password"
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
                            id="cp_ensure_password"
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
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color={"error"}
                            sx={{ mt: 3, mb: 2 }}
                        >
                            确认修改
                        </Button>
                        <Button
                            fullWidth
                            variant="contained"
                            sx={{ mt: 1, mb: 2 }}
                            component={RouterLink}
                            to={this.props.redirectCancel}
                        >
                            取消修改
                        </Button>
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

ChangePassword.contextType = UserContext;
