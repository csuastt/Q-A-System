import React, { useEffect, useState } from "react";
import Card from "@mui/material/Card";
import { UserBasicInfo } from "../services/definations";
import userService from "../services/user.service";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import Typography from "@mui/material/Typography";
import CardContent from "@mui/material/CardContent";
import Skeleton from "@mui/material/Skeleton";
import CardActionArea from "@mui/material/CardActionArea";
import { Link as RouterLink } from "react-router-dom";
import Box from "@mui/material/Box";
import CardActions from "@mui/material/CardActions";
import Button from "@mui/material/Button";

const UserCard: React.FC<{ userId: number; nextUrl?: string }> = (props) => {
    const [userInfo, setUserInfo] = useState<UserBasicInfo>();

    useEffect(() => {
        userService
            .get_user_basic_info(props.userId)
            .then((user) => setUserInfo(user));
    }, []);

    const CardActionWrapper: React.FC<{ nextUrl?: string }> = (props) => {
        return props.nextUrl ? (
            <CardActionArea component={RouterLink} to={props.nextUrl}>
                {props.children}
            </CardActionArea>
        ) : (
            <>{props.children}</>
        );
    };

    return userInfo ? (
        <Card>
            <CardActionWrapper nextUrl={props.nextUrl}>
                <CardContent>
                    <Box
                        sx={{
                            alignItems: "center",
                            display: "flex",
                            flexDirection: "column",
                        }}
                    >
                        <Avatar
                            alt={userInfo.name}
                            src={userInfo.avatarUrl}
                            sx={{
                                height: 70,
                                width: 70
                            }}
                        />
                        <Box mt={1}>
                            <Typography
                                color="textPrimary"
                                gutterBottom
                                variant="h4"
                            >
                                {userInfo.name}
                            </Typography>
                        </Box>
                        <Box mx={2} mt={-1}>
                            <Typography color="textSecondary" variant="body1">
                                {userInfo.introduction}
                            </Typography>
                        </Box>
                        {userInfo.type === 1 ? (
                                <Box mt={1}>
                                    <Typography
                                        color="secondary"
                                        variant="h4"
                                    >
                                        {"￥49.9/次"}
                                    </Typography>
                                </Box>
                            ):
                            (<></>)}
                    </Box>
                </CardContent>
                {userInfo.type === 1 ? (
                <CardActions
                    style={{justifyContent: 'center'}}
                >
                    <Box mb={1} mt={-2}>
                        <Button
                            color="secondary"
                            size="large"
                            variant="contained"
                        >
                            向TA提问
                        </Button>
                    </Box>
                </CardActions>
                ):
                (<></>)}
            </CardActionWrapper>
        </Card>
    ) : (
        <Card>
            <CardHeader
                avatar={
                    <Skeleton variant="circular">
                        <Avatar />
                    </Skeleton>
                }
                title={<Skeleton variant="text" height={30}/>}
            />
            <CardContent>
                <Skeleton variant="rectangular" height={50} />
            </CardContent>
        </Card>
    );
};

export default UserCard;
