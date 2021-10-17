import axios from "axios";

class AuthService {
    login(username: string, password: string) {
        return axios
            .post("/user/login", {
                username: username,
                password: password,
            })
            .then((response) => {
                if (response.data.token) {
                    localStorage.setItem("token", response.data.token);
                    localStorage.setItem(
                        "user",
                        JSON.stringify(response.data.user)
                    );
                }
                return response.data;
            });
    }

    logout() {
        let tokenConfig = this.authToken();
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        return axios.post(
            "/user/logout",
            {},
            {
                headers: tokenConfig,
            }
        );
    }

    register(username: string, email: string, password: string) {
        return axios.post("/users", {
            username: username,
            email: email,
            password: password,
        });
    }

    getCurrentUser() {
        let user_raw = localStorage.getItem("user");
        return user_raw ? JSON.parse(user_raw) : null;
    }

    modifyPassword(id: number, old_password: string, password: string) {
        return axios.put(
            `/users/${id}/password`,
            {
                origin: old_password,
                password: password,
            },
            {
                headers: this.authToken(),
            }
        );
    }

    authToken() {
        const storedToken: string | null = localStorage.getItem("token");
        return storedToken ? { Authorization: `Bearer ${storedToken}` } : {};
    }
}

export default new AuthService();
