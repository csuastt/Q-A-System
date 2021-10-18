import axios from "axios";

class ManagerService {
    login(manager_name: string, password: string) {
        return axios
            .post("/manager/login", {
                username: manager_name,
                password: password,
            })
            .then((response) => {
                if (response.data.token) {
                    localStorage.setItem("token", response.data.token);
                    localStorage.setItem(
                        "manager",
                        JSON.stringify(response.data.manager)
                    );
                }
                return response.data;
            });
    }

    logout() {
        let tokenConfig = this.managerToken();
        localStorage.removeItem("token");
        localStorage.removeItem("manager");
        return axios.post(
            "/manager/logout",
            {},
            {
                headers: tokenConfig,
            }
        );
    }

    create(manager_name: string, permission: string) {
        return axios.post("/users", {
            managername: manager_name,
            permission: permission,
        });
    }

    getCurrentManager() {
        let manager_raw = localStorage.getItem("manager");
        return manager_raw ? JSON.parse(manager_raw) : null;
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
    managerToken() {
        const storedToken: string | null = localStorage.getItem("token");
        return storedToken ? { Authorization: `Bearer ${storedToken}` } : {};
    }
}

export default new ManagerService();
