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
import UserContext from "../AuthContext";
import { ConfigInfo, UserRole } from "../services/definations";
import { renderAnswerHelp } from "./Help";
import {styled} from "@mui/material/styles";
import {IconButton} from "@mui/material";

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
    config: ConfigInfo;
    fetchUserInfo: () => void;
}

interface AccountBriefProfileState {
    openApplyDialog: boolean;
    openPriceDialog: boolean;
    openTipsDialog: boolean;
    description: string;
    profession: string;
    price: number;
    error_msg_description: string;
    error_msg_price: string;
    error_msg_profession: string;
}

// an Input without display
const Input = styled('input')({
    display: 'none',
});

export default class AccountBriefProfile extends Component<
    AccountBriefProfileProps,
    AccountBriefProfileState
> {
    constructor(props: any) {
        super(props);

        this.state = {
            openApplyDialog: false,
            openPriceDialog: false,
            openTipsDialog: false,
            description: "",
            profession: "",
            price: 50,
            error_msg_description: "",
            error_msg_price: "",
            error_msg_profession: "",
        };
        this.handleCloseApplyDialog = this.handleCloseApplyDialog.bind(this);
        this.handleOpenTipsDialog = this.handleOpenTipsDialog.bind(this);
        this.handleCloseTipsDialog = this.handleCloseTipsDialog.bind(this);
        this.handleOpenApplyDialog = this.handleOpenApplyDialog.bind(this);
        this.handleClosePriceDialog = this.handleClosePriceDialog.bind(this);
        this.handleOpenPriceDialog = this.handleOpenPriceDialog.bind(this);
        this.handleChange = this.handleChange.bind(this);
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

    handleOpenTipsDialog() {
        this.setState({ openTipsDialog: true });
    }

    handleCloseTipsDialog() {
        this.setState({ openTipsDialog: false });
    }

    handleChange(e: any) {
        let error = validate_required(e.target.value);
        const nextState = {};
        // @ts-ignore
        nextState[e.target.name] = e.target.value;
        // @ts-ignore
        nextState["error_msg_" + e.target.name] = error;
        this.setState(nextState);
        return error === "";
    }

    handlePriceChange(e: any) {
        let error = validate_required(e.target.value);
        let value = e.target.value;
        if (error) {
            this.setState({
                error_msg_price: "价格为空或格式错误",
                price: value,
            });
            return false;
        }
        this.setState({ error_msg_price: "" });
        if (value < this.props.config.minPrice)
            value = this.props.config.minPrice;
        else if (value > this.props.config.maxPrice)
            value = this.props.config.maxPrice;
        this.setState({
            price: value,
        });
        return true;
    }

    handleSubmitApply() {
        if (
            this.handleChange({
                target: { value: this.state.description, name: "description" },
            }) &&
            this.handleChange({
                target: { value: this.state.profession, name: "profession" },
            }) &&
            this.handlePriceChange({
                target: { value: this.state.price },
            })
        ) {
            if (typeof this.props.id !== "undefined") {
                userService
                    .applyAnswerer(
                        this.props.id,
                        this.state.description +
                            "EwbkK8TU" +
                            this.state.profession,
                        this.state.price
                    )
                    .then(
                        () => {
                            // apply success
                            this.props.alertHandler("success", "提交成功");
                            // update info
                            this.props.fetchUserInfo();
                            this.forceUpdate();
                        },
                        (error) => {
                            // show the error message
                            if (error.response.status === 403) {
                                if (
                                    error.response.data.message ===
                                    "NO_PERMISSION"
                                ) {
                                    this.props.alertHandler(
                                        "error",
                                        "权限不足"
                                    );
                                } else if (
                                    error.response.data.message ===
                                    "ALREADY_ANSWERER"
                                ) {
                                    this.props.alertHandler(
                                        "error",
                                        "已经是回答者"
                                    );
                                } else if (
                                    error.response.data.message ===
                                    "DESCRIPTION_INVALID"
                                ) {
                                    this.props.alertHandler(
                                        "error",
                                        "个人介绍格式错误"
                                    );
                                } else if (
                                    error.response.data.message ===
                                    "PRICE_INVALID"
                                ) {
                                    this.props.alertHandler(
                                        "error",
                                        "价格格式错误"
                                    );
                                } else {
                                    this.props.alertHandler(
                                        "error",
                                        "服务器验证异常"
                                    );
                                }
                            } else if (error.response.status === 401) {
                                this.props.alertHandler("error", "尚未登录");
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
                        // update info
                        this.props.fetchUserInfo();
                    },
                    (error) => {
                        // show the error message
                        if (error.response.status === 403) {
                            if (
                                error.response.data.message === "PRICE_INVALID"
                            ) {
                                this.props.alertHandler(
                                    "error",
                                    "定价超过范围"
                                );
                            } else {
                                this.props.alertHandler(
                                    "error",
                                    "服务器验证异常"
                                );
                            }
                        } else if (error.response.status === 404) {
                            this.props.alertHandler("error", "用户不存在");
                        } else {
                            this.props.alertHandler("error", "网络错误");
                        }
                    }
                );
            }
            this.handleClosePriceDialog();
        }
    }

    handleSubmitAvatar() {

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
                            <label htmlFor="avatar-button-file">
                                <Input
                                    accept="image/*"
                                    id="avatar-button-file"
                                    type="file"
                                    name="image"
                                    multiple={false}
                                    onChange={this.handleSubmitAvatar}/>
                                <IconButton
                                    aria-label="upload avatar"
                                    component="span"
                                >
                                    <Avatar
                                        src={this.props.avatar}
                                        sx={{
                                            height: 100,
                                            width: 100,
                                        }}
                                    />
                                </IconButton>
                            </label>
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
                                        ? "你还不是回答者，快去申请吧~"
                                        : "你已经是回答者了，快去回答问题吧~"}
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
                                onClick={this.handleOpenTipsDialog}
                            >
                                回答者申请
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
                    open={this.state.openTipsDialog}
                    onClose={this.handleCloseTipsDialog}
                >
                    <DialogTitle>回答者须知</DialogTitle>
                    <DialogContent>
                        <DialogContentText mb={1}>
                            在申请成为回答者前，请首先认真阅读以下
                            <Box component="span" fontWeight="fontWeightBold">
                                回答者须知
                            </Box>
                            。 您随后可以于侧栏的“平台须知”再次查看该内容
                        </DialogContentText>
                        {renderAnswerHelp(this.props.config)}
                    </DialogContent>
                    <DialogActions>
                        <Button
                            onClick={this.handleCloseTipsDialog}
                            color="error"
                        >
                            再考虑一下
                        </Button>
                        <Button
                            onClick={() => {
                                this.handleCloseTipsDialog();
                                this.handleOpenApplyDialog();
                            }}
                        >
                            阅读完了
                        </Button>
                    </DialogActions>
                </Dialog>
                <Dialog
                    fullWidth
                    open={this.state.openApplyDialog}
                    onClose={this.handleCloseApplyDialog}
                >
                    <DialogTitle>申请成为回答者</DialogTitle>
                    <DialogContent>
                        <DialogContentText mb={3}>
                            在申请成为回答者前，请填写您的专业领域介绍，
                            重新填写您的个人介绍，以及设置您的问答定价。
                            个人介绍、专业领域介绍是审核的重要根据，并且在提交成功后
                            <Box component="span" fontWeight="fontWeightBold">
                                不可修改
                            </Box>
                            。
                            优秀的、有展示性的个人介绍和精准的专业领域介绍能帮助您获得更多提问者的青睐。
                        </DialogContentText>
                        <TextField
                            fullWidth
                            autoFocus
                            label="个人介绍"
                            name="description"
                            multiline
                            onChange={this.handleChange}
                            rows={4}
                            value={this.state.description}
                            error={
                                this.state.error_msg_description.length !== 0
                            }
                            helperText={this.state.error_msg_description}
                            placeholder="快来介绍一下你自己吧~"
                            variant="outlined"
                            inputProps={{ maxLength: 200 }}
                        />
                        <DialogContentText mt={3} mb={3}>
                            请用
                            <Box component="span" fontWeight="fontWeightBold">
                                逗号
                            </Box>
                            分隔您的问答专业领域。如“体育，健康，运动医学”。
                        </DialogContentText>
                        <TextField
                            fullWidth
                            label="专业领域"
                            name="profession"
                            onChange={this.handleChange}
                            value={this.state.profession}
                            error={this.state.error_msg_profession.length !== 0}
                            helperText={this.state.error_msg_profession}
                            placeholder="请填写您的问答专业领域~"
                            variant="outlined"
                            inputProps={{ maxLength: 50 }}
                        />
                        <DialogContentText mt={3} mb={3}>
                            在当前机制下， 回答定价最高不能超过
                            <Box component="span" fontWeight="fontWeightBold">
                                {this.props.config.maxPrice}
                            </Box>
                            ￥/次， 最低不能低于
                            <Box component="span" fontWeight="fontWeightBold">
                                {this.props.config.minPrice}
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
                                    min: this.props.config.minPrice,
                                    max: this.props.config.maxPrice,
                                },
                                startAdornment: (
                                    <InputAdornment position="start">
                                        ￥/次
                                    </InputAdornment>
                                ),
                            }}
                            value={this.state.price}
                            error={this.state.error_msg_price.length !== 0}
                            helperText={this.state.error_msg_price}
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
                                {this.props.config.maxPrice}
                            </Box>
                            ￥/次， 最低不能低于
                            <Box component="span" fontWeight="fontWeightBold">
                                {this.props.config.minPrice}
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
                                    min: this.props.config.minPrice,
                                    max: this.props.config.maxPrice,
                                },
                                startAdornment: (
                                    <InputAdornment position="start">
                                        ￥/次
                                    </InputAdornment>
                                ),
                            }}
                            value={this.state.price}
                            error={this.state.error_msg_price.length !== 0}
                            helperText={this.state.error_msg_price}
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
