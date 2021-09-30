import React, { Component } from "react";
import AuthService from "../services/auth.service";
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
import { validate_required, validate_length } from "./loginComponent";
import { validate_second_password } from "./registerComponent";
// redirector
import { Redirect } from "react-router-dom";

// state interface
interface ChangePasswordState {
    username: string;
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

export default class ChangePassword extends Component<
    any,
    ChangePasswordState
> {
    constructor(props: any) {
        super(props);
        // handle changing password
        this.handleChanging = this.handleChanging.bind(this);
        this.handleRequestError = this.handleRequestError.bind(this);
        // state
        this.state = {
            username: "",
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
        const currentUser = AuthService.getCurrentUser();

        if (!currentUser) {
            // that means a bug occur
            console.error("Try to change password without login!");
            return;
        }
        this.setState({
            username: currentUser.user.username,
        });
    }

    // listener on old_password/password/ensure_password
    onChangeValue(
        e: any,
        type: "old_password" | "password" | "ensure_password"
    ) {
        const value = e.target.value;
        // first validate not empty
        let error = validate_required(value);
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

    // handle request error
    handleRequestError(error: any) {
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
            // changing request
            AuthService.modifyPassword(
                this.state.username,
                this.state.old_password,
                this.state.password
            ).then(
                () => {
                    // modify success
                    // alert
                    this.setState({
                        alert: true,
                        alertType: "success",
                        alertContent: "Modify success!",
                    });
                    // then logout
                    AuthService.logout(this.state.username).then(
                        () => {
                            this.setState({
                                redirect: "/",
                            });
                        },
                        (error) => {
                            // handle error
                            this.handleRequestError(error);
                        }
                    );
                },
                (error) => {
                    // handle error
                    this.handleRequestError(error);
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
                        marginTop: 8,
                        display: "flex",
                        flexDirection: "column",
                        alignItems: "center",
                    }}
                >
                    <Avatar sx={{ m: 1, bgcolor: "primary.main" }}>
                        <VpnKeyIcon />
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        修改密码
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
                            onClick={() => {
                                window.location.reload();
                            }}
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
