import React, { useEffect, useState } from "react";
import { UserBasicInfo } from "../services/definations";
import userService from "../services/user.service";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import UserCard from "./userCard";
import Typography from "@mui/material/Typography";

const AnswererList: React.FC<{ selectModel?: boolean }> = (props) => {
    const [answerList, setAnswerList] = useState<Array<UserBasicInfo>>();
    useEffect(() => {
        userService.getUserList(true).then((list) => {
            setAnswerList(list);
            console.log(list);
        });
    }, []);

    if (answerList == null) {
        return (
            <Box sx={{ pt: 3 }} mt={1}>
                <Grid container spacing={3}>
                    <Grid item lg={4} md={6} xs={12}>
                        <UserCard placeholder={true} />
                    </Grid>
                </Grid>
            </Box>
        );
    } else if (answerList.length === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有可用的回答者
            </Typography>
        );
    } else {
        return (
            <Box sx={{ pt: 3 }} mt={1}>
                <Grid container spacing={3}>
                    {answerList.map((user: UserBasicInfo, index: number) => (
                        <Grid item key={index} lg={4} md={6} xs={12}>
                            <UserCard
                                userInfo={user}
                                nextUrl={
                                    props.selectModel
                                        ? `/order/create/${user.id}`
                                        : undefined
                                }
                            />
                        </Grid>
                    ))}
                </Grid>
            </Box>
        );
    }
};

export default AnswererList;
