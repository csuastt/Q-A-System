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
import userService from "../services/userService";

const AnswererDetailDialog: React.FC<DialogProps & { info: UserBasicInfo }> = (
    props
) => {
    const { info } = props;
    let description = null;
    let profession = null;
    if (info.description) {
        let arr = info.description.split("EwbkK8TU", 2);
        description = arr[0];
        profession = arr.length > 1 ? arr[1] : "";
    }
    return (
        <Dialog {...props}>
            <DialogTitle>回答者信息</DialogTitle>
            <DialogContent>
                <Card elevation={0}>
                    <CardHeader
                        avatar={
                            <Avatar
                                alt={info.username}
                                src={userService.getAvatarUrl(info.id)}
                            />
                        }
                        title={info.nickname ? info.nickname : info.username}
                        subheader={info.nickname && "@" + info.username}
                    />
                    <CardContent>
                        <TextField
                            label="专业领域"
                            contentEditable={false}
                            value={
                                profession !== null
                                    ? profession
                                    : "该回答者很懒～还没有填写专业领域哦～"
                            }
                            fullWidth
                        />
                        <TextField
                            label="个人介绍"
                            contentEditable={false}
                            value={
                                description !== null
                                    ? description
                                    : "该回答者很懒～还没有填写个人介绍哦～"
                            }
                            fullWidth
                            multiline
                            rows={4}
                            sx={{ marginTop: 4 }}
                        />
                    </CardContent>
                </Card>
            </DialogContent>
        </Dialog>
    );
};

export default AnswererDetailDialog;
