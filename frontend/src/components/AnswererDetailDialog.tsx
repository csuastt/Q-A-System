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

const AnswererDetailDialog: React.FC<DialogProps & { info: UserBasicInfo }> = (
    props
) => {
    const { info } = props;
    return (
        <Dialog {...props}>
            <DialogTitle>回答者信息</DialogTitle>
            <DialogContent>
                <Card elevation={0}>
                    <CardHeader
                        avatar={
                            <Avatar alt={info.username} src={info.avatar} />
                        }
                        title={info.nickname ? info.nickname : info.username}
                        subheader={info.nickname && "@" + info.username}
                    />
                    <CardContent>
                        <TextField
                            label="个人介绍"
                            contentEditable={false}
                            value={
                                info.description
                                    ? info.description
                                    : "该回答者很懒～还没有填写个人介绍哦～"
                            }
                            fullWidth
                        />
                    </CardContent>
                </Card>
            </DialogContent>
        </Dialog>
    );
};

export default AnswererDetailDialog;
