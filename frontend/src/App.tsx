import { BrowserRouter, Route, Switch } from "react-router-dom";
import Appbar from "./components/appbarComponent";
import { Container } from "@mui/material";
import Welcome from "./components/welcomeComponent";
import QuestionList from "./components/orderListComponent";
import OrderCreationWizard from "./components/orderCreationWizard";
import AccountProfile from "./components/profileComponent";
import Login from "./components/loginComponent";
import Register from "./components/registerComponent";
import AnswerList from "./components/answerListComponent";
import Logout from "./components/logoutComponent";
import {useState} from "react";

export default function App() {
    // logout
    const logout = () => {
        setIsAuthenticated(false);
    }

    // login
    const login = () => {
        setIsAuthenticated(true);
    }

    const routes = [
        ["/answerers", <AnswerList type="answerers" />],
        ["/orders", <QuestionList userId={1} />],
        ["/order/create", <OrderCreationWizard answererId={23} />],
        ["/profile", <AccountProfile />],
        ["/login", <Login login={login}/>],
        ["/logout", <Logout logout={logout}/>],
        ["/register", <Register />],
        ["/", <Welcome />],
    ];

    // some app state
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    return (
        <BrowserRouter>
            <Appbar
                isAuthenticated={isAuthenticated}
            />
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
