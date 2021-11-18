import { OrderState as QS, OrderStateMsg } from "../services/definations";
import React from "react";
import Chip from "@mui/material/Chip";
import _ from "lodash";

const renderMap: Map<QS, [string, string]> = new Map([
    [QS.CREATED, ["default", "outlined"]],
    [QS.PAYED, ["success", "filled"]],
    [QS.PAY_TIMEOUT, ["warning", "outlined"]],
    [QS.REVIEWED, ["success", "filled"]],
    [QS.REJECTED_BY_REVIEWER, ["error", "outlined"]],
    [QS.ACCEPTED, ["success", "filled"]],
    [QS.REJECTED_BY_ANSWERER, ["error", "outlined"]],
    [QS.RESPOND_TIMEOUT, ["warning", "outlined"]],
    [QS.ANSWERED, ["success", "filled"]],
    [QS.ANSWER_TIMEOUT, ["warning", "outlined"]],
    [QS.CHAT_ENDED, ["default", "filled"]],
    [QS.FULFILLED, ["default", "outlined"]],
    [QS.CANCELLED, ["default", "outlined"]],
]);

const OrderStateChip: React.FC<{ state: QS }> = (props) => {
    const [style, variant]: [string, string] = _.defaultTo(
        renderMap.get(props.state),
        ["default", "outlined"]
    );
    return (
        <Chip
            label={_.defaultTo(OrderStateMsg.get(props.state), "未知")}
            color={style as any}
            variant={variant as any}
            sx={{ my: "auto" }}
        />
    );
};

export default OrderStateChip;
