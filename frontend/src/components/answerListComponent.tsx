import React, { useEffect, useState } from "react";
import { UserBasicInfo } from "../services/definations";
import userService from "../services/user.service";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import UserCard from "./userCard";

const AnswererList: React.FC<{ type: string; selectModel?: boolean }> = (
    props
) => {
    const [answerList, setAnswerList] = useState<Array<UserBasicInfo>>();
    useEffect(() => {
        userService.getUserList(true).then((list) => setAnswerList(list));
    }, []);

    const renderAnswerList = () => {
        return answerList == null ? (
            <Grid item lg={4} md={6} xs={12}>
                <UserCard placeholder={true} />
            </Grid>
        ) : (
            <>
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
            </>
        );
    };

    return (
        <Box sx={{ pt: 3 }} mt={1}>
            <Grid container spacing={3}>
                {" "}
                {renderAnswerList()}
            </Grid>
        </Box>
    );
};

export default AnswererList;
