import React, { useEffect, useState } from "react";
import Card from "@mui/material/Card";
import { UserBasicInfo} from "../services/definations";
import userService from "../services/userService";
import CardHeader from "@mui/material/CardHeader";
import CardContent from "@mui/material/CardContent";
import TextField from "@mui/material/TextField";
import {Typography} from "@mui/material";

const AnswererDetailCard: React.FC<{
    userId: number;
}> = (props) => {
    const [userInfo, setUserInfo] = useState<UserBasicInfo>();

    useEffect(() => {
        userService
            .getUserBasicInfo(props.userId)
            .then((user) => setUserInfo(user));
    }, [props.userId]);

    let description = null;
    let profession = null;
    if (userInfo) {
        let arr = userInfo.description.split("EwbkK8TU", 2);
        description = arr[0];
        profession = arr.length > 1 ? arr[1] : "";
    }

    return (
        <Card>
            <CardContent>
                <Typography sx={{ fontSize: 18 }} color="text.secondary" gutterBottom>
                    关于回答者
                </Typography>
                <Typography variant="h4" component="div">
                    {userInfo?.nickname}
                </Typography>
                <Typography sx={{ mt: 2, mb: 1.5,fontSize: 16}} color="text.secondary">
                    专业领域
                </Typography>
                <Typography sx={{ fontSize: 20}} >
                    {
                        profession !== null
                            ? profession
                            : "该回答者很懒～还没有填写专业领域哦～"
                    }
                </Typography>
                <Typography sx={{ mt: 1.5, mb: 1.5,fontSize: 16}} color="text.secondary">
                    个人介绍
                </Typography>
                <Typography sx={{ fontSize: 20}}>
                    {
                        description !== null
                            ? description
                            : "该回答者很懒～还没有填写个人介绍哦～"
                    }
                </Typography>
            </CardContent>
        </Card>
    ) ;
};

export default AnswererDetailCard;
