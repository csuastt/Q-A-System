import Dialog, { DialogProps } from "@mui/material/Dialog";
import React from "react";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import TextField from "@mui/material/TextField";
import CardContent from "@mui/material/CardContent";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import Button from "@mui/material/Button";
import { Stack } from "@mui/material";
import copy from "copy-to-clipboard";
import { ManagerRole } from "../services/definations";

const AnswererDetailDialog: React.FC<
    DialogProps & { username: String; password: string; role: ManagerRole }
> = (props) => {
    const { username, password } = props;

    return (
        <Dialog {...props}>
            <DialogTitle>新建管理员信息</DialogTitle>
            <DialogContent>
                <Card elevation={0}>
                    {props.role === ManagerRole.ADMIN ? (
                        <CardHeader
                            avatar={
                                <Avatar sx={{ m: 1, bgcolor: "primary.main" }}>
                                    <AccountCircleIcon />
                                </Avatar>
                            }
                            title={username}
                            subheader={"管理员"}
                        />
                    ) : (
                        <CardHeader
                            avatar={
                                <Avatar sx={{ m: 1, bgcolor: "primary.main" }}>
                                    <AccountCircleIcon />
                                </Avatar>
                            }
                            title={username}
                            subheader={"审核员"}
                        />
                    )}

                    <CardContent>
                        <TextField
                            label="初始密码"
                            contentEditable={false}
                            value={password}
                            fullWidth
                        />
                    </CardContent>
                </Card>
                <Stack
                    direction="row"
                    justifyContent="flex-end"
                    p={1}
                    spacing={1}
                >
                    <Button
                        color="primary"
                        size="medium"
                        variant="outlined"
                        sx={{ ml: 2 }}
                        onClick={() => {
                            copy(password);
                        }}
                    >
                        复制
                    </Button>
                </Stack>
            </DialogContent>
        </Dialog>
    );
};

export default AnswererDetailDialog;
