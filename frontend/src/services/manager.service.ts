import axios from "axios";
import {ManagerInfo,ManagerRole} from "./definations";

class ManagerService {
    login(manager_name: string, password: string) {
        return axios
            .post("/admins/login", {
                username: manager_name,
                password: password,
            })
            .then((response) => {
                localStorage.setItem("token", response.data.token);
                axios.defaults.headers.common["Authorization"] =
                    "Bearer " + response.data.token;
            })
            .then(this.refreshToken);

    }

    logout() {
        return axios.post("/admins/logout").finally(this.clearToken);
    }

    create(manager_name: string, role: ManagerRole) {
        return axios.post("/admins", {
            username: manager_name,
            role: role,
        });
    }

   

    modifyPassword(id: number, old_password: string, password: string) {
        return axios.put(
            `/managers/${id}/password`,
            {
                origin: old_password,
                password: password,
            },
            {
                headers: this.managerToken(),
            }
        );
    }


    getManagerInfo(id: number): Promise<ManagerInfo> {
        return axios.get(`/admins/${id}`).then((response) => response.data);
    }


    managerToken() {
        const storedToken: string | null = localStorage.getItem("token");
        return storedToken ? { Authorization: `Bearer ${storedToken}` } : {};
    }
    refreshToken(): Promise<ManagerInfo> {
        const storedToken: string | null = localStorage.getItem("token");
        if (storedToken) {
            axios.defaults.headers.common["Authorization"] =
                "Bearer " + storedToken;
            try {
                const manager = JSON.parse(atob(storedToken.split(".")[1]));
                return this.getManagerInfo(manager["sub"]);
            } catch (e) {
                this.clearToken();
            }
        }
        return Promise.reject(new Error("No token found"));
    }

    clearToken() {
        localStorage.removeItem("token");
        delete axios.defaults.headers.common["Authorization"];
    }


}

export default new ManagerService();
