import React, { useContext, useEffect } from "react";
import authService from "../services/authService";
import { Redirect } from "react-router-dom";

import AuthContext from "../AuthContext";
import adminAuthService from "../services/adminAuthService";

const Logout: React.FC<{
    redirect: string;
    isAdmin: boolean;
}> = (props) => {
    const { clearUser, clearManager } = useContext(AuthContext);
    useEffect(() => {
        if (props.isAdmin) {
            adminAuthService.logout().then(clearManager);
        } else {
            authService.logout().then(clearUser);
        }
    });
    return <Redirect to={props.redirect} />;
};

export default Logout;
