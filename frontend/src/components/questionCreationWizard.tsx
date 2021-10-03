import React, { useState } from "react";
import Alert from "@mui/material/Alert";
import AlertTitle from "@mui/material/AlertTitle";
import CircularProgress from "@mui/material/CircularProgress";
import Step from "@mui/material/Step";
import Stepper from "@mui/material/Stepper";
import StepLabel from "@mui/material/StepLabel";
import StepContent from "@mui/material/StepContent";
import Typography from "@mui/material/Typography";
import UserCard from "./userCard";
import Stack from "@mui/material/Stack";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import { Link as RouterLink } from "react-router-dom";
import questionService from "../services/question.service";
import { CreationResult, CreationResultType } from "../services/definations";

const QuestionCreationWizard: React.FC<{ answererId?: number }> = (props) => {
    const [activeStep, setActiveStep] = useState(0);
    const [questionText, setQuestionText] = useState<string>("");
    const [questionError, setQuestionError] = useState(false);
    const [descriptionText, setDescriptionText] = useState<string>("");
    const [result, setResult] = useState<CreationResult>();

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
        setQuestionError(false);
    };

    const handleDescriptionChange = (
        event: React.ChangeEvent<HTMLInputElement>
    ) => {
        setDescriptionText(event.target.value);
    };

    const checkInputAndNextStep = () => {
        if (questionText && questionText.length >= 10) {
            nextStep();
        } else {
            setQuestionError(true);
        }
    };

    const createQuestion = () => {
        nextStep();
        questionService
            .create_question(props.answererId!, questionText, descriptionText)
            .then((res) => setResult(res));
    };

    const renderResult = () => {
        if (result) {
            if (result.type === CreationResultType.SUCCESS) {
                return <Alert severity="success">您的问题已经创建成功！</Alert>;
            } else {
                return (
                    <Alert severity="error">
                        <AlertTitle>问题创建出错</AlertTitle>
                        {result.err_msg}
                    </Alert>
                );
            }
        } else {
            return (
                <Stack
                    spacing={3}
                    direction="row"
                    justifyContent="center"
                    sx={{ mt: 5 }}
                >
                    <CircularProgress />
                    <Typography variant="h5" gutterBottom>
                        正在创建问题
                    </Typography>
                </Stack>
            );
        }
    };

    return (
        <>
            <Stepper activeStep={activeStep} orientation="vertical">
                <Step>
                    <StepLabel>选择回答者</StepLabel>
                    <StepContent>
                        {props.answererId ? (
                            <>
                                <UserCard userId={props.answererId} />
                                <Stack
                                    spacing={2}
                                    direction="row"
                                    sx={{ mt: 2 }}
                                >
                                    <Button
                                        variant="contained"
                                        onClick={nextStep}
                                    >
                                        下一步
                                    </Button>
                                    <Button
                                        variant="outlined"
                                        color="error"
                                        component={RouterLink}
                                        to={`/question/select-answerer`}
                                    >
                                        重新选择
                                    </Button>
                                </Stack>
                            </>
                        ) : (
                            <>
                                <Typography variant="subtitle1">
                                    您还没有选择回答者
                                </Typography>
                                <Button
                                    variant="outlined"
                                    color="warning"
                                    component={RouterLink}
                                    to={`/question/select-answerer`}
                                >
                                    浏览列表
                                </Button>
                            </>
                        )}
                    </StepContent>
                </Step>
                <Step>
                    <StepLabel>填写问题信息</StepLabel>
                    <StepContent>
                        <TextField
                            label="问题"
                            margin="normal"
                            value={questionText}
                            onChange={handleQuestionChange}
                            error={questionError}
                            helperText={questionError && "问题长度不应小于10"}
                            fullWidth
                        />
                        <TextField
                            label="详细描述"
                            margin="normal"
                            value={descriptionText}
                            onChange={handleDescriptionChange}
                            fullWidth
                            multiline
                            rows={5}
                        />
                        <Stack spacing={2} direction="row" sx={{ mt: 2 }}>
                            <Button
                                variant="contained"
                                onClick={checkInputAndNextStep}
                            >
                                下一步
                            </Button>
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={previousStep}
                            >
                                上一步
                            </Button>
                        </Stack>
                    </StepContent>
                </Step>
                <Step>
                    <StepLabel>确认支付</StepLabel>
                    <StepContent>
                        <Typography variant="body1">
                            您现在需要预先付款以完成提问。在您确认得到了满意的回答之后，回答者才可能收到这份酬劳。
                        </Typography>
                        <Typography variant="body1">
                            您的问题首先需要经过审核才能发送到回答者。若审核失败，您可以修改问题或取消提问。如果在回答者接单之前取消提问，我们将全额退款。
                        </Typography>
                        <Stack spacing={2} direction="row" sx={{ mt: 2 }}>
                            <Button
                                variant="contained"
                                onClick={createQuestion}
                            >
                                支付并完成提问
                            </Button>
                            <Button
                                variant="outlined"
                                color="error"
                                onClick={previousStep}
                            >
                                上一步
                            </Button>
                        </Stack>
                    </StepContent>
                </Step>
            </Stepper>
            {activeStep === 3 && renderResult()}
        </>
    );
};

export default QuestionCreationWizard;
