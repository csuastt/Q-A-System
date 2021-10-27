import { BrowserRouter, Route, Switch } from "react-router-dom";
import ManagerContext from "./ManagerContext";
import AppFrame from "./components/AppFrame";
import { Container } from "@mui/material";
import Welcome from "./components/Welcome";
import QuestionList from "./components/QuestionList";
import OrderCreationWizard from "./components/OrderCreationWizard";
import AccountProfile from "./components/AccountProfile";
import Login from "./components/Login";
import Create from "./components/Create";
import AnswererList from "./components/AnswererList";
import Logout from "./components/Logout";
import React, { useEffect, useState } from "react";
import ChangePassword from "./components/ChangePassword";
import { ManagerInfo } from "./services/definations";
import adminAuthService from "./services/adminAuthService";
import OrderDetail from "./components/OrderDetail";
import PathParamParser from "./PathParamParser";
import ReviewList from "./components/ReviewList";

export default function AppManage() {
    const [manager, setManager] = useState<ManagerInfo>();
    const [refreshing, setRefreshing] = useState(true);

    useEffect(() => {
        adminAuthService
            .refreshToken()
            .then(setManager)
            .catch(() => adminAuthService.clearToken())
            .finally(() => setRefreshing(false));
    }, []);

    const routes = [
        ["/admins/answerers/select", <AnswererList selectModel />],
        ["/admins/answerers", <AnswererList />],
        [
            "/admins/orders/:orderId",
            <PathParamParser
                params={[["orderId", "number"]]}
                C={OrderDetail}
            />,
        ],
        ["/admins/orders", <QuestionList />],

        ["/admins/profile", <AccountProfile isAdmin={true} />],

        ["/admins/review", <ReviewList />],
        ["/admins/login", <Login redirect={"/"} isAdmin={true} />],
        ["/admins/logout", <Logout redirect={"/"} isAdmin={true} />],
        ["/admins/create", <Create />],
        [
            "/admins/change_password",
            <ChangePassword
                redirectConfirm={"/logout"}
                redirectCancel={"/profile"}
                isAdmin={true}
            />,
        ],
        ["/admins/", <Welcome />],
    ];

    return refreshing ? (
        <></>
    ) : (
        <ManagerContext.Provider
            value={{
                manager: manager,
                setManager: setManager,
                clearManager: () => setManager(undefined),
            }}
        >
            <BrowserRouter>
                <AppFrame>
                    <Container maxWidth="md">
                        <Switch>
                            {routes.map((routeItem) => {
                                return (
                                    <Route
                                        path={routeItem[0].toString()}
                                        key={routeItem[0].toString()}
                                    >
                                        {routeItem[1]}
                                    </Route>
                                );
                            })}
                        </Switch>
                    </Container>
                </AppFrame>
            </BrowserRouter>
        </ManagerContext.Provider>
    );
}
