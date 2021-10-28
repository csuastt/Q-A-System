import axios from "axios";
import { UserInfo } from "./definations";
import userService from "./userService";

class AuthService {
    clearToken() {
        localStorage.removeItem("token");
        delete axios.defaults.headers.common["Authorization"];
    }

    login(username: string, password: string): Promise<UserInfo> {
        return axios
            .post("/user/login", {
                username: username,
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
        return axios.post("/user/logout").finally(this.clearToken);
    }

    register(username: string, email: string, password: string) {
        return axios.post("/users", {
            username: username,
            password: password,
            email: email,
        });
    }

    modifyPassword(id: number, old_password: string, password: string) {
        return axios.put(`/users/${id}/password`, {
            original: old_password,
            password: password,
        });
    }

    refreshToken(): Promise<UserInfo> {
        const storedToken: string | null = localStorage.getItem("token");
        if (storedToken) {
            axios.defaults.headers.common["Authorization"] =
                "Bearer " + storedToken;
            try {
                const user = JSON.parse(atob(storedToken.split(".")[1]));
                return userService.getUserInfo(user["sub"]);
            } catch (e) {
                this.clearToken();
            }
        }
        return Promise.reject(new Error("No token found"));
    }
}

const authService = new AuthService();
export default authService;
