import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import authService from "../services/auth.service";
import userService from "../services/user.service";
import AccountBriefProfile from "./profileBriefComponent";
// mui
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Divider from "@mui/material/Divider";
import CardContent from "@mui/material/CardContent";
import Grid from "@mui/material/Grid";
import TextField from "@mui/material/TextField";
import Alert from "@mui/material/Alert";
import InputAdornment from "@mui/material/InputAdornment";
import MuiPhoneNumber from "mui-phone-number";
import Snackbar from "@mui/material/Snackbar";
import { UserInfo } from "../services/definations";
import authToken from "../services/auth-token";

interface ProfileState {
    redirect: string | null;
    userReady: boolean;
    token: string;
    user: UserInfo | null;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
}

// gender options
const gender_options = [
    { value: "female", label: "女性" },
    { value: "male", label: "男性" },
    { value: "unknown", label: "未知" },
];

export default class AccountProfile extends Component<any, ProfileState> {
    // now nickname
    private now_nickname = "";

    constructor(props: any) {
        super(props);

        this.state = {
            redirect: null,
            userReady: false,
            token: "",
            user: null,
            alert: false,
            alertContent: "",
            alertType: "error",
        };
        this.handleAlert = this.handleAlert.bind(this);
    }

    // alert handler
    handleAlert(
        _alertType: "success" | "info" | "warning" | "error",
        _alertContent: string
    ) {
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

    // if user not found
    // redirect
    componentDidMount() {
        const currentUser = authService.getCurrentUser();

        if (!currentUser) {
            // redirect and alert
            this.handleAlert("error", "Network error");
            this.handleRedirect("#");
            return;
        }
        this.setState({
            // token: authToken(),
            user: currentUser,
            userReady: true,
        });
        // this.now_nickname = currentUser.user.nickname;
    }

    // text change handler
    handleChange = (e: any) => {
        // avoid null
        if (this.state.user === null) return;
        // set new state
        const new_user_info = { ...this.state.user };
        if (typeof e === "string") new_user_info["phone"] = e;
        // @ts-ignore
        else new_user_info[e.target.name] = e.target.value;
        console.log(new_user_info["phone"]);
        this.setState({ user: new_user_info });
    };

    // submit handler
    handleSubmit() {
        // avoid null
        if (this.state.user === null) return;
        // modify info request
        let temp = this.state.user;
        // modify phone
        if (temp.phone === "+") {
            temp.phone = "";
        }
        userService.modifyUserInfo(temp).then(
            () => {
                // modify success
                // refresh nickname
                // @ts-ignore
                this.now_nickname = temp.nickname;
                // alert
                this.handleAlert("success", "Modify success!");
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
                this.handleAlert("error", resMessage);
            }
        );
    }

    render() {
        if (this.state.redirect) {
            return <Redirect to={this.state.redirect} />;
        }

        return (
            <Grid
                container
                spacing={4}
                sx={{
                    width: "100%",
                }}
            >
                <Grid item md={4} xs={4} mt={2}>
                    <AccountBriefProfile
                        avatar={""}
                        nickname={this.now_nickname}
                        username={this.state.user?.username}
                        permission={this.state.user?.permission}
                        alertHandler={this.handleAlert}
                        redirectHandler={this.handleRedirect}
                    />
                </Grid>
                <Grid item md={8} xs={8} mt={2}>
                    <form noValidate>
                        <Card>
                            <CardHeader
                                subheader="在下方编辑并点击保存即可修改个人信息~"
                                title="个人信息"
                            />
                            <Divider />
                            <CardContent>
                                <Grid container spacing={3}>
                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="用户名"
                                            name="username"
                                            InputProps={{
                                                readOnly: true,
                                            }}
                                            value={this.state.user?.username}
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
                                            value={this.state.user?.email}
                                            variant="outlined"
                                        />
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="性别"
                                            name="gender"
                                            onChange={this.handleChange}
                                            required
                                            select
                                            SelectProps={{ native: true }}
                                            value={this.state.user?.gender}
                                            variant="outlined"
                                        >
                                            {gender_options.map((option) => (
                                                <option
                                                    key={option.value}
                                                    value={option.value}
                                                >
                                                    {option.label}
                                                </option>
                                            ))}
                                        </TextField>
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <MuiPhoneNumber
                                            fullWidth
                                            label="电话"
                                            name="phone"
                                            required
                                            sx={{
                                                "& .MuiPhoneNumber-flagButton":
                                                    {
                                                        "min-width": "30px",
                                                    },
                                            }}
                                            defaultCountry={"cn"}
                                            onChange={this.handleChange}
                                            value={this.state.user?.phone}
                                            variant="outlined"
                                        />
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="钱包余额"
                                            name="money"
                                            onChange={this.handleChange}
                                            type="number"
                                            InputProps={{
                                                readOnly: true,
                                                startAdornment: (
                                                    <InputAdornment position="start">
                                                        ￥
                                                    </InputAdornment>
                                                ),
                                            }}
                                            value={this.state.user?.money}
                                            variant="outlined"
                                        />
                                    </Grid>
                                    <Grid item md={12} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="自我介绍"
                                            name="description"
                                            required
                                            multiline
                                            onChange={this.handleChange}
                                            rows={4}
                                            value={this.state.user?.description}
                                            placeholder="快来介绍一下你自己吧~"
                                            variant="outlined"
                                        />
                                    </Grid>
                                </Grid>
                            </CardContent>
                            <Divider />
                            <Box
                                sx={{
                                    display: "flex",
                                    justifyContent: "flex-end",
                                    p: 2,
                                }}
                            >
                                <Grid
                                    container
                                    spacing={1}
                                    justifyContent={"flex-end"}
                                >
                                    <Grid item>
                                        <Button
                                            color="error"
                                            variant="contained"
                                        >
                                            修改密码
                                        </Button>
                                    </Grid>
                                    <Grid item>
                                        <Button
                                            color="primary"
                                            onClick={this.handleSubmit.bind(
                                                this
                                            )}
                                            variant="contained"
                                        >
                                            保存信息
                                        </Button>
                                    </Grid>
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
