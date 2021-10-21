import React, { useEffect, useState } from "react";
import { UserBasicInfo } from "../services/definations";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import IconButton from "@mui/material/IconButton";
import userService from "../services/user.service";
import CardActionArea from "@mui/material/CardActionArea";
import { Link as RouterLink } from "react-router-dom";

export interface UserCardProps {
    userInfo?: UserBasicInfo;
    userId?: number;
    link?: boolean;
}

const CardWrapper: React.FC<{ link?: boolean; userId: number }> = (props) => {
    return props.link ? (
        <Card>
            <CardActionArea
                component={RouterLink}
                to={`/profile/${props.userId}`}
            >
                {props.children}
            </CardActionArea>
        </Card>
    ) : (
        <Card>{props.children}</Card>
    );
};

const UserCard: React.FC<UserCardProps> = (props) => {
    const [userInfo, setUserInfo] = useState<UserBasicInfo>();

    useEffect(() => {
        if (props.userInfo) {
            setUserInfo(props.userInfo);
        } else {
            userService
                .getUserBasicInfo(props.userId!)
                .then((value) => setUserInfo(value));
        }
    }, [props.userId, props.userInfo]);

    return userInfo ? (
        <Card sx={{ maxWidth: 345 }}>
            <CardHeader
                avatar={<Avatar aria-label="recipe">R</Avatar>}
                title="Shrimp and Chorizo Paella"
                subheader="September 14, 2016"
            />
        </Card>
    ) : (
        <Card sx={{ maxWidth: 345 }}>
            <CardHeader
                avatar={<Avatar aria-label="recipe">R</Avatar>}
                title="Shrimp and Chorizo Paella"
                subheader="September 14, 2016"
            />
        </Card>
    );
};

export default UserCard;
