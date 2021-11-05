import React, { useEffect, useState } from "react";
import { UserBasicInfo, UserInfo, UserRole } from "../services/definations";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemText from "@mui/material/ListItemText";
import ListItemAvatar from "@mui/material/ListItemAvatar";
import Avatar from "@mui/material/Avatar";
import Skeleton from "@mui/material/Skeleton";
import _ from "lodash";
import { Divider } from "@mui/material";
import { parseIntWithDefault, useQuery } from "../util";
import userService from "../services/userService";
import Box from "@mui/material/Box";
import Grid from "@mui/material/Grid";
import AnswererCard from "./AnswererCard";
import Typography from "@mui/material/Typography";
import Pagination from "./Pagination";

const UserList: React.FC<{ selectModel?: boolean; userRole: UserRole }> = (
    props
) => {
    const query = useQuery();
    const [userList, setUserList] = useState<Array<UserBasicInfo>>();
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
                setUserList(list.data);
                setMaxPage(list.totalPages);
                setTotalCount(list.totalCount);
            });
    }, [currentPage, itemPrePage, props.userRole]);

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };
    if (userList == null) {
        return (
            <ListItem alignItems="flex-start">
                <ListItemAvatar>
                    <Skeleton variant="circular">
                        <Avatar />
                    </Skeleton>
                </ListItemAvatar>
                <ListItemText primary={<Skeleton />} secondary={<Skeleton />} />
            </ListItem>
        );
    }

    const renderPlaceholder = () => (
        <ListItem alignItems="flex-start">
            <ListItemAvatar>
                <Skeleton variant="circular">
                    <Avatar />
                </Skeleton>
            </ListItemAvatar>
            <ListItemText primary={<Skeleton />} secondary={<Skeleton />} />
        </ListItem>
    );
    if (totalCount === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                用户数量为0
            </Typography>
        );
    }

    return (
        <Box sx={{ pt: 3 }} mt={1}>
            <Grid>
                {userList.map((user: UserBasicInfo, index: number) => (
                    <ListItem alignItems="flex-start" key={index}>
                        <ListItemAvatar>
                            <Avatar alt={user.username} src={user.avatar} />
                        </ListItemAvatar>
                        <ListItemText
                            primary={user.username}
                            secondary={user.description}
                        />
                    </ListItem>
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

export default UserList;
