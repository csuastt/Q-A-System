import React, { useEffect, useState } from "react";
import Card from "@mui/material/Card";
import { UserInfo, UserType } from "../services/definations";
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
import { Grid } from "@mui/material";

const UserCard: React.FC<{ userId: number; nextUrl?: string }> = (props) => {
    const [userInfo, setUserInfo] = useState<UserInfo>();

    useEffect(() => {
        userService.getUserInfo(props.userId).then((user) => setUserInfo(user));
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
                            alt={userInfo.username}
                            src={userInfo.ava_url}
                            sx={{
                                height: 70,
                                width: 70,
                            }}
                        />
                        <Box mt={1}>
                            <Typography
                                color="textPrimary"
                                gutterBottom
                                variant="h5"
                            >
                                {userInfo.username}
                            </Typography>
                        </Box>
                        <Box mx={2} mt={-1}>
                            <Typography color="textSecondary" variant="body1">
                                {userInfo.description}
                            </Typography>
                        </Box>
                        {userInfo.type === 1 ? (
                            <Grid
                                container
                                mt={0.5}
                                direction="row"
                                justifyContent="center"
                                alignItems="flex-end"
                            >
                                <Grid item>
                                    <Typography color="primary" variant="h6">
                                        ￥
                                    </Typography>
                                </Grid>
                                <Grid item>
                                    <Typography color="primary" variant="h4">
                                        49.9
                                    </Typography>
                                </Grid>
                                <Grid item>
                                    <Typography color="primary" variant="h6">
                                        /次
                                    </Typography>
                                </Grid>
                            </Grid>
                        ) : (
                            <></>
                        )}
                    </Box>
                </CardContent>
                {userInfo.type === UserType.Answerer ? (
                    <CardActions style={{ justifyContent: "center" }}>
                        <Box mb={1.5} mt={-2}>
                            <Button
                                color="primary"
                                size="large"
                                variant="contained"
                            >
                                向TA提问
                            </Button>
                        </Box>
                    </CardActions>
                ) : (
                    <></>
                )}
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
                title={<Skeleton variant="text" height={30} />}
            />
            <CardContent>
                <Skeleton variant="rectangular" height={50} />
            </CardContent>
        </Card>
    );
};

export default UserCard;
