import AuthService from "../services/auth.service";
// mui
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Card from '@mui/material/Card'
import CardActions from '@mui/material/CardActions'
import Divider from '@mui/material/Divider'
import CardContent from '@mui/material/CardContent'
import {Component} from "react";

interface AccountBriefProfileProps {
    avatar: string,
    nickname: string | undefined,
    username: string | undefined,
    permission: string | undefined,
    alertHandler: (arg1:"success" | "info" | "warning" | "error", arg2:string)=>void,
    redirectHandler: (arg1: string)=>void
}

export default class AccountBriefProfile extends Component<AccountBriefProfileProps, any> {

    handleLogout(){
        // check if there if the username
        if (!this.props.username)
            return;
        // logout request
        AuthService.logout(this.props.username).then(
            () => {
                // logout success
                // redirect and alert
                if (this.props.alertHandler) {
                    this.props.alertHandler('success', 'Logout success!');
                }
                if (this.props.redirectHandler) {
                    this.props.redirectHandler("/");
                }
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
                if (this.props.alertHandler) {
                    this.props.alertHandler('error', resMessage);
                }
            }
        );
    }

    render() {
        return (
        <Card>
            <CardContent>
                <Box
                    sx={{
                        alignItems: 'center',
                        display: 'flex',
                        flexDirection: 'column'
                    }}
                >
                    <Avatar
                        src={this.props.avatar}
                        sx={{
                            height: 100,
                            width: 100
                        }}
                    />
                    <Box mt={2}>
                        <Typography
                            color="textPrimary"
                            gutterBottom
                            variant="h4"
                        >
                            {this.props.nickname === '' ? '默认昵称' : this.props.nickname}
                        </Typography>
                    </Box>
                    <Typography
                        color="textSecondary"
                        variant="body1"
                    >
                        {this.props.permission === 'q' ? '你还不是问答者，快去申请吧~' :
                            '你已经是问答者了，快去回答问题吧~'}
                    </Typography>
                </Box>
            </CardContent>
            <Divider/>
            <CardActions>
                <Button
                    color="primary"
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
