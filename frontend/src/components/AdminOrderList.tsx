import React, {useContext, useEffect, useState} from "react";
import Tab from "@mui/material/Tab";
import TabPanel from "@mui/lab/TabPanel";
import OrderList from "./OrderList";
import AuthContext from "../AuthContext";
import { Redirect } from "react-router-dom";
import { parseIntWithDefault, useQuery } from "../util";
import {OrderInfo, OrderState, PagedList, UserBasicInfo, UserRole} from "../services/definations";
import { TabContext, TabList } from "@mui/lab";
import userService from "../services/userService";
import orderService from "../services/orderService";

const AdminOrderList: React.FC  <{selectModel?: boolean; orderState:OrderState} >= (props)=>{
    const query = useQuery();
    const [OrderList, setOrderList] = useState<Array<OrderInfo>>();
    const [currentPage, setCurrentPage] = useState(
        parseIntWithDefault(query.get("page"), 1)
    );
    const [itemPrePage] = useState(
        parseIntWithDefault(query.get("prepage"), 9)
    );
    const [maxPage, setMaxPage] = useState(currentPage);
    const [totalCount, setTotalCount] = useState(0);

    const acceptOrderList: (list: PagedList<OrderInfo>) => void = (list) => {
        setOrderList(list.data);
        setMaxPage(list.totalPages);
        setTotalCount(list.totalCount);
    };

    useEffect(() => {
        orderService
            .getOrderListByAdmin(
                props.orderState,
                currentPage,
                itemPrePage
            )
            .then(acceptOrderList);
    }, [currentPage, itemPrePage]);




    return (

    );
};

export default AdminOrderList;
