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
}

export default class SystemSettings extends Component<
    any,
    SystemSettingsState
> {
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
                                // value={1
                                //    // this.state.user?.email || ""
                                // }
                                // onChange={
                                // }
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
                                value={
                                    1
                                    // this.state.user?.email || ""
                                }
                                //onChange={this.handleChangeUser}
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
                                    1
                                    // this.state.user?.email || ""
                                }
                                //onChange={this.handleChangeUser}
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
                                    1
                                    // this.state.user?.email || ""
                                }
                                //onChange={this.handleChangeUser}
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
                                    1
                                    // this.state.user?.email || ""
                                }
                                //onChange={this.handleChangeUser}
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
                                value={
                                    1
                                    // this.state.user?.email || ""
                                }
                                //onChange={this.handleChangeUser}
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
                                    1
                                    // this.state.user?.email || ""
                                }
                                //onChange={this.handleChangeUser}
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
                                value={
                                    1
                                    // this.state.user?.email || ""
                                }
                                //onChange={this.handleChangeUser}
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
                    <Button color="primary" variant="contained">
                        保存信息
                    </Button>
                </Box>
            </Card>
        );
    }
}
