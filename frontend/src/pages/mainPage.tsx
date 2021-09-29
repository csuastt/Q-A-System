import {BrowserRouter, Switch, Route} from "react-router-dom";
import Appbar from "../components/appbarComponent";
import {Container} from "@mui/material";
import Welcome from "../components/welcomeComponent";
import UserList from "../components/userListComponent";
import QuestionList from "../components/questionListComponent";

export default function MainPage() {

    const routes = [
        ["/answerers", <UserList type="answerers"/>],
        ["/questions", <QuestionList userId={1}/>],
        ["/", <Welcome/>]
    ]

    return (
        <BrowserRouter>
            <Appbar/>
            <Container maxWidth='md'>
                <Switch>
                    {routes.map(routeItem => {
                        return (
                            <Route path={routeItem[0].toString()} key={routeItem[0].toString()}>
                                {routeItem[1]}
                            </Route>
                        )
                    })}
                </Switch>
            </Container>
        </BrowserRouter>
    );
}