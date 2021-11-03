import { Card, CardHeader, Box } from '@mui/material';
import ReactApexChart from 'react-apexcharts';
import { merge } from 'lodash';
import BaseOptionChart from "./ChartBaseOption";

const CHART_DATA = [
    {
        name: '收入',
        type: 'area',
        data: [44, 55, 41, 67, 22, 43, 21, 41, 56, 27, 43, 20]
    },
    {
        name: '支出',
        type: 'line',
        data: []
    },
    {
        name: '净收入',
        type: 'column',
        data: []
    }
];

export default function IncomeStatistics() {

    const chartOptions = merge(BaseOptionChart(), {
        stroke: { width: [2, 2, 0] },
        plotOptions: { bar: { columnWidth: '11%', borderRadius: 4 } },
        fill: { type: ['gradient', 'solid', 'solid'] },
        labels: [
            '2020-12-01',
            '2021-01-01',
            '2021-02-01',
            '2021-03-01',
            '2021-04-01',
            '2021-05-01',
            '2021-06-01',
            '2021-07-01',
            '2021-08-01',
            '2021-09-01',
            '2021-10-01',
            '2021-11-01',
        ],
        xaxis: { type: 'datetime' },
        tooltip: {
            shared: true,
            intersect: false,
            y: {
                formatter: (y: undefined | number) => {
                    if (typeof y !== 'undefined') {
                        return `${y.toFixed(0)} ￥`;
                    }
                    return y;
                }
            }
        }
    });


    return (
        <Box mt={2} sx={{width: "95%"}}>
            <Card>
                <CardHeader title="用户收支统计"
                            subheader="下方显示了您过去你一年的收支情况" />
                <Box sx={{ m: 2}} dir="ltr">
                    {
                        <ReactApexChart
                            type="line"
                            series={CHART_DATA}
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
}