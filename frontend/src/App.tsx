import { BrowserRouter, Route, Switch } from "react-router-dom";
import UserContext from "./UserContext";
import AppFrame from "./components/AppFrame";
import { Container } from "@mui/material";
import Welcome from "./components/Welcome";
import QuestionList from "./components/QuestionList";
import OrderCreationWizard from "./components/OrderCreationWizard";
import AccountProfile from "./components/AccountProfile";
import Login from "./components/Login";
import Register from "./components/Register";
import AnswererList from "./components/AnswererList";
import Logout from "./components/Logout";
import React, { useEffect, useState } from "react";
import ChangePassword from "./components/ChangePassword";
import { UserInfo } from "./services/definations";
import authService from "./services/authService";
import OrderDetail from "./components/OrderDetail";
import PathParamParser from "./PathParamParser";

export default function App() {
    const [user, setUser] = useState<UserInfo>();
    const [refreshing, setRefreshing] = useState(true);

    useEffect(() => {
        authService
            .refreshToken()
            .then(setUser)
            .finally(() => setRefreshing(false));
    }, []);

    useEffect(() => {
        authService.refreshToken()?.then((usr) => {
            if (usr) {
                setUser(usr);
            }
        });
    }, []);

    const routes = [
        ["/answerers/select", <AnswererList selectModel />],
        ["/answerers", <AnswererList />],
        [
            "/orders/:orderId",
            <PathParamParser
                params={[["orderId", "number"]]}
                C={OrderDetail}
            />,
        ],
        ["/orders", <QuestionList />],
        ["/order/create/:answerer", <OrderCreationWizard />],
        ["/order/create", <OrderCreationWizard />],
        ["/profile", <AccountProfile isAdmin={false} />],
        ["/login", <Login redirect={"/"} isAdmin={false} />],
        ["/logout", <Logout redirect={"/"} isAdmin={false} />],
        ["/register", <Register />],
        [
            "/change_password",
            <ChangePassword
                redirectConfirm={"/logout"}
                redirectCancel={"/profile"}
                isAdmin={false}
            />,
        ],
        ["/", <Welcome />],
    ];

    return refreshing ? (
        <></>
    ) : (
        <UserContext.Provider
            value={{
                user: user,
                setUser: setUser,
                clearUser: () => setUser(undefined),
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
        </UserContext.Provider>
    );
}
