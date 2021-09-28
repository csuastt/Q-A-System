// mui
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Card from '@mui/material/Card'
import CardActions from '@mui/material/CardActions'
import Divider from '@mui/material/Divider'
import CardContent from '@mui/material/CardContent'

interface AccountBriefProfileProps {
    avatar: string,
    nickname: string,
    permission: string
}

const AccountBriefProfile = (props: AccountBriefProfileProps) => (
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
                    src={props.avatar}
                    sx={{
                        height: 100,
                        width: 100
                    }}
                />
                <Box mt = {2}>
                    <Typography
                        color="textPrimary"
                        gutterBottom
                        variant="h4"
                    >
                        {props.nickname === ''? '默认昵称' : props.nickname}
                    </Typography>
                </Box>
                <Typography
                    color="textSecondary"
                    variant="body1"
                >
                    {props.permission === 'q'? '你还不是问答者，快去申请吧~':
                        '你已经是问答者了，快去回答问题吧~'}
                </Typography>
            </Box>
        </CardContent>
        <Divider />
        <CardActions>
            <Button
                color="primary"
                fullWidth
                variant="text"
            >
                退出登录
            </Button>
        </CardActions>
    </Card>
);

export default AccountBriefProfile;

// default props
AccountBriefProfile.defaultProps = {
    avatar: '/static/images/avatar_default.png',
    nickname: '默认昵称',
    permission: 'q'
}