import axios from "axios";
import { ManagerInfo, ManagerRole } from "./definations";
import managerService from "./managerService";

class AdminAuthService {
    login(manager_name: string, password: string) {
        return axios
            .post("/admin/login", {
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
        return axios.post("/admin/logout").finally(this.clearToken);
    }

    create(manager_name: string, role: ManagerRole): Promise<string> {
        return axios
            .post("/admins", {
                username: manager_name,
                role: role,
            })
            .then((response) => response.data["password"]);
    }

    modifyPassword(id: number, old_password: string, password: string) {
        return axios.put(`/admins/${id}/password`, {
            original: old_password,
            password: password,
        });
    }

    refreshToken(): Promise<ManagerInfo> {
        const storedToken: string | null = localStorage.getItem("token");
        if (storedToken) {
            axios.defaults.headers.common["Authorization"] =
                "Bearer " + storedToken;
            try {
                const manager = JSON.parse(atob(storedToken.split(".")[1]));
                return managerService.getManagerInfo(manager["sub"]);
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
const adminAuthService = new AdminAuthService();
export default adminAuthService;
