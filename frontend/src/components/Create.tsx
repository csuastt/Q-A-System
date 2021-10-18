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

            </Container>
        );
    }
}
