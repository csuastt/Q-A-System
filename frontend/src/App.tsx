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
import React, { useState } from "react";
import ChangePassword from "./components/ChangePassword";
import { UserInfo } from "./services/definations";
import PathParamParser from "./PathParamParser";
import OrderDetail from "./components/OrderDetail";

export default function App() {
    const [user, setUser] = useState<UserInfo>();

    const routes = [
        ["/answerers/select", <AnswererList selectModel />],
        ["/answerers", <AnswererList />],
        ["/orders", <QuestionList />],
        [
            "/orders/:orderId",
            <PathParamParser
                params={[["orderId", "number"]]}
                C={OrderDetail}
            />,
        ],
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

    return (
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
