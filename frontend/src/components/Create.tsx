import React, { Component, useState } from "react";
import { Redirect } from "react-router-dom";
import { ManagerRole } from "../services/definations";
// mui
import Snackbar from "@mui/material/Snackbar";
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import CssBaseline from "@mui/material/CssBaseline";
import TextField from "@mui/material/TextField";
import Box from "@mui/material/Box";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import Typography from "@mui/material/Typography";
import Container from "@mui/material/Container";
import Alert from "@mui/material/Alert";
// email checker
// @ts-ignore
// other validators
import { validate_required, validate_length } from "./Login";
//import { Link as RouterLink } from "react-router-dom";
import AdminAuthService from "../services/adminAuthService";
import CreateDetailDialog from "./CreateDetailDialog";

// role options
const manager_role_options = [
    { value: ManagerRole.ADMIN, label: "管理员" },
    { value: ManagerRole.REVIEWER, label: "审核员" },
];

export const validate_role = (value: any) => {
    if (
        value.toString() !== ManagerRole.ADMIN &&
        value.toString() !== ManagerRole.REVIEWER
    ) {
        return "权限设置非法，选择Observer或者Auditor";
    } else {
        return "";
    }
};

// state interface
interface CreateState {
    username: string;
    password: string;
    role: ManagerRole;
    error_msg_username: string;
    error_msg_role: string;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
    readPolicy: boolean;
    redirect: string | null;
    dialogOpen: boolean;
}

export default class ManageCreate extends Component<any, CreateState> {
    constructor(props: any) {
        super(props);
        // handle create info
        this.handleCreate = this.handleCreate.bind(this);
        // state
        this.state = {
            username: "",
            password: "",
            role: ManagerRole.ADMIN,
            error_msg_username: "",
            error_msg_role: "",
            alert: false,
            alertContent: "",
            alertType: "error",
            readPolicy: false,
            redirect: null,
            dialogOpen: false,
        };
    }

    // listener on /username/role
    onChangeValue(e: any, type: "username" | "role") {
        const value = e.target.value;
        // first validate not empty
        let error = validate_required(value);
        // then validate other requirements
        if (error === "" && type === "username") {
            error = validate_length(value);
        } else if (error === "" && type === "role") {
            error = validate_role(value);
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

    handleCreate(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();

        // validate all the info
        if (
            this.onChangeValue(
                { target: { value: this.state.username } },
                "username"
            ) &&
            this.onChangeValue({ target: { value: this.state.role } }, "role")
        ) {
            // register request
            AdminAuthService.create(this.state.username, this.state.role).then(
                (response) => {
                    // register success
                    if (response) {
                        // alert
                        this.setState({
                            password: response,
                            alert: true,
                            alertType: "success",
                            alertContent:
                                "创建成功，请记住您的密码:" + response,
                        });
                        this.setState({
                            dialogOpen: true,
                        });
                    }
                },
                (error) => {
                    // show the error message
                    if (error.response.status === 403) {
                        this.setState({
                            alert: true,
                            alertType: "error",
                            alertContent: "管理员名称已注册",
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
                        管理员创建
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={this.handleCreate}
                        noValidate
                        sx={{ mt: 0 }}
                    >
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            id="reg_username"
                            label="管理员名称"
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
                            select
                            SelectProps={{
                                native: true,
                            }}
                            value={this.state.role}
                            name="role"
                            label="管理员权限"
                            type="role"
                            id="reg_role"
                            autoComplete="new-role"
                            onChange={(e) => this.onChangeValue(e, "role")}
                            // @ts-ignore
                            error={this.state.error_msg_role.length !== 0}
                            // @ts-ignore
                            helperText={this.state.error_msg_role}
                            inputProps={{ maxLength: 10 }}
                        >
                            {manager_role_options.map((option) => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}{" "}
                        </TextField>

                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{ mt: 3, mb: 2 }}
                        >
                            创建
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
                <CreateDetailDialog
                    open={this.state.dialogOpen}
                    onClose={() =>
                        this.setState({
                            dialogOpen: false,
                        })
                    }
                    username={this.state.username}
                    password={this.state.password}
                    maxWidth="sm"
                    fullWidth
                />
            </Container>
        );
    }
}
