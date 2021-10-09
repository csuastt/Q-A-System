import { useTheme } from "@mui/material/styles";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import LoginIcon from "@mui/icons-material/Login";
import CardActionArea from "@mui/material/CardActionArea";
import { Link as RouterLink } from "react-router-dom";
import React from "react";
import SvgIcon from "@mui/material/SvgIcon/SvgIcon";
import PersonAddIcon from "@mui/icons-material/PersonAdd";
import ContactsIcon from "@mui/icons-material/Contacts";

export default function Welcome() {
    const theme = useTheme();

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
                欢迎来到付费问答系统
            </Typography>
            <ButtonCardWrapper
                to="/answerers"
                Icon={ContactsIcon}
                title1="回答者列表"
                title2="寻找合适的回答者"
            />
            <ButtonCardWrapper
                to="/login"
                Icon={LoginIcon}
                title1="登陆"
                title2="提出问题、回答问题"
            />
            <ButtonCardWrapper
                to="/register"
                Icon={PersonAddIcon}
                title1="注册"
                title2="从现在开始解答您的疑惑"
            />
        </>
    );
}
