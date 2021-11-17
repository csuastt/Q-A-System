import React, { useContext, useEffect, useState } from "react";
import CircularProgress from "@mui/material/CircularProgress";
import Step from "@mui/material/Step";
import Stepper from "@mui/material/Stepper";
import StepLabel from "@mui/material/StepLabel";
import StepContent from "@mui/material/StepContent";
import Typography from "@mui/material/Typography";
import AnswererCard from "./AnswererCard";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import { Link as RouterLink, useParams } from "react-router-dom";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import questionService from "../services/orderService";
import { ConfigInfo, CreationResult, FileInfo } from "../services/definations";
import useMediaQuery from "@mui/material/useMediaQuery";
import { styled, useTheme } from "@mui/material/styles";
import Box from "@mui/material/Box";
import UserContext from "../AuthContext";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import Grid from "@mui/material/Grid";
import AnswererDetailCard from "./AnswererDetailCard";
import Divider from "@mui/material/Divider";
import Markdown from "./Markdown";
import systemConfigService from "../services/systemConfigService";
import {
    Dialog,
    DialogTitle,
    IconButton,
    Link,
    ListItemAvatar,
    ListItemButton,
    ListItemText,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import Avatar from "@mui/material/Avatar";
import FolderIcon from "@mui/icons-material/Folder";
import { formatSize } from "../util";

function processInt(str?: string): number {
    if (str) {
        const x = parseInt(str);
        if (!isNaN(x)) {
            return x;
        }
    }
    return -1;
}

const OrderCreationWizard: React.FC = (props) => {
    const [activeStep, setActiveStep] = useState(0);
    const [questionTitle, setQuestionTitle] = useState<string>("");
    const [questionDescription, setQuestionDescription] = useState<string>("");
    const [questionError, setQuestionError] = useState(false);
    const [result, setResult] = useState<CreationResult>();
    const [config, setConfig] = useState<ConfigInfo>();
    const [files, setFiles] = useState<Array<FileInfo>>([]);
    const [open, setOpen] = React.useState(false);

    const { user } = useContext(UserContext);
    const routerParam = useParams<{ answerer?: string }>();
    const answerer = processInt(routerParam.answerer);

    useEffect(() => {
        systemConfigService.getSystemConfig().then((configInfo) => {
            setConfig(configInfo);
        });
    }, []);

    const nextStep = () => {
        setActiveStep(activeStep + 1);
    };

    const previousStep = () => {
        setActiveStep(activeStep - 1);
    };

    const handleQuestionTitleChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ) => {
        setQuestionTitle(event.target.value);
        checkInput(event.target.value);
    };

    const handleFilesUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
        let fileList = [];
        if (event.target.files !== null) {
            for (let i = 0; i < event.target.files.length; i++) {
                fileList.push({
                    file: event.target.files[i],
                    url: URL.createObjectURL(event.target.files[i]),
                });
            }
        }
        setFiles(fileList);
    };

    const handleFilesClear = () => {
        setFiles([]);
    };

    const handleFileDelete = (name: string) => {
        let curFiles = files;
        curFiles = curFiles.filter(function (file) {
            return file.file.name !== name;
        });
        if (!curFiles.length) handleClose();
        setFiles(curFiles);
    };

    const handleOpen = () => {
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
    };

    const handleQuestionDescriptionChange = (newValue: string) =>
        setQuestionDescription(newValue);

    const checkInput = (input: string) => {
        if (input && input.length >= 10) {
            setQuestionError(false);
        } else {
            setQuestionError(true);
        }
    };

    const checkInputAndNextStep = () => {
        if (!questionError && questionTitle && questionTitle.length >= 10) {
            nextStep();
        } else {
            checkInput(questionTitle);
        }
    };

    const createQuestion = () => {
        nextStep();
        if (user && user.id === answerer) {
            // answering yourself is not allowed
            setResult({
                id: -1,
                type: 1,
                state: "NULL",
                created_id: -1,
                message: "ASKER_ANSWER_SAME",
            });
        } else {
            questionService
                .createQuestion(
                    user!.id,
                    answerer,
                    questionTitle,
                    questionDescription
                )
                .then(
                    (res) => {
                        setResult(res);
                        // upload attachments
                        if (files !== null) {
                            for (let i = 0; i < files.length; i++) {
                                questionService
                                    .uploadAttachment(res.id, files[i].file)
                                    .then((r) => {
                                        console.log(r);
                                    });
                            }
                        }
                    },
                    (error) => {
                        setResult({
                            id: -1,
                            type: 1,
                            state: "NULL",
                            created_id: -1,
                            message: error.response.data.message,
                        });
                    }
                );
        }
    };

    const renderResult = () => {
        if (result) {
            if (result.message === "BALANCE_NOT_ENOUGH") {
                return (
                    <Box textAlign={matches ? "center" : "start"} mt={1}>
                        <ErrorOutlineIcon
                            color="error"
                            sx={
                                matches
                                    ? { fontSize: 80 }
                                    : {
                                          fontSize: 60,
                                          marginLeft: 7,
                                      }
                            }
                        />
                        <Typography
                            variant={matches ? "h5" : "h6"}
                            mt={1}
                            mb={matches ? 4 : 2}
                        >
                            {"余额不足，请充值后重试"}
                        </Typography>
                        <Button
                            variant="outlined"
                            color="error"
                            component={RouterLink}
                            to={`/answerers/select`}
                            size={matches ? "large" : "medium"}
                        >
                            重新选择
                        </Button>
                    </Box>
                );
            } else if (result.message === "ASKER_ANSWER_SAME") {
                return (
                    <Box textAlign={matches ? "center" : "start"} mt={1}>
                        <ErrorOutlineIcon
                            color="error"
                            sx={
                                matches
                                    ? { fontSize: 80 }
                                    : {
                                          fontSize: 60,
                                          marginLeft: 7,
                                      }
                            }
                        />
                        <Typography
                            variant={matches ? "h5" : "h6"}
                            mt={1}
                            mb={matches ? 4 : 2}
                        >
                            {"不能向自己提问，请选择其他回答者重试"}
                        </Typography>
                        <Button
                            variant="outlined"
                            color="error"
                            component={RouterLink}
                            to={`/answerers/select`}
                            size={matches ? "large" : "medium"}
                        >
                            重新选择
                        </Button>
                    </Box>
                );
            } else if (result.state === "CREATED") {
                return (
                    <Box textAlign={matches ? "center" : "start"} mt={1}>
                        <CheckCircleOutlineIcon
                            color="success"
                            sx={
                                matches
                                    ? { fontSize: 80 }
                                    : {
                                          fontSize: 60,
                                          marginLeft: 8,
                                      }
                            }
                        />
                        <Typography
                            variant={matches ? "h5" : "h6"}
                            mt={1}
                            mb={matches ? 4 : 2}
                        >
                            {"您的问题已经创建成功"}
                        </Typography>
                        <Button
                            variant="outlined"
                            color="success"
                            component={RouterLink}
                            to={`/answerers/select`}
                            size={matches ? "large" : "medium"}
                        >
                            再问一个
                        </Button>
                    </Box>
                );
            } else {
                return (
                    <Box textAlign={matches ? "center" : "start"} mt={1}>
                        <ErrorOutlineIcon
                            color="error"
                            sx={
                                matches
                                    ? { fontSize: 80 }
                                    : {
                                          fontSize: 60,
                                          marginLeft: 7,
                                      }
                            }
                        />
                        <Typography
                            variant={matches ? "h5" : "h6"}
                            mt={1}
                            mb={matches ? 4 : 2}
                        >
                            {"您的问题创建出错"}
                        </Typography>
                        <Button
                            variant="outlined"
                            color="error"
                            component={RouterLink}
                            to={`/answerers/select`}
                            size={matches ? "large" : "medium"}
                        >
                            重新选择
                        </Button>
                    </Box>
                );
            }
        } else {
            return (
                <Stack
                    spacing={3}
                    direction="row"
                    justifyContent="center"
                    sx={matches ? { mt: 16 } : { mt: 5 }}
                >
                    <CircularProgress />
                    <Typography variant="h5" gutterBottom>
                        正在创建问题
                    </Typography>
                </Stack>
            );
        }
    };

    const theme = useTheme();

    // if match the mobile size
    const matches = useMediaQuery(theme.breakpoints.up("md"));

    // render 1st step
    const renderFirstStep = () => {
        return answerer >= 0 ? (
            <>
                <Grid container justifyContent={"center"} spacing={4}>
                    <Grid item md={4} xs={8}>
                        <AnswererCard userId={answerer} briefMsg={true} />
                    </Grid>
                    <Grid item md={8} xs={12}>
                        <AnswererDetailCard userId={answerer} />
                    </Grid>
                </Grid>
                <Stack
                    spacing={matches ? 4 : 2}
                    direction="row"
                    sx={matches ? { mt: 4 } : { mt: 2 }}
                    justifyContent={matches ? "center" : "flex-start"}
                >
                    {matches ? (
                        <>
                            <Button
                                variant="outlined"
                                color="error"
                                component={RouterLink}
                                to={`/answerers/select`}
                                size={"large"}
                            >
                                重新选择
                            </Button>
                            <Button
                                variant="contained"
                                onClick={nextStep}
                                size={"large"}
                            >
                                下一步
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button
                                variant="contained"
                                onClick={nextStep}
                                size={"medium"}
                            >
                                下一步
                            </Button>
                            <Button
                                variant="outlined"
                                color="error"
                                component={RouterLink}
                                to={`/answerers/select`}
                                size={"medium"}
                            >
                                重新选择
                            </Button>
                        </>
                    )}
                </Stack>
            </>
        ) : (
            <>
                <Box textAlign={matches ? "center" : "start"} mt={1}>
                    <ErrorOutlineIcon
                        color="warning"
                        sx={
                            matches
                                ? { fontSize: 80 }
                                : {
                                      fontSize: 60,
                                      marginLeft: 7.5,
                                  }
                        }
                    />
                    <Typography
                        variant={matches ? "h5" : "h6"}
                        mt={1}
                        mb={matches ? 4 : 2}
                    >
                        {"您还没有选择回答者"}
                    </Typography>
                    <Button
                        variant="outlined"
                        color="warning"
                        component={RouterLink}
                        to={`/answerers/select`}
                        size={matches ? "large" : "medium"}
                    >
                        浏览列表
                    </Button>
                </Box>
            </>
        );
    };

    // an Input without display
    const Input = styled("input")({
        display: "none",
    });

    // render 2nd step
    const renderSecondStep = () => {
        return (
            <>
                <TextField
                    label="问题标题"
                    margin="normal"
                    value={questionTitle}
                    onChange={handleQuestionTitleChange}
                    error={questionError}
                    helperText={questionError && "标题长度不应小于10"}
                    fullWidth
                    inputProps={{ maxLength: 100 }}
                />
                <Stack
                    sx={{ mt: 2, mb: 2 }}
                    spacing={2}
                    direction="row"
                    alignItems={"center"}
                >
                    <Typography>上传附件(小于100M):</Typography>
                    <label htmlFor="contained-button-file">
                        <Input
                            accept="*"
                            id="contained-button-file"
                            type="file"
                            name="attachments"
                            multiple={true}
                            onChange={handleFilesUpload}
                        />
                        <Link component="span">添加</Link>
                    </label>
                    <Link
                        component="button"
                        variant="body1"
                        color={"error"}
                        onClick={handleFilesClear}
                    >
                        清空
                    </Link>
                    {files.length > 0 && (
                        <Link
                            component="button"
                            variant="body1"
                            onClick={handleOpen}
                        >
                            查看
                        </Link>
                    )}
                </Stack>
                <Markdown
                    value={questionDescription}
                    onChange={handleQuestionDescriptionChange}
                    height="500px"
                />
                <Stack
                    spacing={matches ? 4 : 2}
                    direction="row"
                    sx={matches ? { mt: 4 } : { mt: 2 }}
                    justifyContent={matches ? "center" : "flex-start"}
                >
                    {matches ? (
                        <>
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={previousStep}
                                size={"large"}
                            >
                                上一步
                            </Button>
                            <Button
                                variant="contained"
                                onClick={checkInputAndNextStep}
                                size={"large"}
                            >
                                下一步
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button
                                variant="contained"
                                onClick={checkInputAndNextStep}
                                size={"medium"}
                            >
                                下一步
                            </Button>
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={previousStep}
                                size={"medium"}
                            >
                                上一步
                            </Button>
                        </>
                    )}
                </Stack>
                <Dialog onClose={handleClose} open={open}>
                    <DialogTitle>附件列表</DialogTitle>
                    <List sx={{ pt: 0 }}>
                        {files.map((fileInfo) => (
                            <ListItem
                                secondaryAction={
                                    <IconButton
                                        edge="end"
                                        aria-label="delete"
                                        onClick={() => {
                                            handleFileDelete(
                                                fileInfo.file.name
                                            );
                                        }}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                }
                            >
                                <ListItemButton
                                    role={undefined}
                                    dense
                                    component="a"
                                    href={fileInfo.url}
                                    key={fileInfo.url}
                                >
                                    <ListItemAvatar>
                                        <Avatar>
                                            <FolderIcon />
                                        </Avatar>
                                    </ListItemAvatar>
                                    <ListItemText
                                        primary={fileInfo.file.name}
                                        secondary={formatSize(
                                            fileInfo.file.size,
                                            2
                                        )}
                                    />
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </List>
                </Dialog>
            </>
        );
    };

    // render 3rd step
    const renderThirdStep = () => {
        return (
            <>
                <Divider sx={{ mt: 3, mb: 1 }}>提问须知</Divider>
                <List dense={true}>
                    <ListItem>
                        <Typography variant="body1">
                            1、您现在需要预先付款以完成提问。在您确认得到了满意的回答之后，回答者才可能收到这份酬劳。
                        </Typography>
                    </ListItem>
                    <ListItem>
                        <Typography variant="body1">
                            2、您的问题首先需要经过审核才能发送到回答者。若审核失败，我们将全额退款。您可以修改后重新提问。
                        </Typography>
                    </ListItem>
                    <ListItem>
                        <Typography variant="body1">
                            3、完成提问后，系统会及时通知回答者。回答者将在
                            <Box component="span" fontWeight="fontWeightBold">
                                {config?.respondExpirationSeconds
                                    ? config?.respondExpirationSeconds /
                                      3600 /
                                      24
                                    : ""}
                            </Box>
                            天内接单。
                        </Typography>
                    </ListItem>
                    <ListItem>
                        <Typography variant="body1">
                            4、您的问题不会被公开，只有您和回答者可以知晓其中的内容。
                        </Typography>
                    </ListItem>
                    <ListItem>
                        <Typography variant="body1">
                            5、单次提问交流的最大聊天消息条数为
                            <Box component="span" fontWeight="fontWeightBold">
                                {config?.maxChatMessages
                                    ? config?.maxChatMessages
                                    : ""}
                            </Box>
                            条，最长聊天时间为
                            <Box component="span" fontWeight="fontWeightBold">
                                {config?.maxChatTimeSeconds
                                    ? config?.maxChatTimeSeconds / 3600
                                    : ""}
                            </Box>
                            小时。
                        </Typography>
                    </ListItem>
                    <ListItem>
                        <Typography variant="body1">
                            6、若聊天消息条数超出限制或聊天超时，系统会自动结束订单，并在之后正常进行结算。
                        </Typography>
                    </ListItem>
                </List>
                <Stack
                    spacing={matches ? 4 : 2}
                    direction="row"
                    sx={matches ? { mt: 4 } : { mt: 2 }}
                    justifyContent={matches ? "center" : "flex-start"}
                >
                    {matches ? (
                        <>
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={previousStep}
                                size={"large"}
                            >
                                上一步
                            </Button>
                            <Button
                                variant="contained"
                                onClick={createQuestion}
                                size={"large"}
                            >
                                支付并完成提问
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button
                                variant="contained"
                                onClick={createQuestion}
                                size={"medium"}
                            >
                                支付并完成提问
                            </Button>
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={previousStep}
                                size={"medium"}
                            >
                                上一步
                            </Button>
                        </>
                    )}
                </Stack>
            </>
        );
    };

    // render the mobile screen style
    const renderMobileScreen = () => {
        return (
            <Box mt={3}>
                <Stepper activeStep={activeStep} orientation="vertical">
                    <Step>
                        <StepLabel>选择回答者</StepLabel>
                        <StepContent>{renderFirstStep()}</StepContent>
                    </Step>
                    <Step>
                        <StepLabel>填写问题信息</StepLabel>
                        <StepContent>{renderSecondStep()}</StepContent>
                    </Step>
                    <Step>
                        <StepLabel>确认支付</StepLabel>
                        <StepContent>{renderThirdStep()}</StepContent>
                    </Step>
                </Stepper>
            </Box>
        );
    };

    // render the wide screen style
    const renderWideScreen = () => {
        return (
            <>
                <Box mt={8}>
                    <Stepper activeStep={activeStep} orientation="horizontal">
                        <Step>
                            <StepLabel>选择回答者</StepLabel>
                        </Step>
                        <Step>
                            <StepLabel>填写问题信息</StepLabel>
                        </Step>
                        <Step>
                            <StepLabel>确认支付</StepLabel>
                        </Step>
                    </Stepper>
                </Box>
                <Box mt={5}>
                    {activeStep === 0 ? (
                        renderFirstStep()
                    ) : activeStep === 1 ? (
                        renderSecondStep()
                    ) : activeStep === 2 ? (
                        renderThirdStep()
                    ) : (
                        <></>
                    )}
                </Box>
            </>
        );
    };

    return (
        <>
            {matches ? renderWideScreen() : renderMobileScreen()}
            {activeStep === 3 && renderResult()}
        </>
    );
};

export default OrderCreationWizard;
