import React, { Component, useState } from "react";
import { ConfigInfo } from "../services/definations";

import { Box, Card, CardContent, Grid } from "@mui/material";
import CardHeader from "@mui/material/CardHeader";
import Divider from "@mui/material/Divider";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import configService from "../services/configService";

interface SystemSettingsState {
    config: ConfigInfo | null;
    alert: boolean;
    alertType: "success" | "info" | "warning" | "error";
    alertContent: string;

    error_msg_minPrice: string;
    error_msg_maxPrice: string;
    error_msg_respondExpirationSeconds: string;
    error_msg_answerExpirationSeconds: string;
    error_msg_fulfillExpirationSeconds: string;
    error_msg_maxChatMessages: string;
    error_msg_maxChatTimeSeconds: string;
    error_msg_feeRate: string;
}

export default class SystemSettings extends Component<
    any,
    SystemSettingsState
> {
    constructor(props: any) {
        super(props);
        // state
        this.state = {
            config: null,
            alert: false,
            alertType: "error",
            alertContent: "",
            error_msg_minPrice: "",
            error_msg_maxPrice: "",
            error_msg_respondExpirationSeconds: "",
            error_msg_answerExpirationSeconds: "",
            error_msg_fulfillExpirationSeconds: "",
            error_msg_maxChatMessages: "",
            error_msg_maxChatTimeSeconds: "",
            error_msg_feeRate: "",
        };
        this.fetchConfigInfo = this.fetchConfigInfo.bind(this);
        this.handleChangeConfig = this.handleChangeConfig.bind(this);
        this.handleAlert = this.handleAlert.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.fetchConfigInfo();
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
    // get info of config
    fetchConfigInfo() {
        configService.getSystemConfig().then(
            (response) => {
                if (response) {
                    this.setState({
                        config: response,
                    });
                }
            },
            (error) => {
                this.handleAlert("error", "网络错误");
            }
        );
    }
    handleChangeConfig(e: any) {
        if (this.state.config === null) return;
        const new_config = { ...this.state.config };
        if (e.target.name === "minPrice" && e.target.value >= 0)
            new_config["minPrice"] = e.target.value;
        else if (e.target.name === "maxPrice" && e.target.value >= 0)
            new_config["maxPrice"] = e.target.value;
        else if (
            e.target.name === "respondExpirationSeconds" &&
            e.target.value >= 0
        )
            new_config["respondExpirationSeconds"] = e.target.value;
        else if (
            e.target.name === "answerExpirationSeconds" &&
            e.target.value >= 0
        )
            new_config["answerExpirationSeconds"] = e.target.value;
        else if (
            e.target.name === "fulfillExpirationSeconds" &&
            e.target.value >= 0
        )
            new_config["fulfillExpirationSeconds"] = e.target.value;
        else if (e.target.name === "maxChatMessages" && e.target.value >= 0)
            new_config["maxChatMessages"] = e.target.value;
        else if (e.target.name === "maxChatTimeSeconds" && e.target.value >= 0)
            new_config["maxChatTimeSeconds"] = e.target.value;
        else if (e.target.name === "feeRate" && e.target.value >= 0)
            new_config["feeRate"] = e.target.value;
        this.setState({ config: new_config });
    }
    handleSubmit() {
        // avoid null
        if (this.state.config === null) return;
        let temp = this.state.config;
        configService.modifyConfigInfo(temp).then(
            () => {
                // alert
                this.handleAlert("success", "修改成功");
                // get info again
                this.fetchConfigInfo();
            },
            (error) => {
                // show the error message
                if (error.response.status === 403) {
                    this.handleAlert("error", "权限不足");
                } else if (error.response.status === 401) {
                    this.handleAlert("error", "尚未登录");
                } else {
                    this.handleAlert("error", "网络错误");
                }
            }
        );
    }

    render() {
        return (
            <Card>
                <CardHeader subheader="编辑并保存系统参数" title="系统参数" />
                <Divider />
                <CardContent>
                    <Grid container spacing={3}>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="最低价格限制"
                                name="minPrice"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={this.state.config?.minPrice || ""}
                                onChange={this.handleChangeConfig}
                                required
                                variant="outlined"
                            />
                        </Grid>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="最高价格限制"
                                name="maxPrice"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={this.state.config?.maxPrice || ""}
                                onChange={this.handleChangeConfig}
                                required
                                variant="outlined"
                            />
                        </Grid>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="响应过期秒数"
                                name="respondExpirationSeconds"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={
                                    this.state.config
                                        ?.respondExpirationSeconds || ""
                                }
                                onChange={this.handleChangeConfig}
                                required
                                variant="outlined"
                            />
                        </Grid>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="回答过期秒数"
                                name="answerExpirationSeconds"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={
                                    this.state.config
                                        ?.answerExpirationSeconds || ""
                                }
                                onChange={this.handleChangeConfig}
                                required
                                variant="outlined"
                            />
                        </Grid>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="履行到期秒数"
                                name="fulfillExpirationSeconds"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={
                                    this.state.config
                                        ?.fulfillExpirationSeconds || ""
                                }
                                onChange={this.handleChangeConfig}
                                required
                                variant="outlined"
                            />
                        </Grid>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="最多聊天消息数"
                                name="maxChatMessages"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={this.state.config?.maxChatMessages || ""}
                                onChange={this.handleChangeConfig}
                                required
                                variant="outlined"
                            />
                        </Grid>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="最长聊天时间秒数"
                                name="maxChatTimeSeconds"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={
                                    this.state.config?.maxChatTimeSeconds || ""
                                }
                                onChange={this.handleChangeConfig}
                                required
                                variant="outlined"
                            />
                        </Grid>
                        <Grid item md={6} xs={12}>
                            <TextField
                                fullWidth
                                label="费率"
                                name="feeRate"
                                InputProps={{
                                    readOnly: false,
                                }}
                                value={this.state.config?.feeRate || ""}
                                onChange={this.handleChangeConfig}
                                required
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
                    <Button color="primary" variant="contained"
                            onClick={this.handleSubmit}>
                        保存信息
                    </Button>
                </Box>
            </Card>
        );
    }
}
