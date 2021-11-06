import React, { useEffect, useState } from "react";
import { ManagerInfo, ManagerRole } from "../services/definations";
import Avatar from "@mui/material/Avatar";
import Skeleton from "@mui/material/Skeleton";
import { Stack } from "@mui/material";
import { parseIntWithDefault, useQuery } from "../util";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Pagination from "./Pagination";
import Card from "@mui/material/Card";
import CardActionArea from "@mui/material/CardActionArea";
import CardContent from "@mui/material/CardContent";
import managerService from "../services/managerService";
import HowToRegIcon from "@mui/icons-material/HowToReg";
import PersonIcon from "@mui/icons-material/Person";

const ManagerList: React.FC<{ selectModel?: boolean }> = (props) => {
    const query = useQuery();
    const [managerList, setManagerList] = useState<Array<ManagerInfo>>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const [itemPrePage] = useState(
        parseIntWithDefault(query.get("prepage"), 10)
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [totalCount, setTotalCount] = useState(0);
    useEffect(() => {
        managerService
            .getAllManagerList(currentPage, itemPrePage)
            .then((list) => {
                setManagerList(list.data);
                setMaxPage(list.totalPages);
                setTotalCount(list.totalCount);
            });
    }, [currentPage, itemPrePage]);

    const onPageChanged = (newPage: number) => {
        setCurrentPage(newPage);
    };

    const renderCardPlaceholder = () => (
        <Card>
            <CardContent>
                <Box sx={{ display: "flex", flexDirection: "column" }}>
                    <Skeleton variant="text" height={30} width={120} />
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

    const renderPlaceholder = () => (
        <>
            {renderCardPlaceholder()}
            {renderCardPlaceholder()}
        </>
    );

    if (managerList == null) {
        return (
            <Stack spacing={2} mt={4}>
                {renderPlaceholder()}
            </Stack>
        );
    }
    if (totalCount === 0) {
        return (
            <Typography variant="h3" textAlign="center" sx={{ mt: 3 }}>
                管理员数量为0
            </Typography>
        );
    }

    return (
        <Stack spacing={2}>
            {managerList.map((manager: ManagerInfo, index: number) => (
                <Card key={index}>
                    <CardActionArea
                    // component={RouterLink}
                    // to={`/users/${user.id}`}
                    >
                        <CardContent>
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
                                    mt={1}
                                >
                                    <Avatar
                                        sx={{
                                            width: 30,
                                            height: 30,
                                            m: 1,
                                            bgcolor: "primary.main",
                                        }}
                                    >
                                        {manager.role ===
                                        ManagerRole.REVIEWER ? (
                                            <PersonIcon />
                                        ) : (
                                            <HowToRegIcon />
                                        )}
                                    </Avatar>
                                    <Typography
                                        variant="subtitle1"
                                        sx={{ ml: 1 }}
                                    >
                                        {manager.username}
                                    </Typography>
                                </Box>
                                <Typography variant="caption" mb={-1} mt={1}>
                                    管理员id：
                                    {manager.id}
                                </Typography>
                            </Box>
                        </CardContent>
                    </CardActionArea>
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
        </Stack>
    );
};

export default ManagerList;
