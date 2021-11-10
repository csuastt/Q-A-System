import { BrowserRouter, Route, Switch } from "react-router-dom";
import AuthContext from "./AuthContext";
import AppFrame from "./components/AppFrame";
import { Container } from "@mui/material";
import Welcome from "./components/Welcome";
import OrderCreationWizard from "./components/OrderCreationWizard";
import AccountProfile from "./components/AccountProfile";
import Login from "./components/Login";
import Register from "./components/Register";
import AnswererList from "./components/AnswererList";
import Logout from "./components/Logout";
import React, { useEffect, useState } from "react";
import ChangePassword from "./components/ChangePassword";
import { UserInfo, UserRole } from "./services/definations";
import authService from "./services/authService";
import OrderDetail from "./components/OrderDetail";
import PathParamParser from "./PathParamParser";
import UserOrderList from "./components/UserOrderList";
import IncomeStatistics from "./components/IncomeStatistics";
import Help from "./components/Help";
import { SnackbarProvider } from "notistack";
import NotificationController from "./components/NotificationController";
import websocketService from "./services/websocketService";
import NotificationList from "./components/NotificationList";

export default function App() {
    const [user, setUser] = useState<UserInfo>();
    const [refreshing, setRefreshing] = useState(true);
    const [wsAvailable, setWsAvailable] = useState(false);

    useEffect(() => {
        websocketService.onConnected = () => {
            console.log("WebSocket connection established");
            setWsAvailable(true);
        };
        websocketService.onDisconnected = () => {
            console.log("WebSocket is disconnected");
            setWsAvailable(false);
        };
        authService
            .refreshToken()
            .then(setUser)
            .then(() => websocketService.tryActivate())
            .catch(() => authService.clearToken())
            .finally(() => setRefreshing(false));
    }, []);

    const routes = [
        [
            "/answerers/select",
            <AnswererList selectModel userRole={UserRole.ANSWERER} />,
        ],
        [
            "/answerers",
            <AnswererList
                userRole={UserRole.ANSWERER}
                selectModel={typeof user !== "undefined"}
            />,
        ],
        [
            "/orders/:orderId",
            <PathParamParser
                params={[["orderId", "number"]]}
                C={OrderDetail}
            />,
        ],
        ["/orders", <UserOrderList />],
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
        ["/notif", <NotificationList />],
        ["/income", <IncomeStatistics userId={user?.id} briefMsg={false} />],
        ["/help", <Help />],
        ["/", <Welcome />],
    ];

    return refreshing ? (
        <></>
    ) : (
        <AuthContext.Provider
            value={{
                user: user,
                setUser: setUser,
                clearUser: () => setUser(undefined),
                manager: undefined,
                setManager: () => {},
                clearManager: () => {},
            }}
        >
            <BrowserRouter>
                <SnackbarProvider maxSnack={4}>
                    <NotificationController wsAvailable={wsAvailable}>
                        <AppFrame isAdmin={false}>
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
                    </NotificationController>
                </SnackbarProvider>
            </BrowserRouter>
        </AuthContext.Provider>
    );
}
