import React, { useState } from "react";
import TextField from "@mui/material/TextField";
import { parseIntWithDefault, useQuery } from "../util";
import OrderList from "./OrderList";
import { Box, Grid, InputAdornment, Typography } from "@mui/material";
import Button from "@mui/material/Button";
import SearchIcon from "@mui/icons-material/Search";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";

const Library: React.FC<{
    briefMsg?: boolean;
}> = (props) => {
    const [keywords, setKeywords] = useState<string>("");
    const [millis, setMillis] = useState<number>(-1);
    const [count, setCount] = useState<number>(-1);
    const query = useQuery();
    const currentPage = parseIntWithDefault(query.get("page"), 1);
    const itemPrePage = parseIntWithDefault(
        query.get("prepage"),
        props.briefMsg ? 5 : 10
    );
    const theme = useTheme();
    const matches = useMediaQuery(theme.breakpoints.up("md"));

    const PublicOrderListWrapper: React.FC<{
        keywords: string;
        listMode?: boolean;
    }> = (wrapperProps) => (
        <OrderList
            keywords={keywords}
            setMillis={setMillis}
            setCount={setCount}
            itemPrePage={itemPrePage}
            initCurrentPage={currentPage}
            listMode={wrapperProps.listMode}
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
            <Box mt={5}>
                <PublicOrderListWrapper keywords={keywords} />
            </Box>
        </Box>
    );
};

export default Library;
