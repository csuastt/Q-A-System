import MuiPagination from "@mui/material/Pagination";
import Stack from "@mui/material/Stack";
import TextField from "@mui/material/TextField";
import React, { useEffect, useState } from "react";
import Button from "@mui/material/Button";
import KeyboardReturnIcon from "@mui/icons-material/KeyboardReturn";
import { useTheme } from "@mui/material/styles";
import useMediaQuery from "@mui/material/useMediaQuery";

export interface PaginationProp {
    currentPage: number;
    maxPage: number;
    totalCount: number;
    itemPrePage: number;
    onPageChanged: (newPage: number) => void;
}

const Pagination: React.FC<PaginationProp> = (props) => {
    const [gotoFieldText, setGotoFieldText] = useState<string>("");
    // if match the mobile size
    const theme = useTheme();
    const matches = useMediaQuery(theme.breakpoints.up("md"));

    useEffect(() => {
        setGotoFieldText(props.currentPage.toString());
    }, [props.currentPage]);

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
        <Stack
            direction="row"
            spacing={2}
            sx={{ justifyContent: "center" }}
            mt={matches ? 0 : 2}
        >
            {matches ? (
                <MuiPagination
                    count={props.maxPage}
                    page={props.currentPage}
                    onChange={(_, value) => props.onPageChanged(value)}
                    showFirstButton
                    showLastButton
                    sx={{ my: "auto" }}
                />
            ) : (
                <MuiPagination
                    count={props.maxPage}
                    page={props.currentPage}
                    onChange={(_, value) => props.onPageChanged(value)}
                    showFirstButton
                    showLastButton
                    siblingCount={0}
                    boundaryCount={0}
                    size="large"
                    sx={{ my: "auto" }}
                />
            )}
            {matches && (
                <>
                    <TextField
                        value={gotoFieldText}
                        onChange={onGotoFieldChanged}
                        inputProps={{ inputMode: "numeric", pattern: "[0-9]*" }}
                        onKeyDown={checkEnter}
                        size="small"
                        sx={{ width: "6ch" }}
                    />
                    <Button
                        endIcon={<KeyboardReturnIcon />}
                        onClick={gotoPage}
                        variant="outlined"
                        size="small"
                    >
                        跳转
                    </Button>
                </>
            )}
        </Stack>
    );
};

export default Pagination;
