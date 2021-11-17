import React, { useEffect, useMemo, useState } from "react";
import TextField from '@mui/material/TextField';
import {parseIntWithDefault, useQuery} from "../util";
import OrderList from "./OrderList";
import {Box, Grid, InputAdornment} from "@mui/material";
import Button from "@mui/material/Button";
import SearchIcon from '@mui/icons-material/Search';


const Library = () => {
    const [keywords, setKeywords] = useState<string>("");
    const query = useQuery();
    const currentPage = parseIntWithDefault(query.get("page"), 1);
    const itemPrePage = parseIntWithDefault(query.get("prepage"), 10);

    const PublicOrderListWrapper: React.FC<{ keywords: string }> =
        (props) => (
        <OrderList
            keywords={keywords}
            itemPrePage={itemPrePage}
            initCurrentPage={currentPage}
        />
    );

    const handleSubmitSearch = (event: any) => {
        console.log(event);
    }

    return (
        <Box mt={3}>
                <form
                    onSubmit={(e) => {
                        e.preventDefault();
                        // @ts-ignore
                        setKeywords(e.target[0].value);
                    } }
                >
                    <Grid
                        container
                        spacing={3}
                        direction="row"
                        alignItems="center"
                        justifyContent="center"
                    >
                        <Grid item xs={8}>
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
                        <Grid item xs={2}>
                            <Button
                                color="primary"
                                variant="contained"
                                type="submit"
                                size={"large"}
                            >
                                搜一搜
                            </Button>
                        </Grid>
                    </Grid>
                </form>
            <Box mt={3}>
                <PublicOrderListWrapper keywords={keywords}/>
            </Box>
        </Box>
    );

};

export default Library;
