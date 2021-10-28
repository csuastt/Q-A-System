import React, { useEffect, useState } from "react";
import { UserBasicInfo, UserRole } from "../services/definations";
import userService from "../services/userService";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import AnswererCard from "./AnswererCard";
import Typography from "@mui/material/Typography";
import { parseIntWithDefault, useQuery } from "../util";
import Pagination, { usePagination } from "./Pagination";
import { Redirect } from "react-router-dom";

const AnswererList: React.FC<{ selectModel?: boolean }> = (props) => {
    const query = useQuery();
    const [answerList, setAnswerList] = useState<Array<UserBasicInfo>>();
    const [shouldRedirect, setShouldRedirect] = useState<string>();
    const paginationInfo = usePagination(
        parseIntWithDefault(query.get("page"), 1),
        parseIntWithDefault(query.get("prepage"), 20)
    );

    useEffect(() => {
        userService.getUserList(true).then((list) => {
            list.data.forEach((user) => (user.role = UserRole.ANSWERER));
            setAnswerList(list.data);
            paginationInfo.applyPagedList(list);
        });
    }, [paginationInfo]);

    const onPageChanged = (newPage: number) => {
        setShouldRedirect(
            `/answerers?page=${newPage}&prepage=${paginationInfo.itemPrePage}`
        );
    };

    if (shouldRedirect) {
        return <Redirect to={shouldRedirect} />;
    }
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
    }
    if (answerList.length === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有可用的回答者
            </Typography>
        );
    }
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
            {paginationInfo.maxPage > 1 && (
                <Pagination {...paginationInfo} onPageChanged={onPageChanged} />
            )}
        </Box>
    );
};

export default AnswererList;
