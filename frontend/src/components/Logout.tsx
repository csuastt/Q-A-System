import React from "react";
import authService from "../services/auth.service";
import { Redirect } from "react-router-dom";
import ManagerService from "../services/adminAuthService";

const Logout: React.FC<{
    logout: () => void;
    redirect: string;
    isAdmin: boolean;
}> = (props) => {
    if (props.isAdmin) {
        // add the service code for admin
        ManagerService.logout().then(() => {
            props.logout();
        });
    } else {
        authService.logout().then(() => {
            props.logout();
        });
    }

    return <Redirect to={props.redirect} />;
};

export default Logout;
