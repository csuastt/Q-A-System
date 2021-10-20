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
import UserList from "./components/UserList";
import ReviewList from "./components/ReviewList";
//import ReviewList from "./components/ReviewList";
import Logout from "./components/Logout";
import Create from "./components/Create";
import { useEffect, useState } from "react";
import authService from "./services/auth.service";
import ChangePassword from "./components/ChangePassword";
import ManagerService from "./services/manager.service";
import ManagerProfile from "./components/ManagerProfile";


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
        ["/manager/answerers", <AnswererList />],
        ["/manager/users", <UserList />],
        ["/manager/review_orders", <ReviewList />],





        ["/manager/profile", <AccountProfile isAdmin={true} />],

        ["/manager/manager_profile", <ManagerProfile />],

        ["/manager/login", <Login login={managerLogin} redirect={"/"} isAdmin={true} />],
        ["/manager/logout", <Logout logout={managerLogout} redirect={"/"} isAdmin={true} />],
        ["/manager/create", <Create />],
        [
            "/manager/change_password",
            <ChangePassword
                redirectConfirm={"/manager/logout"}
                redirectCancel={"/manager/manager_profile"}
                isAdmin={true}
            />,
        ],
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
