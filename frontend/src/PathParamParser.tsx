import React from "react";
import { useParams } from "react-router-dom";

const PathParamParser: React.FC<{
    params: [string, string][];
    C: React.FC<any>;
}> = (props) => {
    const parsed: any = {};
    const params = useParams<any>();
    for (let [key, type] of props.params) {
        let p = params[key];
        if (type === "number") {
            parsed[key] = Number(p);
        } else if (type === "string") {
            parsed[key] = p;
        }
    }
    return (
        <props.C
            // Spread parsed params
            {...parsed}
        />
    );
};

export default PathParamParser;
