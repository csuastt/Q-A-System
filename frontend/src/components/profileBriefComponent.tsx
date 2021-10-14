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

interface AccountBriefProfileProps {
    avatar: string;
    nickname: string | undefined;
    username: string | undefined;
    permission: string | undefined;
    alertHandler: (
        arg1: "success" | "info" | "warning" | "error",
        arg2: string
    ) => void;
    redirectHandler: (arg1: string) => void;
}

export default class AccountBriefProfile extends Component<
    AccountBriefProfileProps,
    any
> {
    handleLogout() {
        // check if there if the username
        if (!this.props.username) return;
        // logout request
        this.props.redirectHandler("/logout");
    }

    render() {
        return (
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
        );
    }
}
