import React, { Component } from "react";
import AuthService from "../services/auth.service";
// mui
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {Alert} from "@mui/material";
import { createTheme, ThemeProvider } from '@mui/material/styles';
// email checker
// @ts-ignore
import {isEmail} from "validator";
// other validators
import {validate_required, validate_length} from './loginComponent'

// theme
const theme = createTheme();

// email validator
// to ensure a valid email
const validate_email = (value: any) => {
    if (!isEmail(value.toString().trim())) {
        return '请输入合法的邮箱';
    } else {
        return '';
    }
};


export default class Register extends Component {

    constructor(props: any) {
        super(props);
        // handle register info
        this.handleRegister = this.handleRegister.bind(this);
        // state
        this.state = {
            username: '',
            password: '',
            email: '',
            error_msg_username: '',
            error_msg_password: '',
            error_msg_email: ''
        };
    }
    // if alert
    private alert = false;
    // alert msg
    private alertContent = '';

    // listener on email/username/password
    onChangeValue(e: any, type: 'username' | 'password' | 'email') {
        const value = e.target.value;
        // first validate not empty
        let error = validate_required(value);
        if (error === '' && type === 'email'){
            error = validate_email(value);
        }
        else if(error === '') {
            error = validate_length(value);
        }
        // set new state
        const nextState = {type: value};
        // @ts-ignore
        nextState['error_msg_' + type] = error;
        this.setState(nextState);
    }

    handleRegister(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();

        if ((
            // @ts-ignore
            validate_required(this.state.username) +
            // @ts-ignore
            validate_length(this.state.username) +
            // @ts-ignore
            validate_required(this.state.password) +
            // @ts-ignore
            validate_length(this.state.password) +
            // @ts-ignore
            validate_required(this.state.email) +
            // @ts-ignore
            validate_email(this.state.email)
        ).length === 0) {
            // @ts-ignore
            AuthService.register(this.state.username,
                // @ts-ignore
                this.state.email, this.state.password).then(
                () => {
                    // login success
                    // todo jump to sw
                    // alert
                    this.alert = true;
                    this.alertContent = 'Register success!';
                },
                error => {
                    // retrieve error
                    const resMessage =
                        (error.response &&
                            error.response.data &&
                            error.response.data.message) ||
                        error.message ||
                        error.toString();
                    // show the error message
                    // todo alert dialog vanishes when timeout
                    this.alert = true;
                    this.alertContent = resMessage;
                }
            );
        }
    }

    render() {
        return (
            <ThemeProvider theme={theme}>
                <Container component="main" maxWidth="xs">
                    <CssBaseline />
                    <Box
                        sx={{
                            marginTop: 8,
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                        }}
                    >
                        <Avatar sx={{ m: 1, bgcolor: 'primary.main' }}>
                            <AccountCircleIcon />
                        </Avatar>
                        <Typography component="h1" variant="h5">
                            注册
                        </Typography>
                        <Box component="form"
                             onSubmit={this.handleRegister}
                             noValidate sx={{ mt: 1 }}>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                id="reg_username"
                                label="用户名"
                                name="username"
                                autoComplete="username"
                                autoFocus
                                onChange={e => this.onChangeValue(e, 'username')}
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
                                onChange={e => this.onChangeValue(e, 'email')}
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
                                autoComplete="current-password"
                                onChange={e => this.onChangeValue(e, 'password')}
                                // @ts-ignore
                                error={this.state.error_msg_password.length !== 0}
                                // @ts-ignore
                                helperText={this.state.error_msg_password}
                                inputProps={{ maxLength: 15 }}
                            />
                            <FormControlLabel
                                control={<Checkbox value="agree" color="primary" />}
                                label={
                                    <div>
                                        <span>我已阅读并同意</span>
                                        <Link href={'https://www.kuaishou.com/about/policy'}>《快手用户服务协议》</Link>
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
                                    <Link href="#" variant="body2">
                                        {"已有账号？快速登录"}
                                    </Link>
                                </Grid>
                            </Grid>
                        </Box>
                    </Box>
                    {this.alert ? <Alert severity='error'>{this.alertContent}</Alert> : <></> }
                </Container>
            </ThemeProvider>
        );
    }
}
