import { BrowserRouter, Route, Switch } from "react-router-dom";
import Appbar from "./components/Appbar";
import { Container } from "@mui/material";
import Welcome from "./components/Welcome";
import QuestionList from "./components/QuestionList";
import OrderCreationWizard from "./components/OrderCreationWizard";
import AccountProfile from "./components/AccountProfile";
import Login from "./components/Login";
import Register from "./components/Register";
import AnswererList from "./components/AnswererList";
import Logout from "./components/Logout";
import { useEffect, useState } from "react";
import authService from "./services/auth.service";
import ChangePassword from "./components/ChangePassword";
import ManagerService from "./services/manager.service";

export default function AppManage() {
    // logout
    const managerLogout = () => {
        setIsAuthenticated(false);
    };

    // login
    const managerLogin = () => {
        setIsAuthenticated(true);
    };

    // todo also need to add login & logout function for the admin

    const routes = [
        // ["/answerers/select", <AnswererList selectModel />],
        // ["/answerers", <AnswererList />],
        // ["/orders", <QuestionList />],
        // ["/order/create/:answerer", <OrderCreationWizard />],
        // ["/order/create", <OrderCreationWizard />],
        // ["/profile", <AccountProfile isAdmin={false} />],
        ["/manager/login", <Login login={managerLogin} redirect={"/"} isAdmin={true} />],
        ["/manager/logout", <Logout logout={managerLogout} redirect={"/"} isAdmin={true} />],
        //["/register", <Register />],
        // [
        //     "/change_password",
        //     <ChangePassword
        //         redirectConfirm={"/logout"}
        //         redirectCancel={"/profile"}
        //         isAdmin={false}
        //     />,
        // ],
        // ["/", <Welcome />],
    ];

    // some app state
    // note: this authentication is of the user
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    // todo add authentication state for the admin

    useEffect(() => {
        const manager = ManagerService.getCurrentManager();
        setIsAuthenticated(manager !== null);
    }, []);

    return (
        <BrowserRouter>
            <Appbar isAuthenticated={isAuthenticated} />
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
        </BrowserRouter>
    );
}
