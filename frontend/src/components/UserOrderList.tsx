import React, { useContext, useState } from "react";
import Tab from "@mui/material/Tab";
import TabPanel from "@mui/lab/TabPanel";
import OrderList from "./OrderList";
import AuthContext from "../AuthContext";
import { Redirect } from "react-router-dom";
import { parseIntWithDefault, useQuery } from "../util";
import { UserRole } from "../services/definations";
import { TabContext, TabList } from "@mui/lab";

const UserOrderList: React.FC = () => {
    const { user } = useContext(AuthContext);
    const query = useQuery();
    const [tabValue, setTabValue] = useState("inProgress");
    const handleTabChange = (event: React.SyntheticEvent, newValue: string) => {
        setTabValue(newValue);
    };

    if (user == null) {
        return <Redirect to={"/login"} />;
    }

    const showAnswerer =
        user.role === UserRole.ANSWERER && query.get("answerer") === "true";
    const currentPage = parseIntWithDefault(query.get("page"), 1);
    const itemPrePage = parseIntWithDefault(query.get("prepage"), 20);

    const OrderListWrapper: React.FC<{ finished: boolean }> = (props) => (
        <OrderList
            userId={user.id}
            showAnswerer={showAnswerer}
            itemPrePage={itemPrePage}
            initCurrentPage={currentPage}
            filterFinished={props.finished}
        />
    );

    return (
        <TabContext value={tabValue}>
            <TabList onChange={handleTabChange}>
                <Tab label={"进行中的订单"} value={"inProgress"} />
                <Tab label={"已完成的订单"} value={"finished"} />
            </TabList>
            <TabPanel value={"inProgress"}>
                <OrderListWrapper finished={false} />
            </TabPanel>
            <TabPanel value={"finished"}>
                <OrderListWrapper finished={true} />
            </TabPanel>
        </TabContext>
    );
};

export default UserOrderList;
