import MuiPagination from "@mui/material/Pagination";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import React, { useState } from "react";
import Button from "@mui/material/Button";
import KeyboardReturnIcon from "@mui/icons-material/KeyboardReturn";
import { PagedList } from "../services/definations";

export interface PaginationProp {
    currentPage: number;
    maxPage: number;
    totalCount: number;
    itemPrePage: number;
    onPageChanged: (newPage: number) => void;
}

export interface PaginationInfo {
    currentPage: number;
    setCurrentPage: (cp: number) => void;
    maxPage: number;
    setMaxPage: (mp: number) => void;
    totalCount: number;
    setTotalCount: (tc: number) => void;
    itemPrePage: number;
    setItemPrePage: (pre: number) => void;
    applyPagedList: (pl: PagedList<any>) => void;
}

export function usePagination(
    cp: number = 1,
    pre: number = 20
): PaginationInfo {
    const [currentPage, setCurrentPage] = useState(cp);
    const [maxPage, setMaxPage] = useState(cp);
    const [totalCount, setTotalCount] = useState(0);
    const [itemPrePage, setItemPrePage] = useState(pre);

    const applyPagedList = (pl: PagedList<any>) => {
        setCurrentPage(pl.page);
        setMaxPage(pl.totalPages);
        setTotalCount(pl.totalCount);
        setItemPrePage(pl.pageSize);
    };

    return {
        currentPage,
        setCurrentPage,
        maxPage,
        setMaxPage,
        totalCount,
        setTotalCount,
        itemPrePage,
        setItemPrePage,
        applyPagedList,
    };
}

const Pagination: React.FC<PaginationProp> = (props) => {
    const [gotoFieldText, setGotoFieldText] = useState(
        props.currentPage.toString()
    );
    const onGotoFieldChanged = (event: React.ChangeEvent<HTMLInputElement>) => {
        setGotoFieldText(event.target.value);
    };
    const gotoPage = () => {
        const newPage = parseInt(gotoFieldText);
        if (
            0 < newPage &&
            newPage <= props.maxPage &&
            newPage !== props.currentPage
        ) {
            props.onPageChanged(newPage);
        } else {
            setGotoFieldText(props.currentPage.toString());
        }
    };
    const checkEnter = (event: React.KeyboardEvent<HTMLInputElement>) => {
        if (event.key === "Enter") {
            gotoPage();
        }
    };

    return (
        <Stack direction="row" spacing={2}>
            <MuiPagination
                count={props.maxPage}
                page={props.currentPage}
                onChange={(_, value) => props.onPageChanged(value)}
                showFirstButton
                showLastButton
            />
            <TextField
                value={gotoFieldText}
                onChange={onGotoFieldChanged}
                inputProps={{ inputMode: "numeric", pattern: "[0-9]*" }}
                onKeyDown={checkEnter}
            />
            <Button
                endIcon={<KeyboardReturnIcon />}
                onClick={gotoPage}
                variant="outlined"
                size="small"
            >
                跳转
            </Button>
        </Stack>
    );
};

export default Pagination;
