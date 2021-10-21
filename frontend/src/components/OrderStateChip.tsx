import { OrderState as QS } from "../services/definations";
import React from "react";
import Chip from "@mui/material/Chip";
import _ from "lodash";

const renderMap: Map<QS, [string, string, string]> = new Map([
    [QS.CREATED, ["已创建", "default", "outlined"]],
    [QS.PAYED, ["已支付", "success", "filled"]],
    [QS.PAY_TIMEOUT, ["支付超时", "warning", "outlined"]],
    [QS.REVIEWED, ["审核通过", "success", "filled"]],
    [QS.REJECTED_BY_REVIEWER, ["审核失败", "error", "outlined"]],
    [QS.ACCEPTED, ["已接单", "success", "filled"]],
    [QS.REJECTED_BY_ANSWERER, ["拒绝接单", "error", "outlined"]],
    [QS.RESPOND_TIMEOUT, ["接单超时", "warning", "outlined"]],
    [QS.ANSWERED, ["已回答", "success", "filled"]],
    [QS.ANSWER_TIMEOUT, ["回答超时", "warning", "outlined"]],
    [QS.CHAT_ENDED, ["交流结束", "default", "filled"]],
    [QS.FULFILLED, ["交易完成", "default", "outlined"]],
]);

const OrderStateChip: React.FC<{ state: QS }> = (props) => {
    const [label, style, variant]: [string, any, any] = _.defaultTo(
        renderMap.get(props.state),
        ["未知", "default", "outlined"]
    );
    return (
        <Chip
            label={label}
            color={style}
            variant={variant}
            sx={{ my: "auto" }}
        />
    );
};

export default OrderStateChip;
