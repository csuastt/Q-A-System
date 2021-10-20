import React, { useContext, useEffect } from "react";
import authService from "../services/authService";
import { Redirect } from "react-router-dom";
import UserContext from "../UserContext";

const Logout: React.FC<{
    redirect: string;
    isAdmin: boolean;
}> = (props) => {
    const { clearUser } = useContext(UserContext);
    useEffect(() => {
        authService.logout().then(clearUser);
    });
    return <Redirect to={props.redirect} />;
};

export default Logout;
