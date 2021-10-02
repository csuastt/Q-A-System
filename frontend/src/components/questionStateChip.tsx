import { QuestionState as QS } from "../services/definations";
import React from "react";
import Chip from "@mui/material/Chip";
import _ from "lodash";

const renderMap: Map<QS, [string, string]> = new Map([
    [QS.WAITING_FOR_REVIEW, ["等待审核", "warning"]],
    [QS.REJECTED_BY_REVIEWER, ["审核驳回", "error"]],
    [QS.WAITING_TO_BE_ACCEPTED, ["等待接单", "warning"]],
    [QS.REJECTED_BY_ANSWERER, ["拒绝接单", "error"]],
    [QS.WAITING_FOR_INITIAL_ANSWER, ["等待作答", "info"]],
    [QS.COMMUNICATING, ["交流中", "info"]],
    [QS.CANCELLED, ["已取消", "default"]],
    [QS.SOLVED, ["已解决", "success"]],
    [QS.TRANSACTION_COMPLETE, ["交易完成", "default"]],
]);

const QuestionStateChip: React.FC<any & { state: QS }> = (props) => {
    const [label, style] = _.defaultTo(renderMap.get(props.state), [
        "未知",
        "default",
    ]);
    // According to Material-UI document, color accepts any string. However it doesn't in my code.
    return (
        <Chip
            label={label}
            // @ts-ignore
            color={style}
            variant="outlined"
            sx={{ my: "auto" }}
        />
    );
};

export default QuestionStateChip;
