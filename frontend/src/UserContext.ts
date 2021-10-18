import { UserInfo } from "./services/definations";
import React from "react";

interface UserContextType {
    user?: UserInfo;
    setUser: (user: UserInfo) => void;
    clearUser: () => void;
}

const UserContext = React.createContext<UserContextType>({
    user: undefined,
    setUser: () => {},
    clearUser: () => {},
});

export default UserContext;
