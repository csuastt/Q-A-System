import { ManagerInfo } from "./services/definations";
import React from "react";

interface ManagerContextType {
    Manager?: ManagerInfo;
    setManager: (Manager: ManagerInfo) => void;
    clearManager: () => void;
}

const ManagerContext = React.createContext<ManagerContextType>({
    Manager: undefined,
    setManager: () => {},
    clearManager: () => {},
});

export default ManagerContext;
