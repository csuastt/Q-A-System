import React, { useEffect, useState } from "react";
import { UserBasicInfo, UserRole } from "../services/definations";
import userService from "../services/userService";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import AnswererCard from "./AnswererCard";
import Typography from "@mui/material/Typography";

const AnswererList: React.FC<{ selectModel?: boolean }> = (props) => {
    const [answerList, setAnswerList] = useState<Array<UserBasicInfo>>();
    useEffect(() => {
        userService.getUserList(true).then((list) => {
            list.forEach((user) => (user.role = UserRole.ANSWERER));
            setAnswerList(list);
        });
    }, []);

    if (answerList == null) {
        return (
            <Box sx={{ pt: 3 }} mt={1}>
                <Grid container spacing={3}>
                    <Grid item lg={4} md={6} xs={12}>
                        <AnswererCard placeholder={true} />
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
                            <AnswererCard
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
