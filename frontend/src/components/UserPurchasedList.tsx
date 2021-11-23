import React, { useContext } from "react";
import OrderList from "./OrderList";
import AuthContext from "../AuthContext";
import { Redirect } from "react-router-dom";
import { parseIntWithDefault, useQuery } from "../util";

const UserPurchasedList: React.FC = () => {
    const { user } = useContext(AuthContext);
    const query = useQuery();

    if (user == null) {
        return <Redirect to={"/login"} />;
    }

    const currentPage = parseIntWithDefault(query.get("page"), 1);
    const itemPrePage = parseIntWithDefault(query.get("prepage"), 10);

    return (
        <OrderList
            userId={user.id}
            itemPrePage={itemPrePage}
            initCurrentPage={currentPage}
            purchased={true}
        />
    );
};

export default UserPurchasedList;
