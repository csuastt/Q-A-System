import React, { Component } from "react";
import { Redirect } from "react-router-dom";
import AuthService from "../services/auth.service";
import AccountBriefProfile from "./profileBriefComponent";
// mui
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card'
import CardHeader from '@mui/material/CardHeader'
import Divider from '@mui/material/Divider'
import CardContent from '@mui/material/CardContent'
import Grid from '@mui/material/Grid'
import TextField from "@mui/material/TextField";
import {InputAdornment} from "@mui/material";
import MuiPhoneNumber from "material-ui-phone-number";

// interface for user info
export interface UserInfo {
    username: string,
    nickname: string,
    email: string,
    password: string,
    gender: string,
    phone: string,
    permission: string,
    money: string,
    description: string
}

interface ProfileState {
    redirect: string | null,
    userReady: boolean,
    token: string,
    user: UserInfo | null
}

// gender options
const gender_options = [
    {value: 'female', label: '女性'},
    {value: 'male', label: '男性'},
    {value: 'unknown', label: '未知'}
];

export default class AccountProfile extends Component<any, ProfileState> {
    constructor(props: any) {
        super(props);

        this.state = {
            redirect: null,
            userReady: false,
            token: '',
            user: null
        };
    }

    // now nickname
    private now_nickname = ""

    // if user not found
    // redirect
    componentDidMount() {
        // const currentUser = AuthService.getCurrentUser();
        // mock code
        const currentUser = {
            token: '',
            user:{
                username: "test123",
                nickname: "",
                email: "12345@qq.com",
                password: "-----",
                gender: "female",
                phone: "",
                permission: "q",
                money: "100",
                description: ""
            }
        }

        if (!currentUser) this.setState({redirect: "/"});
        this.setState({
            token: currentUser.token,
            user: currentUser.user,
            userReady: true
        })
        this.now_nickname = currentUser.user.nickname;
    }

    // change handler
    handleChange = (e: any) => {
        // avoid null
        if (this.state.user === null)
            return;
        // set new state
        const new_user_info = {...this.state.user}
        if (typeof e === 'string')
            new_user_info['phone'] = e;
        else
            // @ts-ignore
            new_user_info[e.target.name] = e.target.value;
        console.log(new_user_info['phone']);
        this.setState({user: new_user_info});
    };

    render() {
        if (this.state.redirect) {
            return <Redirect to={this.state.redirect}/>
        }

        return (
            <Grid
                container
                spacing={4}
            >
                <Grid item md={4} xs={4}>
                    <AccountBriefProfile
                        nickname={this.now_nickname}
                        permission={this.state.user?.permission}
                    />
                </Grid>
            <Grid item md={8} xs={8}>
            <form
                noValidate
            >
                <Card>
                    <CardHeader
                        subheader="在下方编辑并点击保存即可修改个人信息~"
                        title="个人信息"
                    />
                    <Divider/>
                    <CardContent>
                        <Grid
                            container
                            spacing={3}
                        >
                            <Grid
                                item
                                md={6}
                                xs={12}
                            >
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
                            <Grid
                                item
                                md={6}
                                xs={12}
                            >
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
                            <Grid
                                item
                                md={6}
                                xs={12}
                            >
                                <TextField
                                    fullWidth
                                    label="昵称"
                                    required
                                    name="nickname"
                                    onChange={this.handleChange}
                                    value={this.state.user?.nickname}
                                    variant="outlined"
                                    placeholder={"请填写昵称~"}
                                />
                            </Grid>
                            <Grid
                                item
                                md={6}
                                xs={12}
                            >
                                <TextField
                                    fullWidth
                                    label="性别"
                                    name="gender"
                                    onChange={this.handleChange}
                                    required
                                    select
                                    SelectProps={{native: true}}
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
                            <Grid
                                item
                                md={6}
                                xs={12}
                            >
                                <MuiPhoneNumber
                                    fullWidth
                                    label="电话"
                                    name="phone"
                                    required
                                    defaultCountry={"cn"}
                                    onChange={this.handleChange}
                                    value={this.state.user?.phone}
                                    variant="outlined"
                                />
                            </Grid>
                            <Grid
                                item
                                md={6}
                                xs={12}
                            >
                                <TextField
                                    fullWidth
                                    label="钱包余额"
                                    name="money"
                                    onChange={this.handleChange}
                                    type="number"
                                    InputProps={{
                                        readOnly: true,
                                        startAdornment: <InputAdornment position="start">￥</InputAdornment>
                                    }}
                                    value={this.state.user?.money}
                                    variant="outlined"
                                />
                            </Grid>
                            <Grid
                                item
                                md={12}
                                xs={12}
                            >
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
                            display: 'flex',
                            justifyContent: 'flex-end',
                            p: 2
                        }}

                    >
                        <Grid container spacing={1} justifyContent={'flex-end'}>
                            <Grid item>
                                <Button
                                    color="secondary"
                                    variant="contained"
                                >
                                    修改密码
                                </Button>
                            </Grid>
                            <Grid item>
                                <Button
                                    color="primary"
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
            </Grid>
        );
    }
}
