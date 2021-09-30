import React, {useEffect, useState} from "react";
import Card from "@mui/material/Card";
import {UserBasicInfo} from "../services/definations";
import userService from "../services/user.service"
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import Typography from "@mui/material/Typography";
import CardContent from "@mui/material/CardContent";
import Skeleton from "@mui/material/Skeleton";
import CardActionArea from "@mui/material/CardActionArea";
import {Link as RouterLink} from "react-router-dom";

const UserCard: React.FC<{ userId: number, nextUrl?: string }> = props => {
    const [userInfo, setUserInfo] = useState<UserBasicInfo>();

    useEffect(() => {
        userService.get_user_basic_info(props.userId).then(user => setUserInfo(user))
    }, []);

    const CardActionWrapper: React.FC<{ nextUrl?: string }> = props => {
        return props.nextUrl ? (
            <CardActionArea
                component={RouterLink}
                to={props.nextUrl}
            >
                {props.children}
            </CardActionArea>
        ) : (
            <>
                {props.children}
            </>
        );
    }

    return userInfo ? (
        <Card>
            <CardActionWrapper nextUrl={props.nextUrl}>
                <CardHeader
                    avatar={
                        <Avatar alt={userInfo.name} src={userInfo.avatarUrl}/>
                    }
                    title={userInfo.name}
                />
                <CardContent>
                    <Typography variant="body2" color="text.secondary">
                        {userInfo.introduction}
                    </Typography>
                </CardContent>
            </CardActionWrapper>
        </Card>
    ) : (
        <Card>
            <CardHeader
                avatar={
                    <Skeleton variant="circular">
                        <Avatar/>
                    </Skeleton>
                }
                title={
                    <Skeleton variant="text"/>
                }
            />
            <CardContent>
                <Skeleton variant="rectangular" height={50}/>
            </CardContent>
        </Card>
    )
}

export default UserCard;