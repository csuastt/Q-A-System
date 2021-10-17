// mui
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import Divider from "@mui/material/Divider";
import CardContent from "@mui/material/CardContent";
import React, { Component } from "react";
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import TextField from "@mui/material/TextField";

interface AccountBriefProfileProps {
    avatar: string | undefined;
    nickname: string | undefined;
    username: string | undefined;
    permission: string | undefined;
    alertHandler: (
        arg1: "success" | "info" | "warning" | "error",
        arg2: string
    ) => void;
    redirectHandler: (arg1: string) => void;
}

interface AccountBriefProfileState {
    openApplyDialog: boolean;
    openPriceDialog: boolean;
}


export default class AccountBriefProfile extends Component<
    AccountBriefProfileProps,
    AccountBriefProfileState
> {

    constructor(props: any) {
        super(props);

        this.state = {
            openApplyDialog: false,
            openPriceDialog: false
        };
        this.handleCloseApplyDialog = this.handleCloseApplyDialog.bind(this);
        this.handleOpenApplyDialog = this.handleOpenApplyDialog.bind(this);
        this.handleClosePriceDialog = this.handleClosePriceDialog.bind(this);
        this.handleOpenPriceDialog = this.handleOpenPriceDialog.bind(this);
    }

    handleLogout() {
        // check if there if the username
        if (!this.props.username) return;
        // logout request
        this.props.redirectHandler("/logout");
    }

    handleCloseApplyDialog() {
        this.setState({openApplyDialog: false});
    }

    handleOpenApplyDialog() {
        this.setState({openApplyDialog: true});
    }

    handleClosePriceDialog() {
        this.setState({openPriceDialog: false});
    }

    handleOpenPriceDialog() {
        this.setState({openPriceDialog: true})
    }

    render() {
        return (
            <>
                <Card>
                    <CardContent>
                        <Box
                            sx={{
                                alignItems: "center",
                                display: "flex",
                                flexDirection: "column",
                            }}
                        >
                            <Avatar
                                src={this.props.avatar}
                                sx={{
                                    height: 100,
                                    width: 100,
                                }}
                            />
                            <Box mt={2}>
                                <Typography
                                    color="textPrimary"
                                    gutterBottom
                                    variant="h4"
                                >
                                    {this.props.nickname === ""
                                        ? "匿名用户"
                                        : this.props.nickname}
                                </Typography>
                            </Box>
                            <Box mx={2}>
                                <Typography color="textSecondary" variant="body1">
                                    {this.props.permission === "q"
                                        ? "你还不是问答者，快去申请吧~"
                                        : "你已经是问答者了，快去回答问题吧~"}
                                </Typography>
                            </Box>
                        </Box>
                    </CardContent>
                    <Divider />
                    <CardActions>
                        {
                            this.props.permission === "q" ?
                            <Button
                                color="primary"
                                fullWidth
                                variant="text"
                                onClick={this.handleOpenApplyDialog}
                            >
                                问答者申请
                            </Button> :
                            <Button
                                color="primary"
                                fullWidth
                                variant="text"
                                onClick={this.handleOpenPriceDialog}
                            >
                                修改定价
                            </Button>
                        }
                    </CardActions>
                </Card>
                <Dialog fullWidth open={this.state.openApplyDialog} onClose={this.handleCloseApplyDialog}>
                    <DialogTitle>申请成为问答者</DialogTitle>
                    <DialogContent>
                        <DialogContentText mb={3}>
                            在申请成为问答者前，请重新填写您的个人介绍。
                            个人介绍是审核的重要根据，并且在提交成功后<Box component="span" fontWeight='fontWeightBold'>不可修改</Box>。
                            优秀的、有展示性的个人介绍能帮助您获得更多提问者的青睐。
                        </DialogContentText>
                        <TextField
                            fullWidth
                            autoFocus
                            label="个人介绍"
                            name="description"
                            multiline
                            // onChange={this.handleChange}
                            rows={4}
                            // value={this.state.description}
                            placeholder="快来介绍一下你自己吧~"
                            variant="outlined"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseApplyDialog} color="error">取消</Button>
                        <Button onClick={this.handleCloseApplyDialog}>提交</Button>
                    </DialogActions>
                </Dialog>
            </>
        );
    }
}
