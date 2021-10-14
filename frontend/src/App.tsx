import { BrowserRouter, Route, Switch } from "react-router-dom";
import Appbar from "./components/Appbar";
import { Container } from "@mui/material";
import Welcome from "./components/Welcome";
import QuestionList from "./components/QuestionList";
import OrderCreationWizard from "./components/OrderCreationWizard";
import AccountProfile from "./components/AccountProfile";
import Login from "./components/Login";
import Register from "./components/Register";
import AnswererList from "./components/AnswerList";
import Logout from "./components/Logout";
import { useEffect, useState } from "react";
import authService from "./services/auth.service";
import ChangePassword from "./components/ChangePassword";

export default function App() {
    // logout
    const logout = () => {
        setIsAuthenticated(false);
    };

    // login
    const login = () => {
        setIsAuthenticated(true);
    };

    const routes = [
        ["/answerers/select", <AnswererList selectModel />],
        ["/answerers", <AnswererList />],
        ["/orders", <QuestionList />],
        ["/order/create/:answerer", <OrderCreationWizard />],
        ["/order/create", <OrderCreationWizard />],
        ["/profile", <AccountProfile />],
        ["/login", <Login login={login} />],
        ["/logout", <Logout logout={logout} />],
        ["/register", <Register />],
        ["/change_password", <ChangePassword />],
        ["/", <Welcome />],
    ];

    // some app state
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        const user = authService.getCurrentUser();
        setIsAuthenticated(user !== null);
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
