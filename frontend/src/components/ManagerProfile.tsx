import React, { Component } from "react";
import { Link as RouterLink, Redirect } from "react-router-dom";
import ManagerService from "../services/manager.service";
import {ManagerInfo} from "../services/definations";
import userService from "../services/user.service";
import authService from "../services/auth.service";
import {validate_length, validate_required} from "./Login";
import {validate_email} from "./Register";
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
import {UserFullyInfo, UserInfo} from "../services/definations";


// state interface
interface ManagerProfileState {
    redirect: string | null;
    managerReady: boolean;
    token: string;
    manager: ManagerInfo | null;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;
    error_msg_manager_name: string;
    error_msg_password: string;
    error_msg_email: string;
}


export default class ManagerProfile extends Component<
    ManagerProfileState
    > {

    constructor(props: any) {
        super(props);

        this.state = {
            redirect: null,
            managerReady: false,
            token: "",
            manager: null,
            alert: false,
            alertType: "error",
            alertContent: "",
            error_msg_manager_name: "",
            error_msg_password: "",
            error_msg_email: "",
        };
        this.handleAlert = this.handleAlert.bind(this);
        this.handleRedirect = this.handleRedirect.bind(this);
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



}
