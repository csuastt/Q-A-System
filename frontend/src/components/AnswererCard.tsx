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
import { Grid } from "@mui/material";
import AnswererDetailDialog from "./AnswererDetailDialog";

const AnswererCard: React.FC<{
    userInfo?: UserBasicInfo;
    userId?: number;
    placeholder?: boolean;
    nextUrl?: string;
}> = (props) => {
    const [userInfo, setUserInfo] = useState<UserBasicInfo>();
    const [dialogOpen, setDialogOpen] = useState(false);

    useEffect(() => {
        if (props.placeholder) {
            return;
        } else if (props.userId) {
            userService
                .getUserBasicInfo(props.userId)
                .then((user) => setUserInfo(user));
        } else if (props.userInfo) {
            setUserInfo(props.userInfo);
        }
    }, [props.placeholder, props.userId, props.userInfo]);

    const handleOpenDialog = () => setDialogOpen(true);
    const handleCloseDialog = () => setDialogOpen(false);

    const CardActionWrapper: React.FC<{ nextUrl?: string }> = (
        wrapperProps
    ) => {
        return wrapperProps.nextUrl ? (
            // If has nextUrl, Card will follow it
            <CardActionArea component={RouterLink} to={wrapperProps.nextUrl}>
                {wrapperProps.children}
            </CardActionArea>
        ) : (
            // Or it will show answerer's detail dialog
            <CardActionArea onClick={handleOpenDialog}>
                {wrapperProps.children}
            </CardActionArea>
        );
    };

    return userInfo ? (
        <>
            <Card>
                <CardActionWrapper nextUrl={props.nextUrl}>
                    <CardContent sx={{ paddingBottom: 0 }}>
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
                                <Typography
                                    color="textSecondary"
                                    variant="body1"
                                >
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
                                        <Typography
                                            color="primary"
                                            variant="h6"
                                        >
                                            ￥
                                        </Typography>
                                    </Grid>
                                    <Grid item>
                                        <Typography
                                            color="primary"
                                            variant="h4"
                                        >
                                            49.9
                                        </Typography>
                                    </Grid>
                                    <Grid item>
                                        <Typography
                                            color="primary"
                                            variant="h6"
                                        >
                                            /次
                                        </Typography>
                                    </Grid>
                                </Grid>
                            ) : (
                                <></>
                            )}
                        </Box>
                    </CardContent>
                </CardActionWrapper>
                <CardActions
                    style={{ justifyContent: "center" }}
                    sx={{ paddingBottom: 2 }}
                >
                    <Button
                        color="primary"
                        size="large"
                        variant="outlined"
                        onClick={handleOpenDialog}
                    >
                        详细信息
                    </Button>
                    {props.nextUrl && (
                        <Button
                            color="primary"
                            size="large"
                            variant="contained"
                            component={RouterLink}
                            to={props.nextUrl}
                            sx={{ ml: 2 }}
                        >
                            向TA提问
                        </Button>
                    )}
                </CardActions>
            </Card>
            <AnswererDetailDialog
                open={dialogOpen}
                onClose={handleCloseDialog}
                info={userInfo}
                maxWidth="sm"
                fullWidth
            />
        </>
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

export default AnswererCard;
