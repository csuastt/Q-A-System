import { ManagerInfo } from "./services/definations";
import React from "react";

interface ManagerContextType {
    manager?: ManagerInfo;
    setManager: (manager: ManagerInfo) => void;
    clearManager: () => void;
}

const ManagerContext = React.createContext<ManagerContextType>({
    manager: undefined,
    setManager: () => {},
    clearManager: () => {},
});

export default ManagerContext;
