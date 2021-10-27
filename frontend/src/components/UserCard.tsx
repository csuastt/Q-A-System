import React, { useEffect, useState } from "react";
import Card from "@mui/material/Card";
import { UserBasicInfo } from "../services/definations";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import userService from "../services/userService";

export interface UserCardProps {
    userInfo?: UserBasicInfo;
    userId?: number;
    link?: boolean;
}

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
