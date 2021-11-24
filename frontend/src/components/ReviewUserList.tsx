import Button from "@mui/material/Button";
import CardActionArea from "@mui/material/CardActionArea";
import React, { useContext, useEffect, useState } from "react";
import { parseIntWithDefault, useQuery } from "../util";
import { ManagerRole, PagedList, UserBasicInfo } from "../services/definations";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Box from "@mui/material/Box";
import Skeleton from "@mui/material/Skeleton";
import Typography from "@mui/material/Typography";
import Avatar from "@mui/material/Avatar";
import Pagination from "./Pagination";
import Stack from "@mui/material/Stack";
import userService from "../services/userService";
import { Link as RouterLink } from "react-router-dom";
import AuthContext from "../AuthContext";

interface ReviewListProps {
    filterFinished?: boolean;
    initCurrentPage?: number;
    itemPrePage?: number;
}

const ReviewUserList: React.FC<ReviewListProps> = (props) => {
    const query = useQuery();
    const [userList, setUserList] = useState<Array<UserBasicInfo>>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const { manager } = useContext(AuthContext);
    const [itemPrePage] = useState(
        parseIntWithDefault(query.get("prepage"), 10)
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [totalCount, setTotalCount] = useState(0);
    const [longPending, setLongPending] = useState(false);

    const acceptUserList: (list: PagedList<UserBasicInfo>) => void = (list) => {
        setUserList(list.data);
        setMaxPage(list.totalPages);
        setTotalCount(list.totalCount);
    };

    useEffect(() => {
        userService
            .getReviewUserList(currentPage, itemPrePage, true)
            .then(acceptUserList);
        setTimeout(() => {
            setLongPending(true);
        }, 500);
    }, [currentPage, itemPrePage]);

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };

    const renderCardPlaceholder = () => (
        <Card>
            <CardContent>
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <Skeleton variant="text" height={30} width={120} />
                    <Skeleton variant="rectangular" height={100} />
                    <Box sx={{ display: "flex", flexDirection: "row", mt: 1 }}>
                        <Skeleton variant="circular" height={30} width={30} />
                        <Skeleton
                            variant="text"
                            height={30}
                            width={60}
                            sx={{ ml: 1 }}
                        />
                    </Box>
                </Box>
            </CardContent>
        </Card>
    );
    const renderUserList = () => (
        <>
            {userList!.map((user: UserBasicInfo, index: number) => (
                <Card key={index}>
                    {/*<CardActionArea*/}
                    {/*    component={RouterLink}*/}
                    {/*    to={`/admins/orders/${user.id}`}*/}
                    {/*>*/}
                    <CardContent>
                        <Box
                            sx={{
                                display: "flex",
                                justifyContent: "space-between",
                                p: 1,
                                m: 1,
                                flexDirection: "row",
                            }}
                        >
                            <Box
                                sx={{
                                    display: "flex",
                                    flexDirection: "column",
                                }}
                            >
                                <Box
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                    }}
                                >
                                    <Typography
                                        variant="h6"
                                        noWrap
                                        style={{ fontWeight: 600 }}
                                    >
                                        {user.username}
                                    </Typography>
                                    <Box sx={{ flexGrow: 1 }} />
                                </Box>
                                <Box
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                    }}
                                >
                                    <Typography
                                        variant="subtitle1"
                                        sx={{ ml: 1 }}
                                    >
                                        个人介绍：
                                        {
                                            user.description.split(
                                                "EwbkK8TU",
                                                2
                                            )[0]
                                        }
                                    </Typography>
                                </Box>
                                <Box
                                    sx={{
                                        display: "flex",
                                        flexDirection: "row",
                                    }}
                                >
                                    <Typography
                                        variant="subtitle1"
                                        sx={{ ml: 1 }}
                                    >
                                        专业领域：
                                        {
                                            user.description.split(
                                                "EwbkK8TU",
                                                2
                                            )[1]
                                        }
                                    </Typography>
                                </Box>
                            </Box>
                            {manager?.role === ManagerRole.ADMIN ? (
                                <></>
                            ) : (
                                <Stack direction="row" p={3} spacing={2}>
                                    <Button
                                        size="small"
                                        color="error"
                                        variant="outlined"
                                        onClick={() => {
                                            userService.reviewUserToAnswer(
                                                user.id,
                                                false
                                            );
                                            window.location.reload();
                                        }}
                                    >
                                        驳回
                                    </Button>
                                    <Button
                                        size="small"
                                        color="success"
                                        variant="outlined"
                                        onClick={() => {
                                            userService.reviewUserToAnswer(
                                                user.id,
                                                true
                                            );
                                            window.location.reload();
                                        }}
                                    >
                                        通过
                                    </Button>
                                </Stack>
                            )}
                        </Box>
                    </CardContent>
                    {/*</CardActionArea>*/}
                </Card>
            ))}
            {maxPage > 1 && (
                <Pagination
                    currentPage={currentPage}
                    maxPage={maxPage}
                    totalCount={totalCount}
                    itemPrePage={itemPrePage}
                    onPageChanged={onPageChanged}
                />
            )}
        </>
    );
    if (longPending && userList == null) {
        return (
            <Stack spacing={2} mt={4}>
                {renderCardPlaceholder()}
            </Stack>
        );
    }
    if (userList && totalCount === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                没有申请者
            </Typography>
        );
    }
    return (
        <>
            <Box marginTop={1.4}>
                {userList && <Stack spacing={2}>{renderUserList()}</Stack>}
            </Box>
        </>
    );
};

export default ReviewUserList;
