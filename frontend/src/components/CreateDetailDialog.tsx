import Dialog, { DialogProps } from "@mui/material/Dialog";
import { UserBasicInfo } from "../services/definations";
import React from "react";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import Card from "@mui/material/Card";
import CardHeader from "@mui/material/CardHeader";
import Avatar from "@mui/material/Avatar";
import TextField from "@mui/material/TextField";
import CardContent from "@mui/material/CardContent";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";

const AnswererDetailDialog: React.FC<
    DialogProps & { username: String; password: string }
> = (props) => {
    const { username, password } = props;
    let description = null;
    let profession = null;

    return (
        <Dialog {...props}>
            <DialogTitle>回答者信息</DialogTitle>
            <DialogContent>
                <Card elevation={0}>
                    <CardHeader
                        avatar={
                            <Avatar sx={{ m: 1, bgcolor: "primary.main" }}>
                                <AccountCircleIcon />
                            </Avatar>
                        }
                        title={username}
                    />
                    <CardContent>
                        <TextField
                            label="初始密码"
                            contentEditable={false}
                            value={password}
                            fullWidth
                        />
                    </CardContent>
                </Card>
            </DialogContent>
        </Dialog>
    );
};

export default AnswererDetailDialog;
