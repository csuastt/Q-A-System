import { useTheme } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import LoginIcon from "@mui/icons-material/Login";
import CardActionArea from "@mui/material/CardActionArea";
import { Link as RouterLink } from "react-router-dom";
import React, { useContext } from "react";
import SvgIcon from "@mui/material/SvgIcon/SvgIcon";
import ContactsIcon from "@mui/icons-material/Contacts";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import FormatListBulletedIcon from "@mui/icons-material/FormatListBulleted";
import AccountCircle from "@mui/icons-material/AccountCircle";
import { LiveHelp } from "@mui/icons-material";
import AuthContext from "../AuthContext";

export default function HelloAdmin() {
    const theme = useTheme();
    const { manager } = useContext(AuthContext);

    const ButtonCardWrapper: React.FC<{
        to: string;
        Icon: typeof SvgIcon;
        title1: string;
        title2: string;
    }> = (props) => (
        <Card
            sx={{
                display: "flex",
                margin: theme.spacing(2),
            }}
        >
            <CardActionArea component={RouterLink} to={props.to}>
                <CardContent>
                    <Box sx={{ display: "flex", flexDirection: "row" }}>
                        <props.Icon
                            sx={{
                                margin: theme.spacing(2, 2, 2, 0),
                            }}
                        />
                        <Box sx={{ flexDirection: "column" }}>
                            <Typography component="div" variant="h5">
                                {props.title1}
                            </Typography>
                            <Typography
                                variant="subtitle1"
                                color="text.secondary"
                                component="div"
                            >
                                {props.title2}
                            </Typography>
                        </Box>
                    </Box>
                </CardContent>
            </CardActionArea>
        </Card>
    );

    return (
        <>
            <Typography
                align="center"
                variant="h5"
                sx={{ margin: theme.spacing(3, 2, 2, 0) }}
            >
                欢迎来到管理员系统
            </Typography>
            {manager ? (
                <>
                    <ButtonCardWrapper
                        to="/admins/review"
                        Icon={ContactsIcon}
                        title1="审核列表"
                        title2="查看待审核订单"
                    />
                    <ButtonCardWrapper
                        to="/order/create"
                        Icon={LiveHelp}
                        title1="用户列表"
                        title2="查看所有用户"
                    />
                    <ButtonCardWrapper
                        to="/orders"
                        Icon={FormatListBulletedIcon}
                        title1="回答者列表"
                        title2="查看所有回答者"
                    />
                    <ButtonCardWrapper
                        to="/profile"
                        Icon={AccountCircle}
                        title1="个人信息"
                        title2="查看您的信息"
                    />
                </>
            ) : (
                <>
                    <ButtonCardWrapper
                        to="/admins/login"
                        Icon={LoginIcon}
                        title1="登录"
                        title2="快来开始工作吧"
                    />
                    <ButtonCardWrapper
                        to="/admins/create"
                        Icon={PersonAddIcon}
                        title1="注册"
                        title2="只限超级管理员"
                    />
                </>
            )}
        </>
    );
}
