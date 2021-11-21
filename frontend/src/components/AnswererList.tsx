import React, { useEffect, useState } from "react";
import { UserBasicInfo, UserRole } from "../services/definations";
import userService from "../services/userService";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import AnswererCard from "./AnswererCard";
import Typography from "@mui/material/Typography";
import UserList from "./UserList";
import { parseIntWithDefault, useQuery } from "../util";
import Pagination from "./Pagination";
import {
    Button,
    FormControl,
    InputLabel,
    List,
    MenuItem,
    Select,
    Stack,
} from "@mui/material";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import TrendingDownIcon from "@mui/icons-material/TrendingDown";

const AnswererList: React.FC<{
    selectModel?: boolean;
    briefMsg?: boolean;
    userRole: UserRole;
    isSuperAdmin?: boolean;
}> = (props) => {
    const query = useQuery();
    const [answerList, setAnswerList] = useState<Array<UserBasicInfo>>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const [itemPrePage] = useState(
        parseIntWithDefault(
            query.get("prepage"),
            props.isSuperAdmin || props.briefMsg ? 5 : 9
        )
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [totalCount, setTotalCount] = useState(0);
    const [longPending, setLongPending] = useState(false);
    const [errorFlag, setErrorFlag] = useState(false);
    const [sortProperty, setSortProperty] = useState("id");
    const [sortOrder, setSortOrder] = useState("ASC");

    useEffect(() => {
        userService
            .getUserList(
                props.userRole === UserRole.ANSWERER,
                currentPage,
                itemPrePage,
                sortOrder,
                sortProperty
            )
            .then(
                (list) => {
                    // list.data.forEach((user) => (user.role = props.userRole));
                    setAnswerList(list.data);
                    setMaxPage(list.totalPages);
                    setTotalCount(list.totalCount);
                },
                () => setErrorFlag(true)
            );

        setTimeout(() => {
            setLongPending(true);
        }, 500);
    }, [currentPage, itemPrePage, props.userRole, sortOrder, sortProperty]);

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };

    if (errorFlag) {
        return <Typography>加载失败</Typography>;
    }
    if (longPending && !answerList) {
        return props.briefMsg ? (
            <UserList userRole={UserRole.ANSWERER} renderPlaceHolder={true} />
        ) : (
            <Box sx={{ pt: 3 }} mt={1}>
                <Grid container spacing={3}>
                    <Grid item lg={4} md={6} xs={12}>
                        <AnswererCard placeholder={true} />
                    </Grid>
                </Grid>
            </Box>
        );
    }
    if (answerList && totalCount === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有可用的回答者
            </Typography>
        );
    }
    return props.briefMsg ? (
        <Box>
            {answerList && (
                <List
                    sx={{
                        width: "100%",
                        paddingTop: 0,
                    }}
                >
                    {answerList.map((user: UserBasicInfo, index: number) => (
                        <AnswererCard
                            userInfo={user}
                            nextUrl={
                                props.selectModel
                                    ? `/order/create/${user.id}`
                                    : undefined
                            }
                            listMode={true}
                        />
                    ))}
                </List>
            )}
        </Box>
    ) : (
        <Box sx={{ pt: 3 }} mt={1}>
            <Stack
                direction={"row"}
                justifyContent="flex-start"
                alignItems="center"
                spacing={2}
                mb={2}
            >
                <FormControl
                    variant="outlined"
                    sx={{ minWidth: 120 }}
                    size={"small"}
                >
                    <InputLabel id="demo-simple-select-label">
                        排序依据
                    </InputLabel>
                    <Select
                        labelId="demo-simple-select-label"
                        id="demo-simple-select"
                        value={sortProperty}
                        label="sort-property"
                        onChange={(e) => {
                            setSortProperty(e.target.value);
                        }}
                    >
                        <MenuItem value={"id"}>ID</MenuItem>
                        <MenuItem value={"price"}>定价</MenuItem>
                        <MenuItem value={"ratingCount"}>评分次数</MenuItem>
                        <MenuItem value={"ratingTotal"}>总评分</MenuItem>
                        <MenuItem value={"rating"}>平均评分</MenuItem>
                    </Select>
                </FormControl>
                <Button
                    startIcon={
                        sortOrder === "ASC" ? (
                            <TrendingUpIcon />
                        ) : (
                            <TrendingDownIcon />
                        )
                    }
                    onClick={() => {
                        sortOrder === "ASC"
                            ? setSortOrder("DESC")
                            : setSortOrder("ASC");
                    }}
                    size={"large"}
                >
                    {sortOrder === "ASC" ? "升序" : "降序"}
                </Button>
            </Stack>
            {answerList && (
                <Grid container spacing={3} marginBottom={3}>
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
            )}
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
