import { ManagerInfo, UserInfo } from "./services/definations";
import React from "react";

interface AuthContextType {
    user?: UserInfo;
    setUser: (user: UserInfo) => void;
    clearUser: () => void;
    manager?: ManagerInfo;
    setManager: (manager: ManagerInfo) => void;
    clearManager: () => void;
}

const AuthContext = React.createContext<AuthContextType>({
    user: undefined,
    setUser: () => {},
    clearUser: () => {},
    manager: undefined,
    setManager: () => {},
    clearManager: () => {},
});

export default AuthContext;
