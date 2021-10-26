// mui
import Avatar from "@mui/material/Avatar";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import Card from "@mui/material/Card";
import CardActions from "@mui/material/CardActions";
import Divider from "@mui/material/Divider";
import CardContent from "@mui/material/CardContent";
import React, { Component } from "react";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import TextField from "@mui/material/TextField";
import InputAdornment from "@mui/material/InputAdornment";
import { validate_required } from "./Login";
import userService from "../services/userService";
import UserContext from "../UserContext";
import { UserRole } from "../services/definations";

interface AccountBriefProfileProps {
    id: number | undefined;
    avatar: string | undefined;
    nickname: string | undefined;
    username: string | undefined;
    role: UserRole | undefined;
    alertHandler: (
        arg1: "success" | "info" | "warning" | "error",
        arg2: string
    ) => void;
    redirectHandler: (arg1: string) => void;
    minPrice: number;
    maxPrice: number;
}

interface AccountBriefProfileState {
    openApplyDialog: boolean;
    openPriceDialog: boolean;
    description: string;
    price: number;
    error_msg: string;
    error_msg_2: string;
}

export default class AccountBriefProfile extends Component<
    AccountBriefProfileProps,
    AccountBriefProfileState
> {
    constructor(props: any) {
        super(props);

        this.state = {
            openApplyDialog: false,
            openPriceDialog: false,
            description: "",
            price: 50,
            error_msg: "",
            error_msg_2: "",
        };
        this.handleCloseApplyDialog = this.handleCloseApplyDialog.bind(this);
        this.handleOpenApplyDialog = this.handleOpenApplyDialog.bind(this);
        this.handleClosePriceDialog = this.handleClosePriceDialog.bind(this);
        this.handleOpenPriceDialog = this.handleOpenPriceDialog.bind(this);
        this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
        this.handlePriceChange = this.handlePriceChange.bind(this);
        this.handleSubmitApply = this.handleSubmitApply.bind(this);
        this.handleSubmitPrice = this.handleSubmitPrice.bind(this);
    }

    componentDidMount() {
        const currentUser = this.context.user;
        if (this.context.user) {
            this.setState({
                price: currentUser.price,
            });
        }
    }

    handleCloseApplyDialog() {
        this.setState({ openApplyDialog: false });
    }

    handleOpenApplyDialog() {
        this.setState({ openApplyDialog: true });
    }

    handleClosePriceDialog() {
        this.setState({ openPriceDialog: false });
    }

    handleOpenPriceDialog() {
        this.setState({ openPriceDialog: true });
    }

    handleDescriptionChange(e: any) {
        let error = validate_required(e.target.value);
        const nextState = {};
        // @ts-ignore
        nextState[e.target.name] = e.target.value;
        // @ts-ignore
        nextState["error_msg"] = error;
        this.setState(nextState);
        return error === "";
    }

    handlePriceChange(e: any) {
        let error = validate_required(e.target.value);
        let value = e.target.value;
        if (error) {
            this.setState({
                error_msg_2: "价格为空或格式错误",
                price: value,
            });
            return false;
        }
        this.setState({ error_msg_2: "" });
        if (value < this.props.minPrice) value = this.props.minPrice;
        else if (value > this.props.maxPrice) value = this.props.maxPrice;
        this.setState({
            price: value,
        });
        return true;
    }

    handleSubmitApply() {
        if (
            this.handleDescriptionChange({
                target: { value: this.state.description },
            }) &&
            this.handlePriceChange({
                target: { value: this.state.price },
            })
        ) {
            if (typeof this.props.id !== "undefined") {
                userService
                    .applyAnswerer(
                        this.props.id,
                        this.state.description,
                        this.state.price
                    )
                    .then(
                        () => {
                            // apply success
                            this.props.alertHandler("success", "提交成功");
                        },
                        (error) => {
                            // show the error message
                            if (error.response.status === 403) {
                                this.props.alertHandler(
                                    "error",
                                    "服务器验证异常"
                                );
                            } else {
                                this.props.alertHandler("error", "网络错误");
                            }
                        }
                    );
            }
            this.handleCloseApplyDialog();
        }
    }

    handleSubmitPrice() {
        if (
            this.handlePriceChange({
                target: { value: this.state.price },
            })
        ) {
            if (typeof this.props.id !== "undefined") {
                userService.modifyPrice(this.props.id, this.state.price).then(
                    () => {
                        // apply success
                        this.props.alertHandler("success", "提交成功");
                    },
                    (error) => {
                        // show the error message
                        if (error.response.status === 403) {
                            if (
                                error.response.data.message === "PRICE_INVALID"
                            ) {
                                this.props.alertHandler("error", "价格格式错误");
                            } else {
                                this.props.alertHandler("error", "服务器验证异常");
                            }
                        }
                        else if (error.response.status === 404) {
                            this.props.alertHandler("error", "用户不存在");
                        }
                        else{
                            this.props.alertHandler("error", "网络错误");
                        }
                    }
                );
            }
            this.handleClosePriceDialog();
        }
    }

    render() {
        return (
            <>
                <Card>
                    <CardContent>
                        <Box
                            sx={{
                                alignItems: "center",
                                display: "flex",
                                flexDirection: "column",
                            }}
                        >
                            <Avatar
                                src={this.props.avatar}
                                sx={{
                                    height: 100,
                                    width: 100,
                                }}
                            />
                            <Box mt={2}>
                                <Typography
                                    color="textPrimary"
                                    gutterBottom
                                    variant="h4"
                                >
                                    {this.props.nickname === ""
                                        ? "匿名用户"
                                        : this.props.nickname}
                                </Typography>
                            </Box>
                            <Box mx={2}>
                                <Typography
                                    color="textSecondary"
                                    variant="body1"
                                >
                                    {this.props.role === UserRole.USER
                                        ? "你还不是问答者，快去申请吧~"
                                        : "你已经是问答者了，快去回答问题吧~"}
                                </Typography>
                            </Box>
                        </Box>
                    </CardContent>
                    <Divider />
                    <CardActions>
                        {this.props.role === UserRole.USER ? (
                            <Button
                                color="primary"
                                fullWidth
                                variant="text"
                                onClick={this.handleOpenApplyDialog}
                            >
                                问答者申请
                            </Button>
                        ) : (
                            <Button
                                color="primary"
                                fullWidth
                                variant="text"
                                onClick={this.handleOpenPriceDialog}
                            >
                                修改定价
                            </Button>
                        )}
                    </CardActions>
                </Card>
                <Dialog
                    fullWidth
                    open={this.state.openApplyDialog}
                    onClose={this.handleCloseApplyDialog}
                >
                    <DialogTitle>申请成为问答者</DialogTitle>
                    <DialogContent>
                        <DialogContentText mb={3}>
                            在申请成为问答者前，请重新填写您的个人介绍以及设置定价。
                            个人介绍是审核的重要根据，并且在提交成功后
                            <Box component="span" fontWeight="fontWeightBold">
                                不可修改
                            </Box>
                            。
                            优秀的、有展示性的个人介绍能帮助您获得更多提问者的青睐。
                        </DialogContentText>
                        <TextField
                            fullWidth
                            autoFocus
                            label="个人介绍"
                            name="description"
                            multiline
                            onChange={this.handleDescriptionChange}
                            rows={4}
                            value={this.state.description}
                            error={this.state.error_msg.length !== 0}
                            helperText={this.state.error_msg}
                            placeholder="快来介绍一下你自己吧~"
                            variant="outlined"
                        />
                        <DialogContentText mt={3} mb={3}>
                            在当前机制下， 回答定价最高不能超过
                            <Box component="span" fontWeight="fontWeightBold">
                                {this.props.maxPrice}
                            </Box>
                            ￥/次， 最低不能低于
                            <Box component="span" fontWeight="fontWeightBold">
                                {this.props.minPrice}
                            </Box>
                            ￥/次。
                        </DialogContentText>
                        <TextField
                            fullWidth
                            label="回答定价"
                            name="price"
                            onChange={this.handlePriceChange}
                            type="number"
                            InputProps={{
                                inputProps: {
                                    min: this.props.minPrice,
                                    max: this.props.maxPrice,
                                },
                                startAdornment: (
                                    <InputAdornment position="start">
                                        ￥/次
                                    </InputAdornment>
                                ),
                            }}
                            value={this.state.price}
                            error={this.state.error_msg_2.length !== 0}
                            helperText={this.state.error_msg_2}
                            variant="outlined"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={this.handleCloseApplyDialog}
                            color="error"
                        >
                            取消
                        </Button>
                        <Button onClick={this.handleSubmitApply}>提交</Button>
                    </DialogActions>
                </Dialog>
                <Dialog
                    maxWidth={"xs"}
                    open={this.state.openPriceDialog}
                    onClose={this.handleClosePriceDialog}
                >
                    <DialogTitle>修改回答定价</DialogTitle>
                    <DialogContent>
                        <DialogContentText mb={3}>
                            您可以在任何时候修改您的回答定价。 在当前机制下，
                            回答定价最高不能超过
                            <Box component="span" fontWeight="fontWeightBold">
                                {this.props.maxPrice}
                            </Box>
                            ￥/次， 最低不能低于
                            <Box component="span" fontWeight="fontWeightBold">
                                {this.props.minPrice}
                            </Box>
                            ￥/次。
                        </DialogContentText>
                        <TextField
                            fullWidth
                            label="回答定价"
                            name="price"
                            onChange={this.handlePriceChange}
                            type="number"
                            InputProps={{
                                inputProps: {
                                    min: this.props.minPrice,
                                    max: this.props.maxPrice,
                                },
                                startAdornment: (
                                    <InputAdornment position="start">
                                        ￥/次
                                    </InputAdornment>
                                ),
                            }}
                            value={this.state.price}
                            error={this.state.error_msg_2.length !== 0}
                            helperText={this.state.error_msg_2}
                            variant="outlined"
                        />
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={this.handleClosePriceDialog}
                            color="error"
                        >
                            取消
                        </Button>
                        <Button onClick={this.handleSubmitPrice}>提交</Button>
                    </DialogActions>
                </Dialog>
            </>
        );
    }
}

AccountBriefProfile.contextType = UserContext;
