import React from "react";
import authService from "../services/auth.service";
import { Redirect } from "react-router-dom";

const Logout: React.FC = (props) => {
    authService.logout();
    return <Redirect to="/" />;
};

export default Logout;
