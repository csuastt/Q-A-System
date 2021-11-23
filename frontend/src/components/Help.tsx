import { Box, Card, CardHeader } from "@mui/material";
import React, { useEffect, useState } from "react";
import { ConfigInfo } from "../services/definations";
import systemConfigService from "../services/systemConfigService";
import Typography from "@mui/material/Typography";
import ListItem from "@mui/material/ListItem";
import List from "@mui/material/List";
import Divider from "@mui/material/Divider";
import { formatInterval } from "../util";

export const renderAnswerHelp = (config: ConfigInfo | undefined) => {
    return (
        <List dense={true}>
            <ListItem>
                <Typography variant="body1">
                    1、平台要求：回答定价最高不能超过￥
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.maxPrice ? config?.maxPrice : ""}
                    </Box>
                    /次， 最低不能低于￥
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.minPrice ? config?.minPrice : ""}
                    </Box>
                    /次。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    2、在平台给您发送订单后。您需要在
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.respondExpirationSeconds
                            ? formatInterval(config?.respondExpirationSeconds)
                            : ""}
                    </Box>
                    内接单。超时视为放弃订单。消息中心会随时提醒您剩余时长。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    3、在您接单后。您需要在
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.answerExpirationSeconds
                            ? formatInterval(config?.answerExpirationSeconds)
                            : ""}
                    </Box>
                    内进行首次回答。超时视为放弃订单。消息中心会随时提醒您剩余时长。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    4、单次提问交流的最大聊天消息条数为
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.maxChatMessages ? config?.maxChatMessages : ""}
                    </Box>
                    条，最长聊天时间为
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.maxChatTimeSeconds
                            ? formatInterval(config?.maxChatTimeSeconds)
                            : ""}
                    </Box>
                    。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    5、若聊天消息条数超出限制或聊天超时，系统会自动结束订单，并在之后正常进行结算。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    6、您或提问者可以随时结束订单。结束订单后系统会正常进行结算。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    7、在结束订单后。平台将在
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.fulfillExpirationSeconds
                            ? formatInterval(config?.fulfillExpirationSeconds)
                            : ""}
                    </Box>
                    内进行分成。当前平台抽成率为
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.feeRate ? config?.feeRate : ""}
                    </Box>
                    %。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    8、当您回答公开问题时，公开问题的所得将会有
                    <Box component="span" fontWeight="fontWeightBold">
                        {config?.askerFeeRate ? 100 - config?.feeRate - config?.askerFeeRate
                            : ""}
                    </Box>
                    %支付给您。需要提醒的是，所有公开问题的问题内容及聊天记录均为付费可见。
                </Typography>
            </ListItem>
            <ListItem>
                <Typography variant="body1">
                    9、更多疑问请咨询平台管理员。
                </Typography>
            </ListItem>
        </List>
    );
};

const Help: React.FC<{}> = (props) => {
    const [config, setConfig] = useState<ConfigInfo>();

    useEffect(() => {
        systemConfigService.getSystemConfig().then((configInfo) => {
            setConfig(configInfo);
        });
    }, []);

    return (
        <Box mt={2} sx={{ width: "95%" }}>
            <Card>
                <CardHeader
                    title="平台须知"
                    subheader="下方显示了目前的平台有关规定"
                />
                <Box ml={1} mb={3}>
                    <Divider sx={{ mb: 1 }}>提问者须知</Divider>
                    <List dense={true}>
                        <ListItem>
                            <Typography variant="body1">
                                1、您提问前需要预先付款。提问后，您的问题会首先进行审核，审核成功后我们会及时通知回答者。
                            </Typography>
                        </ListItem>
                        <ListItem>
                            <Typography variant="body1">
                                2、若审核失败，我们将全额退款。您可以修改后重新提问。
                            </Typography>
                        </ListItem>
                        <ListItem>
                            <Typography variant="body1">
                                3、您的问题不会被公开，只有您和回答者可以知晓其中的内容。
                            </Typography>
                        </ListItem>
                        <ListItem>
                            <Typography variant="body1">
                                4、单次提问交流的最大聊天消息条数为
                                <Box
                                    component="span"
                                    fontWeight="fontWeightBold"
                                >
                                    {config?.maxChatMessages
                                        ? config?.maxChatMessages
                                        : ""}
                                </Box>
                                条，最长聊天时间为
                                <Box
                                    component="span"
                                    fontWeight="fontWeightBold"
                                >
                                    {config?.maxChatTimeSeconds
                                        ? formatInterval(
                                              config?.maxChatTimeSeconds
                                          )
                                        : ""}
                                </Box>
                                。
                            </Typography>
                        </ListItem>
                        <ListItem>
                            <Typography variant="body1">
                                5、若聊天消息条数超出限制或聊天超时，系统会自动结束订单，并在之后正常进行结算。
                            </Typography>
                        </ListItem>
                        <ListItem>
                            <Typography variant="body1">
                                6、您或回答者可以随时结束订单。结束订单后系统会正常进行结算。
                            </Typography>
                        </ListItem>
                        <ListItem>
                            <Typography variant="body1">
                                7、您可以设置问题为公开问题。公开问题将会被加入问答库，
                                其问题内容及聊天记录均为付费可见。其他用户需要支付一定费用才能查看公开问题，所支付费用的
                                <Box component="span" fontWeight="fontWeightBold">
                                    {config?.askerFeeRate ? config?.askerFeeRate
                                        : ""}
                                </Box>
                                %将会直接支付给您
                            </Typography>
                        </ListItem>
                        <ListItem>
                            <Typography variant="body1">
                                8、更多疑问请咨询平台管理员。
                            </Typography>
                        </ListItem>
                    </List>
                    <Divider sx={{ mt: 1, mb: 1 }}>回答者须知</Divider>
                    {renderAnswerHelp(config)}
                </Box>
            </Card>
        </Box>
    );
};

export default Help;
