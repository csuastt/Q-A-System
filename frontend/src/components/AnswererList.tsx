import React, { useEffect, useState } from "react";
import { UserBasicInfo, UserRole } from "../services/definations";
import userService from "../services/userService";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import AnswererCard from "./AnswererCard";
import Typography from "@mui/material/Typography";
import { parseIntWithDefault, useQuery } from "../util";
import Pagination from "./Pagination";

const AnswererList: React.FC<{ selectModel?: boolean; userRole: UserRole }> = (
    props
) => {
    const query = useQuery();
    const [answerList, setAnswerList] = useState<Array<UserBasicInfo>>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const [itemPrePage] = useState(
        parseIntWithDefault(query.get("prepage"), 9)
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [totalCount, setTotalCount] = useState(0);

    useEffect(() => {
        userService
            .getUserList(
                props.userRole === UserRole.ANSWERER,
                currentPage,
                itemPrePage
            )
            .then((list) => {
                // list.data.forEach((user) => (user.role = props.userRole));
                setAnswerList(list.data);
                setMaxPage(list.totalPages);
                setTotalCount(list.totalCount);
            });
    }, [currentPage, itemPrePage, props.userRole]);

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };

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
    if (totalCount === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有可用的回答者
            </Typography>
        );
    }
    return (
        <Box sx={{ pt: 3 }} mt={1}>
            <Grid container spacing={3} marginBottom={3}>
                {answerList.map((user: UserBasicInfo, index: number) => (
                    <Grid
                        item
                        key={index}
                        lg={4}
                        md={6}
                        xs={12}
                        sx={{
                            display: "flex",
                            flexDirection: "column",
                            flex: 1,
                        }}
                    >
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
            {maxPage > 1 && (
                <Pagination
                    currentPage={currentPage}
                    maxPage={maxPage}
                    totalCount={totalCount}
                    itemPrePage={itemPrePage}
                    onPageChanged={onPageChanged}
                />
            )}
        </Box>
    );
};

export default AnswererList;
