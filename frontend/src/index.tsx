import React from 'react';
import ReactDOM from 'react-dom';
import reportWebVitals from './reportWebVitals';
import * as serviceWorker from "./serviceWorker";
import MainPage from './pages/mainPage'

ReactDOM.render(
    <React.StrictMode>
        <MainPage/>
    </React.StrictMode>,
    document.getElementById('root')
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();

// unregister service worker
serviceWorker.unregister();
