// mui
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import Divider from "@mui/material/Divider";
import CardContent from "@mui/material/CardContent";
import { Component } from "react";
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
        this.handleClosePriceDialog = this.handleClosePriceDialog.bind(this);
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
                        <Button
                            color="error"
                            fullWidth
                            variant="text"
                            onClick={this.handleLogout.bind(this)}
                        >
                            退出登录
                        </Button>
                    </CardActions>
                </Card>
                <Dialog open={this.state.openApplyDialog} onClose={this.handleCloseApplyDialog}>
                    <DialogTitle>Subscribe</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            To subscribe to this website, please enter your email address here. We
                            will send updates occasionally.
                        </DialogContentText>
                        <TextField
                            autoFocus
                            margin="dense"
                            id="name"
                            label="Email Address"
                            type="email"
                            fullWidth
                            variant="standard"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.handleCloseApplyDialog}>Cancel</Button>
                        <Button onClick={this.handleCloseApplyDialog}>Subscribe</Button>
                    </DialogActions>
                </Dialog>
            </>
        );
    }
}
