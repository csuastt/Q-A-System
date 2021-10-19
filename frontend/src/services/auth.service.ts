import axios from "axios";
import { UserInfo } from "./definations";
import userService from "./user.service";

class AuthService {
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
                return response.data.user;
            });
    }

    logout() {
        return axios.post("/user/logout").finally(() => {
            localStorage.removeItem("token");
            delete axios.defaults.headers.common["Authorization"];
        });
    }

    register(username: string, email: string, password: string) {
        return axios.post("/users", {
            username: username,
            email: email,
            password: password,
        });
    }

    modifyPassword(id: number, old_password: string, password: string) {
        return axios.put(`/users/${id}/password`, {
            origin: old_password,
            password: password,
        });
    }

    refreshToken() {
        const storedToken: string | null = localStorage.getItem("token");
        if (storedToken) {
            console.log("Found stored token: " + storedToken);
            axios.defaults.headers.common["Authorization"] =
                "Bearer " + storedToken;
            const payload = JSON.parse(atob(storedToken.split(".")[1]));
            const userId = payload.sub;
            console.log("UserId: " + userId);
            return userService.getUserInfo(userId).catch(() => {
                localStorage.removeItem("token");
                delete axios.defaults.headers.common["Authorization"];
                return null;
            });
        }
    }
}

const authService = new AuthService();
export default authService;
