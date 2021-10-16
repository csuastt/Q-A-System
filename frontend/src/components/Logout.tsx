import React from "react";
import authService from "../services/auth.service";
import { Redirect } from "react-router-dom";

const Logout: React.FC<{
    logout: () => void;
    redirect: string;
    isAdmin: boolean;
}> = (props) => {
    if (props.isAdmin) {
        // todo
        // add the service code for admin
    } else {
        authService.logout().then(() => {
            props.logout();
        });
    }

    return <Redirect to={props.redirect} />;
};

export default Logout;
