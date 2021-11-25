import { Box, Card, CardHeader } from "@mui/material";
import ReactApexChart from "react-apexcharts";
import { merge } from "lodash";
import BaseOptionChart from "./BaseOptionChart";
import React, { useEffect, useMemo, useState } from "react";
import userService from "../services/userService";
import { ConfigInfo, UserInfo } from "../services/definations";
import systemConfigService from "../services/systemConfigService";
import Typography from "@mui/material/Typography";
import { Link as RouterLink } from "react-router-dom";
import Link from "@mui/material/Link";
import configService from "../services/configService";
import { useTheme } from "@mui/material/styles";

const IncomeStatistics: React.FC<{
    userId?: number;
    user?: UserInfo;
    isAdmin: boolean;
    briefMsg: boolean;
}> = (props) => {
    const monthCount: number = props.briefMsg ? 6 : 12;
    const [earningsList, setEarningsList] = useState<Array<number>>(
        new Array(monthCount).fill(0)
    );
    const [config, setConfig] = useState<ConfigInfo>();
    const theme = useTheme();
    const mode = theme.palette.mode;

    // init date list
    let labelsList = useMemo(() => {
        let ll = new Array<string>(monthCount);
        let date: Date = new Date();
        for (let index = monthCount - 1; index >= 0; index--) {
            let m = date.getMonth() + 1;
            let m_str = m >= 10 ? m.toString() : "0" + m.toString();
            ll[index] = date.getFullYear() + "-" + m_str;
            date.setMonth(date.getMonth() - 1);
        }
        return ll;
    }, [monthCount]);

    useEffect(() => {
        if (!props.isAdmin) {
            let userId = null;
            if (props.userId) userId = props.userId;
            else if (props.user) userId = props.user.id;
            if (userId) {
                userService.getUserIncome(userId).then((info) => {
                    let new_earningsList = new Array(12).fill(0);
                    info.monthly.forEach((monthly) => {
                        let index = labelsList.findIndex((label) => {
                            return label === monthly.month;
                        });
                        new_earningsList[index] = monthly.earnings;
                    });
                    setEarningsList(new_earningsList);
                });
            }
            if (!props.briefMsg)
                systemConfigService.getSystemConfig().then((configInfo) => {
                    setConfig(configInfo);
                });
        } else {
            configService.getSystemEarnings().then((info) => {
                let new_earningsList = new Array(12).fill(0);
                info.monthly.forEach((monthly) => {
                    let index = labelsList.findIndex((label) => {
                        return label === monthly.month;
                    });
                    new_earningsList[index] = monthly.earnings;
                });
                setEarningsList(new_earningsList);
            });

            if (!props.briefMsg)
                configService.getSystemConfig().then((info) => {
                    setConfig(info);
                });
        }
    }, [labelsList, props.briefMsg, props.isAdmin, props.user, props.userId]);

    const fillType = props.briefMsg ? "solid" : "gradient";
    const chartOptions = merge(BaseOptionChart(), {
        stroke: { width: [2, 2, 0] },
        plotOptions: { bar: { columnWidth: "11%", borderRadius: 4 } },
        fill: { type: [fillType, "solid", "solid"] },
        labels: labelsList,
        xaxis: { type: "datetime" },
        tooltip: {
            shared: true,
            intersect: false,
            y: {
                formatter: (y: undefined | number) => {
                    if (typeof y !== "undefined") {
                        return `￥ ${y.toFixed(0)}`;
                    }
                    return y;
                },
            },
        },
        theme: {
            mode: mode,
        },
    });

    return props.briefMsg ? (
        <Box>
            <Card>
                {props.isAdmin ? (
                    <CardHeader
                        title={
                            <Typography align="left" variant="h6">
                                平台收入
                            </Typography>
                        }
                        subheader={
                            <>
                                <Typography align="left" variant="body2">
                                    平台近半年的收入情况，
                                    <Link
                                        variant="body2"
                                        component={RouterLink}
                                        to="/admins/income"
                                    >
                                        点此查看更多
                                    </Link>
                                </Typography>
                            </>
                        }
                        sx={{ paddingBottom: 0 }}
                    />
                ) : (
                    <CardHeader
                        title={
                            <Typography align="left" variant="h6">
                                我的收入
                            </Typography>
                        }
                        subheader={
                            <>
                                <Typography align="left" variant="body2">
                                    下方显示了您近半年的收入情况，
                                    <Link
                                        variant="body2"
                                        component={RouterLink}
                                        to="/income"
                                    >
                                        点此查看更多
                                    </Link>
                                </Typography>
                            </>
                        }
                        sx={{ paddingBottom: 0 }}
                    />
                )}

                <Box sx={{ m: 1 }} dir="ltr">
                    <ReactApexChart
                        type="bar"
                        series={[
                            {
                                name: "收入",
                                type: "column",
                                data: earningsList,
                            },
                        ]}
                        // @ts-ignore
                        options={chartOptions}
                        height="170%"
                    />
                </Box>
            </Card>
        </Box>
    ) : (
        <Box mt={2} sx={{ width: "95%" }}>
            <Card>
                {props.isAdmin ? (
                    <CardHeader
                        title="平台收入统计"
                        subheader="平台过去一年的收入情况"
                    />
                ) : (
                    <CardHeader
                        title="用户收入统计"
                        subheader="下方显示了您过去你一年的收入情况"
                    />
                )}
                {props.isAdmin ? (
                    <Box sx={{ m: 2, marginBottom: 1 }} dir="ltr">
                        {
                            <ReactApexChart
                                type="line"
                                series={[
                                    {
                                        name: "收入",
                                        type: "area",
                                        data: earningsList,
                                    },
                                    {
                                        name: "支出",
                                        type: "line",
                                        data: [],
                                    },
                                    {
                                        name: "净收入",
                                        type: "column",
                                        data: [],
                                    },
                                ]}
                                // @ts-ignore
                                options={chartOptions}
                                height={364}
                                width="94%"
                            />
                        }
                    </Box>
                ) : (
                    <Box sx={{ m: 2, marginBottom: 1 }} dir="ltr">
                        {
                            <ReactApexChart
                                type="line"
                                series={[
                                    {
                                        name: "收入",
                                        type: "area",
                                        data: earningsList,
                                    },
                                    {
                                        name: "支出",
                                        type: "line",
                                        data: [],
                                    },
                                    {
                                        name: "净收入",
                                        type: "column",
                                        data: [],
                                    },
                                ]}
                                // @ts-ignore
                                options={chartOptions}
                                height={364}
                                width="94%"
                            />
                        }
                    </Box>
                )}

                <Box ml={3} mb={3}>
                    <Typography variant="body2">
                        PS：当前平台抽成率为
                        <Box component="span" fontWeight="fontWeightBold">
                            {config?.feeRate ? config?.feeRate : ""}
                        </Box>
                        %。
                    </Typography>
                </Box>
            </Card>
        </Box>
    );
};

export default IncomeStatistics;
