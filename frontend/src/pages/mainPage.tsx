import {BrowserRouter, Switch, Route} from "react-router-dom";
import Appbar from "../components/appbarComponent";
import {Container, Typography} from "@mui/material";


export default function MainPage() {
    return (
        <BrowserRouter>
            <Appbar/>
            <Container maxWidth='md'>
                <Switch>
                    <Route path='/'>
                        <Typography>
                            Hello world from MainPage
                        </Typography>
                    </Route>
                </Switch>
            </Container>
        </BrowserRouter>
    );
}