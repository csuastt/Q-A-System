import React, { Component } from "react";
import ManagerService from "../services/manager.service";
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
import MenuItem from '@mui/material/MenuItem';
// email checker
// @ts-ignore
// other validators
import { validate_required, validate_length } from "./Login";
import { Link as RouterLink } from "react-router-dom";


// permission options
const manager_permission_options = [
    { value: "Observer", label: "观察员" },
    { value: "Auditor", label: "审核员" },
];

export const validate_permission =(value: any)=>{
    if(value.toString()!=="Observer"&&value.toString()!=="Auditor"){
        return "权限设置非法，选择Observer或者Auditor";
    }else{
        return "";
    }
};


// state interface
interface CreateState {
    manager_name: string;
    permission: string;
    error_msg_manager_name: string;
    error_msg_permission:string;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
    readPolicy: boolean;
    redirect: string | null;
}

export default class ManageCreate extends Component<any, CreateState> {
    constructor(props: any) {
        super(props);
        // handle create info
        this.handleCreate = this.handleCreate.bind(this);
        // state
        this.state = {
            manager_name: "",
            permission: "",
            error_msg_manager_name: "",
            error_msg_permission: "",
            alert: false,
            alertContent: "",
            alertType: "error",
            readPolicy: false,
            redirect: null,
        };
    }

    // listener on /manager_name/permission
    onChangeValue(
        e: any,
        type: "manager_name" | "permission"
    ) {
        const value = e.target.value;
        // first validate not empty
        let error = validate_required(value);
        // then validate other requirements
        if (error === ""&&type ==="manager_name") {
            error = validate_length(value);
        }else if(error === ""&&type ==="permission"){
            error=validate_permission(value);
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
                { target: { value: this.state.manager_name } },
                "manager_name"
            ) &&
            this.onChangeValue(
                { target: { value: this.state.permission } },
                "permission"
            )
        ) {
            // register request
            ManagerService.create(
                this.state.manager_name,
                this.state.permission,
            ).then(
                () => {
                    // register success
                    // alert
                    this.setState({
                        alert: true,
                        alertType: "success",
                        alertContent: "创建成功，请记住您的密码！",
                    });
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
                            id="reg_manager_name"
                            label="管理员名称"
                            name="manager_name"
                            autoComplete="manager_name"
                            autoFocus
                            onChange={(e) => this.onChangeValue(e, "manager_name")}
                            // @ts-ignore
                            error={this.state.error_msg_manager_name.length !== 0}
                            // @ts-ignore
                            helperText={this.state.error_msg_manager_name}
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
                            value={
                                this.state.permission
                            }

                            name="permission"
                            label="管理员权限"
                            type="permission"
                            id="reg_permission"
                            autoComplete="new-permission"
                            onChange={(e) => this.onChangeValue(e, "permission")}
                            // @ts-ignore
                            error={this.state.error_msg_permission.length !== 0}
                            // @ts-ignore
                            helperText={this.state.error_msg_permission}
                            inputProps={{ maxLength: 10 }}
                        >{manager_permission_options.map(
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
                    )} </TextField>

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
            </Container>
        );
    }
}
