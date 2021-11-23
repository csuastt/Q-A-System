import React, { useState } from "react";
import TextField from "@mui/material/TextField";
import { parseIntWithDefault, useQuery } from "../util";
import OrderList from "./OrderList";
import {
    Box,
    FormControl,
    Grid,
    InputAdornment,
    InputLabel,
    MenuItem,
    Select, ToggleButton,
    Typography,
} from "@mui/material";
import Button from "@mui/material/Button";
import SearchIcon from "@mui/icons-material/Search";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import TrendingDownIcon from "@mui/icons-material/TrendingDown";
import Stack from "@mui/material/Stack";
import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";

const Library: React.FC<{
    briefMsg?: boolean;
}> = (props) => {
    const [keywords, setKeywords] = useState<string>("");
    const [millis, setMillis] = useState<number>(-1);
    const [count, setCount] = useState<number>(-1);
    const query = useQuery();
    const [currentPage, setCurrentPage] = useState<number>(1);
    const [filterPurchased, setFilterPurchased] = useState(false);
    const itemPrePage = parseIntWithDefault(
        query.get("prepage"),
        props.briefMsg ? 5 : 10
    );
    const theme = useTheme();
    const matches = useMediaQuery(theme.breakpoints.up("md"));
    const [sortProperty, setSortProperty] = useState("createTime");
    const [sortOrder, setSortOrder] = useState("DESC");

    const onFilterButtonChanged = (
        event: React.MouseEvent<HTMLElement>,
        value: boolean
    ) => setFilterPurchased(value);

    const PublicOrderListWrapper: React.FC<{
        keywords: string;
        listMode?: boolean;
    }> = (wrapperProps) => (
        <OrderList
            keywords={keywords}
            setMillis={setMillis}
            setCount={setCount}
            itemPrePage={itemPrePage}
            setCurrentPage={setCurrentPage}
            initCurrentPage={currentPage}
            listMode={wrapperProps.listMode}
            initSortOrder={sortOrder}
            initSortProperty={sortProperty}
            purchased={filterPurchased ? filterPurchased : undefined}
        />
    );

    return props.briefMsg ? (
        <>
            <PublicOrderListWrapper keywords={""} listMode={true} />
        </>
    ) : (
        <Box mt={3}>
            <Grid container justifyContent="center" mb={3}>
                <Box
                    component="img"
                    sx={{
                        maxHeight: { xs: 60, md: 90 },
                    }}
                    alt="问客"
                    src={
                        theme.palette.mode === "dark"
                            ? "/static/images/logo_dark.png"
                            : "/static/images/logo_light.png"
                    }
                />
            </Grid>
            <form
                onSubmit={(e) => {
                    e.preventDefault();
                    // @ts-ignore
                    setKeywords(e.target[0].value);
                }}
            >
                <Grid
                    container
                    spacing={3}
                    direction="row"
                    alignItems="center"
                    justifyContent="center"
                >
                    <Grid item xs={7} md={8}>
                        <TextField
                            label="搜索问答库"
                            fullWidth
                            type={"search"}
                            inputProps={{
                                maxLength: 100,
                            }}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon />
                                    </InputAdornment>
                                ),
                            }}
                        />
                    </Grid>
                    <Grid item xs={3} md={2}>
                        <Button
                            color="primary"
                            variant="contained"
                            type="submit"
                            size={matches ? "large" : "medium"}
                        >
                            搜一搜
                        </Button>
                    </Grid>
                    <Grid item xs={10} md={10} mt={-1}>
                        <Typography color={"textSecondary"}>
                            {millis !== -1 && count !== -1
                                ? "问客为您找到相关结果" +
                                  count.toString() +
                                  "个，耗时" +
                                  millis.toString() +
                                  "毫秒"
                                : ""}
                        </Typography>
                    </Grid>
                </Grid>
            </form>
            <Box mt={2}>
                <Stack
                    direction="row"
                    justifyContent="flex-start"
                    alignItems="center"
                    spacing={2}
                    mb={2}
                    mt={3}
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
                            <MenuItem value={"createTime"}>创建时间</MenuItem>
                            <MenuItem value={"expireTime"}>超时时间</MenuItem>
                            <MenuItem value={"price"}>成交价格</MenuItem>
                            <MenuItem value={"messageCount"}>聊天条数</MenuItem>
                            <MenuItem value={"rating"}>订单评分</MenuItem>
                            <MenuItem value={"state"}>订单状态</MenuItem>
                            <MenuItem value={"publicPrice"}>分享价格</MenuItem>
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
                    <ToggleButtonGroup
                        value={filterPurchased}
                        exclusive
                        onChange={onFilterButtonChanged}
                        size="small"
                    >
                        <ToggleButton value={true}>筛选已购买</ToggleButton>
                        <ToggleButton value={false}>显示全部</ToggleButton>
                    </ToggleButtonGroup>
                </Stack>
                <PublicOrderListWrapper keywords={keywords} />
            </Box>
        </Box>
    );
};

export default Library;
