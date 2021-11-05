import { BrowserRouter, Route, Switch } from "react-router-dom";
import AuthContext from "./AuthContext";
import AppFrame from "./components/AppFrame";
import { Container, createTheme, ThemeProvider } from "@mui/material";
import Login from "./components/Login";
import Create from "./components/Create";
import AnswererList from "./components/AnswererList";
import Logout from "./components/Logout";
import React, { useEffect, useState } from "react";
import ChangePassword from "./components/ChangePassword";
import { ManagerInfo, OrderState, UserRole } from "./services/definations";
import adminAuthService from "./services/adminAuthService";
import ReviewList from "./components/ReviewList";
import HelloAdmin from "./components/HelloAdmin";
import AdminOrderList from "./components/AdminOrderList";
import { Theme } from "@mui/material/styles";

const managerTheme: Theme = createTheme({
    palette: {
        primary: {
            main: "#7b1fa2",
        },
        secondary: {
            main: "#00838f",
        },
        warning: {
            main: "#f57c00",
        },
        error: {
            main: "#c62828",
        },
    },
});

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
        ["/admins/answerers", <AnswererList userRole={UserRole.ANSWERER} />],
        ["/admins/users", <AnswererList userRole={UserRole.USER} />],

        ["/admins/orders", <AdminOrderList />],
        // [
        //     "/admins/orders/:orderId",
        //     <PathParamParser
        //         params={[["orderId", "number"]]}
        //         C={OrderDetailForAdmin}
        //     />,
        // ],
        ["/admins/review", <ReviewList />],

        ["/admins/login", <Login redirect={"/admins/"} isAdmin={true} />],
        ["/admins/logout", <Logout redirect={"/admins/"} isAdmin={true} />],
        ["/admins/create", <Create />],
        [
            "/admins/change_password",
            <ChangePassword
                redirectConfirm={"/admins/logout"}
                redirectCancel={"/admins"}
                isAdmin={true}
            />,
        ],
        ["/admins/", <HelloAdmin />],
    ];

    return refreshing ? (
        <></>
    ) : (
        <AuthContext.Provider
            value={{
                manager: manager,
                setManager: setManager,
                clearManager: () => setManager(undefined),
                user: undefined,
                setUser: () => {},
                clearUser: () => {},
            }}
        >
            <ThemeProvider theme={managerTheme}>
                <BrowserRouter>
                    <AppFrame isAdmin={true}>
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
            </ThemeProvider>
        </AuthContext.Provider>
    );
}
