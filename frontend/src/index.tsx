import React from "react";
import ReactDOM from "react-dom";
import reportWebVitals from "./reportWebVitals";
import axios from "axios";
import App from "./App";
import AppManage from "./AppManage";
import { BrowserRouter, Route, Switch } from "react-router-dom";
import * as serviceWorkerRegistration from "./serviceWorkerRegistration";

axios.defaults.baseURL = process.env.REACT_APP_API_BASE;

ReactDOM.render(
    <React.StrictMode>
        <BrowserRouter>
            <Switch>
                <Route path="/admins">
                    <AppManage />
                </Route>
                <Route path="/">
                    <App />
                </Route>
            </Switch>
        </BrowserRouter>
    </React.StrictMode>,
    document.getElementById("root")
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://cra.link/PWA
serviceWorkerRegistration.register();
