import { Card, CardHeader, Box } from "@mui/material";
import ReactApexChart from "react-apexcharts";
import { merge } from "lodash";
import BaseOptionChart from "./ChartBaseOption";
import React, { useEffect, useState } from "react";
import userService from "../services/userService";

const IncomeStatistics: React.FC<{
    userId: number | undefined;
}> = (props) => {
    const [earningsList, setEarningsList] = useState<Array<number>>(
        new Array(12).fill(0)
    );

    // init date list
    let labelsList = new Array<string>(12);
    let date: Date = new Date();
    for (let index = 11; index >= 0; index--) {
        let m = date.getMonth() + 1;
        let m_str = m >= 10 ? m.toString() : "0" + m.toString();
        labelsList[index] = date.getFullYear() + "-" + m_str;
        date.setMonth(date.getMonth() - 1);
    }

    useEffect(() => {
        if (typeof props.userId !== "undefined") {
            userService.getUserIncome(props.userId).then((info) => {
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
    }, [props.userId]);

    const chartOptions = merge(BaseOptionChart(), {
        stroke: { width: [2, 2, 0] },
        plotOptions: { bar: { columnWidth: "11%", borderRadius: 4 } },
        fill: { type: ["gradient", "solid", "solid"] },
        labels: labelsList,
        xaxis: { type: "datetime" },
        tooltip: {
            shared: true,
            intersect: false,
            y: {
                formatter: (y: undefined | number) => {
                    if (typeof y !== "undefined") {
                        return `${y.toFixed(0)} ￥`;
                    }
                    return y;
                },
            },
        },
    });

    return (
        <Box mt={2} sx={{ width: "95%" }}>
            <Card>
                <CardHeader
                    title="用户收支统计"
                    subheader="下方显示了您过去你一年的收支情况"
                />
                <Box sx={{ m: 2 }} dir="ltr">
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
                            width="95%"
                        />
                    }
                </Box>
            </Card>
        </Box>
    );
};

export default IncomeStatistics;
