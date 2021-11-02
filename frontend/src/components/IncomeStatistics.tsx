import { Card, CardHeader, Box } from '@mui/material';


export default function IncomeStatistics() {

    return (
        <Box mt={2}>
            <Card>
                <CardHeader title="用户收支统计"
                            subheader="下方显示了您过去你一年的收支情况" />
            </Card>
        </Box>
    );
}