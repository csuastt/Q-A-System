import React, { useState } from "react";
import { ConfigInfo } from "../services/definations";
import _ from "lodash";
import Chip from "@mui/material/Chip";
import { Card } from "@mui/material";
import CardHeader from "@mui/material/CardHeader";

const SystemSettings: React.FC<{}> = (props) => {
    const [systemConfig, setSystemConfig] = useState<ConfigInfo>();

    return (
        <Card>
            <CardHeader subheader="编辑并保存系统参数" title="系统参数" />
        </Card>
    );
};

export default SystemSettings;
