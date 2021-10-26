import React, { Component } from "react";
import { Link as RouterLink, Redirect } from "react-router-dom";
import ManagerService from "../services/adminAuthService";
import { ManagerInfo } from "../services/definations";
import userService from "../services/user.service";
import authService from "../services/auth.service";
import { validate_length, validate_required } from "./Login";
import { validate_email } from "./Register";
import Grid from "@mui/material/Grid";
import AccountBriefProfile from "./AccountBriefProfile";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Divider from "@mui/material/Divider";
import CardContent from "@mui/material/CardContent";
import TextField from "@mui/material/TextField";
import MuiPhoneNumber from "mui-phone-number";
import InputAdornment from "@mui/material/InputAdornment";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Snackbar from "@mui/material/Snackbar";
import Alert from "@mui/material/Alert";
import { UserFullyInfo, UserInfo } from "../services/definations";

// state interface
interface ManagerProfileState {
    redirect: string | null;
    managerReady: boolean;
    token: string;
    manager: ManagerInfo | null;
    alert: boolean;
    alertContent: string;
    alertType: "info" | "error";
}

export default class ManagerProfile extends Component<{}, ManagerProfileState> {
    constructor(props: any) {
        super(props);

        this.state = {
            redirect: null,
            managerReady: false,
            token: "",
            manager: null,
            alert: false,
            alertContent: "",
            alertType: "error",
        };
        this.handleAlert = this.handleAlert.bind(this);
        this.handleRedirect = this.handleRedirect.bind(this);
    }

    // alert handler
    handleAlert(_alertType: "info" | "error", _alertContent: string) {
        this.setState({
            alert: true,
            alertType: _alertType,
            alertContent: _alertContent,
        });
    }

    // redirect handler
    handleRedirect(target: string) {
        this.setState({
            redirect: target,
        });
    }

    componentDidMount() {
        const currentManager = ManagerService.getCurrentManager();
        if (!currentManager) {
            // alert
            this.handleAlert("error", "非法访问");
            //this.handleRedirect("/");
            return;
        }
        this.setState({
            // token: authToken(),
            manager: currentManager,
            managerReady: true,
        });
    }

    render() {
        if (this.state.redirect) {
            return <Redirect to={this.state.redirect} />;
        }
        return (
            <Grid
                container
                spacing={4}
                justifyContent="center"
                sx={{
                    width: "100%",
                }}
            >
                <Grid item md={8} xs={12} mt={3}>
                    <form noValidate>
                        <Card>
                            <CardHeader title="管理员信息" />

                            <Divider />
                            <CardContent>
                                <Grid container spacing={4}>
                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="用户名"
                                            name="username"
                                            InputProps={{ readOnly: true }}
                                            value={
                                                this.state.manager?.manager_name
                                            }
                                            variant="outlined"
                                        />
                                    </Grid>

                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="邮箱"
                                            name="email"
                                            InputProps={{
                                                readOnly: true,
                                            }}
                                            value={this.state.manager?.email}
                                            variant="outlined"
                                        />
                                    </Grid>

                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="密码"
                                            name="password"
                                            InputProps={{
                                                readOnly: true,
                                            }}
                                            value={this.state.manager?.password}
                                            variant="outlined"
                                            // @ts-ignore
                                        />
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="权限"
                                            name="permission"
                                            InputProps={{
                                                readOnly: true,
                                            }}
                                            value={
                                                this.state.manager?.permission
                                            }
                                            variant="outlined"
                                        ></TextField>
                                    </Grid>
                                </Grid>
                            </CardContent>

                            <Divider />
                            <Box
                                sx={{
                                    display: "flex",
                                    justifyContent: "flex-end",
                                    p: 1,
                                }}
                            >
                                <Grid item>
                                    <Button
                                        color="error"
                                        variant="contained"
                                        component={RouterLink}
                                        to="/manager/change_password"
                                    >
                                        修改密码
                                    </Button>
                                </Grid>
                            </Box>
                        </Card>
                    </form>
                </Grid>

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
            </Grid>
        );
    }
}
