import React, {Component} from "react";
import {Link as RouterLink, Redirect} from "react-router-dom";
import userService from "../services/userService";
import AccountBriefProfile from "./AccountBriefProfile";
import {UserFullyInfo, UserGender, UserInfo, UserRole} from "../services/definations";
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
import {validate_length, validate_required} from "./Login";
import {validate_email} from "./Register";
import UserContext from "../UserContext";

// state interface
interface ProfileState {
    redirect: string | null;
    userReady: boolean;
    token: string;
    user: UserInfo | UserFullyInfo | null;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
    minPrice: number;
    maxPrice: number;
    error_msg_username: string;
    error_msg_password: string;
    error_msg_email: string;
}

// props interface
interface ProfileProps {
    isAdmin: boolean;
}

// gender options
const gender_options = [
    { value: UserGender.FEMALE, label: "女性" },
    { value: UserGender.MALE, label: "男性" },
    { value: UserGender.UNKNOWN, label: "保密" },
];

// permission options
const permission_options = [
    { value: UserRole.USER, label: "提问者" },
    { value: UserRole.ANSWERER, label: "问答者" },
];

export default class AccountProfile extends Component<
    ProfileProps,
    ProfileState
> {
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
            minPrice: 0,
            maxPrice: 100,
            error_msg_username: "",
            error_msg_password: "",
            error_msg_email: "",
        };
        this.handleAlert = this.handleAlert.bind(this);
        this.handleRedirect = this.handleRedirect.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleChangeUser = this.handleChangeUser.bind(this);
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
        if (this.props.isAdmin) {
            // todo use admin api to get fully info of user
            // you also need to get min price and max price for answerer
            // init state: minPrice maxPrice
            // remove this mock code
            const currentUser = {
                id: 123213,
                username: "tester123",
                password: "thisIsPassword",
                nickname: "Nickname",
                avatar: "www.ava.com",
                sign_up_timestamp: 112323333,
                email: "sdassss@qq.com",
                phone: "",
                gender: UserGender.UNKNOWN,
                permission: "a",
                balance: 100,
                description: "This is the description",
                price: 50,
                role: UserRole.ANSWERER,
            };
            this.setState({
                user: currentUser,
                userReady: true,
            });
            this.now_nickname = currentUser.nickname;
        } else {
            const currentUser = this.context.user;

            if (!currentUser) {
                // redirect and alert
                this.handleAlert("error", "非法访问");
                this.handleRedirect("/");
                return;
            }
            this.setState({
                // token: authToken(),
                user: currentUser,
                userReady: true,
            });
            this.now_nickname = currentUser.nickname;
        }
    }

    // text change handler
    handleChange = (e: any) => {
        // avoid null
        if (this.state.user === null) return;
        // set new state
        const new_user_info = { ...this.state.user };
        if (typeof e === "string") new_user_info["phone"] = e;
        else if (e.target.name === "balance" && e.target.value < 0)
            new_user_info["balance"] = 0;
        else if (
            e.target.name === "price" &&
            e.target.value < this.state.minPrice
        ) {
            // @ts-ignore
            new_user_info["price"] = this.state.minPrice;
        } else if (
            e.target.name === "price" &&
            e.target.value > this.state.maxPrice
        ) {
            // @ts-ignore
            new_user_info["price"] = this.state.maxPrice;
        }
        // @ts-ignore
        else new_user_info[e.target.name] = e.target.value;
        this.setState({ user: new_user_info });
    };

    // handle changes on email/username/password
    handleChangeUser(e: any) {
        let error = validate_required(e.target.value);
        if (error === "" && e.target.name === "email") {
            error = validate_email(e.target.value);
        } else if (error === "") {
            error = validate_length(e.target.value);
        }
        // set new state
        const nextUserInfo = { ...this.state.user };
        // @ts-ignore
        nextUserInfo[e.target.name] = e.target.value;
        const nextState = {};
        // @ts-ignore
        nextState["error_msg_" + e.target.name] = error;
        // @ts-ignore
        nextState["user"] = nextUserInfo;
        this.setState(nextState);
        return error === "";
    }

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
        // request
        if (this.props.isAdmin) {
            // todo make request for admin
            // validate all the info
            if (
                this.handleChangeUser({
                    target: { value: this.state.user.username },
                }) &&
                this.handleChangeUser({
                    target: { value: this.state.user.email },
                }) &&
                "password" in this.state.user &&
                this.handleChangeUser({
                    target: { value: this.state.user.password },
                })
            ) {
                console.log("some requests for admin");
            }
        } else {
            userService.modifyUserInfo(temp).then(
                () => {
                    // modify success
                    // refresh nickname
                    // @ts-ignore
                    this.now_nickname = temp.nickname;
                    // alert
                    this.handleAlert("success", "修改成功");
                    // get info again
                    if (this.state.user) {
                        userService.getUserInfo(this.state.user.id).then(
                            (response) => {
                                if (response) {
                                    this.context.setUser(response);
                                }
                            },
                            (error) => {
                                // show the error message
                                if (error.response.status === 403) {
                                    this.handleAlert("error",
                                        "服务器验证异常");
                                } else {
                                    this.handleAlert("error",
                                        "网络错误");
                                }
                            }
                        );
                    }
                },
                (error) => {
                    // show the error message
                    this.handleAlert("error", "网络错误");
                }
            );
        }
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
                {this.props.isAdmin ? (
                    <Grid item md={1} xs={1} mt={2}>
                        <></>
                    </Grid>
                ) : (
                    <Grid item md={4} xs={8} mt={2}>
                        <AccountBriefProfile
                            id={this.state.user?.id}
                            avatar={this.state.user?.avatar}
                            nickname={this.now_nickname}
                            username={this.state.user?.username}
                            role={this.state.user?.role}
                            alertHandler={this.handleAlert}
                            redirectHandler={this.handleRedirect}
                            minPrice={this.state.minPrice}
                            maxPrice={this.state.maxPrice}
                        />
                    </Grid>
                )}
                <Grid item md={8} xs={12} mt={2}>
                    <form noValidate>
                        <Card>
                            {this.props.isAdmin ? (
                                <CardHeader
                                    subheader="在下方编辑并点击保存即可修改该用户的信息~"
                                    title="用户信息"
                                />
                            ) : (
                                <CardHeader
                                    subheader="在下方编辑并点击保存即可修改个人信息~"
                                    title="个人信息"
                                />
                            )}
                            <Divider />
                            <CardContent>
                                <Grid container spacing={3}>
                                    <Grid item md={6} xs={12}>
                                        {this.props.isAdmin ? (
                                            <TextField
                                                fullWidth
                                                label="用户名"
                                                name="username"
                                                InputProps={{
                                                    readOnly: false,
                                                }}
                                                inputProps={{ maxLength: 15 }}
                                                onChange={this.handleChangeUser}
                                                required
                                                value={
                                                    this.state.user?.username
                                                }
                                                // @ts-ignore
                                                error={
                                                    this.state
                                                        .error_msg_username
                                                        .length !== 0
                                                }
                                                // @ts-ignore
                                                helperText={
                                                    this.state
                                                        .error_msg_username
                                                }
                                                variant="outlined"
                                            />
                                        ) : (
                                            <TextField
                                                fullWidth
                                                label="用户名"
                                                name="username"
                                                InputProps={{ readOnly: true }}
                                                value={
                                                    this.state.user?.username
                                                }
                                                variant="outlined"
                                            />
                                        )}
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        {this.props.isAdmin ? (
                                            <TextField
                                                fullWidth
                                                label="邮箱"
                                                name="email"
                                                InputProps={{
                                                    readOnly: false,
                                                }}
                                                value={this.state.user?.email}
                                                onChange={this.handleChangeUser}
                                                required
                                                variant="outlined"
                                                // @ts-ignore
                                                error={
                                                    this.state.error_msg_email
                                                        .length !== 0
                                                }
                                                // @ts-ignore
                                                helperText={
                                                    this.state.error_msg_email
                                                }
                                                inputProps={{ maxLength: 30 }}
                                            />
                                        ) : (
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
                                        )}
                                    </Grid>
                                    {this.props.isAdmin &&
                                    this.state.user &&
                                    "password" in this.state.user ? (
                                        <>
                                            <Grid item md={6} xs={12}>
                                                <TextField
                                                    fullWidth
                                                    label="密码"
                                                    name="password"
                                                    onChange={
                                                        this.handleChangeUser
                                                    }
                                                    required
                                                    value={
                                                        this.state.user
                                                            ?.password
                                                    }
                                                    variant="outlined"
                                                    // @ts-ignore
                                                    error={
                                                        this.state
                                                            .error_msg_password
                                                            .length !== 0
                                                    }
                                                    // @ts-ignore
                                                    helperText={
                                                        this.state
                                                            .error_msg_password
                                                    }
                                                    inputProps={{
                                                        maxLength: 15,
                                                    }}
                                                />
                                            </Grid>
                                            <Grid item md={6} xs={12}>
                                                <TextField
                                                    fullWidth
                                                    label="权限"
                                                    name="permission"
                                                    onChange={this.handleChange}
                                                    select
                                                    SelectProps={{
                                                        native: true,
                                                    }}
                                                    value={
                                                        this.state.user
                                                            ?.role
                                                    }
                                                    variant="outlined"
                                                >
                                                    {permission_options.map(
                                                        (option) => (
                                                            <option
                                                                key={
                                                                    option.value
                                                                }
                                                                value={
                                                                    option.value
                                                                }
                                                            >
                                                                {option.label}
                                                            </option>
                                                        )
                                                    )}
                                                </TextField>
                                            </Grid>
                                        </>
                                    ) : (
                                        <></>
                                    )}
                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="昵称"
                                            name="nickname"
                                            onChange={this.handleChange}
                                            value={this.state.user?.nickname}
                                            variant="outlined"
                                            placeholder={"请填写昵称~"}
                                            inputProps={{ maxLength: 30 }}
                                        />
                                    </Grid>
                                    <Grid item md={6} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="性别"
                                            name="gender"
                                            onChange={this.handleChange}
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
                                            name="balance"
                                            onChange={this.handleChange}
                                            type="number"
                                            InputProps={
                                                this.props.isAdmin
                                                    ? {
                                                          inputProps: {
                                                              min: 0,
                                                          },
                                                          startAdornment: (
                                                              <InputAdornment position="start">
                                                                  ￥
                                                              </InputAdornment>
                                                          ),
                                                      }
                                                    : {
                                                          readOnly: true,
                                                          startAdornment: (
                                                              <InputAdornment position="start">
                                                                  ￥
                                                              </InputAdornment>
                                                          ),
                                                      }
                                            }
                                            value={this.state.user?.balance}
                                            variant="outlined"
                                        />
                                    </Grid>
                                    {this.props.isAdmin &&
                                    this.state.user &&
                                    this.state.user.role === UserRole.ANSWERER &&
                                    "price" in this.state.user ? (
                                        <>
                                            <Grid item md={6} xs={12}>
                                                <TextField
                                                    fullWidth
                                                    label="定价区间"
                                                    name="min_max_price"
                                                    InputProps={{
                                                        readOnly: true,
                                                        startAdornment: (
                                                            <InputAdornment position="start">
                                                                ￥
                                                            </InputAdornment>
                                                        ),
                                                    }}
                                                    value={
                                                        this.state.minPrice.toString() +
                                                        " - " +
                                                        this.state.maxPrice
                                                    }
                                                    variant="outlined"
                                                />
                                            </Grid>
                                            <Grid item md={6} xs={12}>
                                                <TextField
                                                    fullWidth
                                                    label="回答定价"
                                                    name="price"
                                                    onChange={this.handleChange}
                                                    type="number"
                                                    InputProps={{
                                                        inputProps: {
                                                            min: this.state
                                                                .minPrice,
                                                            max: this.state
                                                                .maxPrice,
                                                        },
                                                        startAdornment: (
                                                            <InputAdornment position="start">
                                                                ￥/次
                                                            </InputAdornment>
                                                        ),
                                                    }}
                                                    value={
                                                        this.state.user?.price
                                                    }
                                                    variant="outlined"
                                                />
                                            </Grid>
                                        </>
                                    ) : (
                                        <></>
                                    )}
                                    <Grid item md={12} xs={12}>
                                        <TextField
                                            fullWidth
                                            label="个人介绍"
                                            name="description"
                                            multiline
                                            onChange={this.handleChange}
                                            rows={4}
                                            value={this.state.user?.description}
                                            InputProps={
                                                !this.props.isAdmin &&
                                                this.state.user &&
                                                this.state.user.role ===
                                                    UserRole.ANSWERER
                                                    ? {
                                                          readOnly: true,
                                                      }
                                                    : {
                                                          readOnly: false,
                                                      }
                                            }
                                            inputProps={{ maxLength: 200 }}
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
                                    {this.props.isAdmin ? (
                                        <></>
                                    ) : (
                                        <Grid item>
                                            <Button
                                                color="error"
                                                variant="contained"
                                                component={RouterLink}
                                                to="/change_password"
                                            >
                                                修改密码
                                            </Button>
                                        </Grid>
                                    )}
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

AccountProfile.contextType = UserContext;
