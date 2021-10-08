import React, { useEffect, useState } from "react";
import {UserBasicInfo} from "../services/definations";
import userService from "../services/user.service";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import UserCard from "./userCard";

const AnswerList: React.FC<{ type: string }> = (props) => {
    const [answerList, setAnswerList] = useState<Array<UserBasicInfo>>();
    useEffect(() => {
        userService
            .get_users_of_type(props.type)
            .then((list) => setAnswerList(list));
    }, []);

    const renderAnswerList = () => {
        const list = (answerList == null ?
            (new Array(6)).fill({
                id: -1,
                avatarUrl: "",
                name: "",
                introduction: "",
                type: 1
            })
            : answerList);

        return (
            <>
                {
                    list.filter((user: UserBasicInfo) => {return user.type === 1;})
                        .map((user: UserBasicInfo, index: number) => {
                        return (
                            <Grid
                                item
                                key={index}
                                lg={4}
                                md={6}
                                xs={12}
                            >
                                <UserCard userId={user.id}/>
                            </Grid>
                        );
                })}
            </>
        );
    };

    return (
        <Box sx={{ pt: 3 }} mt={1}>
            <Grid
                container
                spacing={3}
            > {renderAnswerList()}
            </Grid>
        </Box>
    );
};

export default AnswerList;
