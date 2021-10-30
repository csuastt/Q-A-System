import React, { useContext, useState } from "react";
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
import questionService from "../services/orderService";
import { CreationResult } from "../services/definations";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import Box from "@mui/material/Box";
import UserContext from "../AuthContext";
import ErrorOutlineIcon from "@mui/icons-material/ErrorOutline";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import Grid from "@mui/material/Grid";

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
    const [questionText, setQuestionText] = useState<string>("");
    const [questionError, setQuestionError] = useState(false);
    const [result, setResult] = useState<CreationResult>();

    const { user } = useContext(UserContext);
    const routerParam = useParams<{ answerer?: string }>();
    const answerer = processInt(routerParam.answerer);

    const nextStep = () => {
        setActiveStep(activeStep + 1);
    };

    const previousStep = () => {
        setActiveStep(activeStep - 1);
    };

    const handleQuestionChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ) => {
        setQuestionText(event.target.value);
        checkInput(event.target.value);
    };

    const checkInput = (input: string) => {
        if (input && input.length >= 10) {
            setQuestionError(false);
        } else {
            setQuestionError(true);
        }
    };

    const checkInputAndNextStep = () => {
        if (!questionError && questionText && questionText.length >= 10) {
            nextStep();
        } else {
            checkInput(questionText);
        }
    };

    const createQuestion = () => {
        nextStep();
        questionService
            .create_question(user!.id, answerer, questionText)
            .then((res) => setResult(res));
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
                            {"余额不足，请充值后再试"}
                        </Typography>
                    </Box>)
            }
            else if (result.state === "CREATED") {
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
                <Grid
                    container
                    justifyContent={"center"}
                >
                    <Grid
                        item
                        lg={4}
                        md={6}
                        xs={12}
                    >
                        <AnswererCard userId={answerer} />
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

    // render 2nd step
    const renderSecondStep = () => {
        return (
            <>
                <TextField
                    label="问题"
                    margin="normal"
                    value={questionText}
                    onChange={handleQuestionChange}
                    error={questionError}
                    helperText={questionError && "问题长度不应小于10"}
                    fullWidth
                    multiline
                    rows={5}
                    inputProps={{ maxLength: 100 }}
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
            </>
        );
    };

    // render 3rd step
    const renderThirdStep = () => {
        return (
            <>
                <Typography variant="body1">
                    您现在需要预先付款以完成提问。在您确认得到了满意的回答之后，回答者才可能收到这份酬劳。
                </Typography>
                <Typography variant="body1">
                    您的问题首先需要经过审核才能发送到回答者。若审核失败，您可以修改问题或取消提问。如果在回答者接单之前取消提问，我们将全额退款。
                </Typography>
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
