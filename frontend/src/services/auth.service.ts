import axios from "axios";

// temporary local server
const API_URL = "http://localhost:8080/api/user/";

class AuthService {
    login(username: string, password: string) {
        return axios
            .post(API_URL + "login", {
                username: username,
                password: password
            })
            .then(response => {
                if (response.data.token) {
                    localStorage.setItem("user", JSON.stringify(response.data));
                }
                return response.data;
            });
    }

    logout() {
        localStorage.removeItem("user");
    }

    register(username: string, email: string, password: string) {
        return axios.post(API_URL + "register", {
            username: username,
            email: email,
            password: password
        });
    }

    getCurrentUser()  {
        let user_raw = localStorage.getItem('user');
        if (user_raw)
            return JSON.parse(user_raw);
        else
            return null
    }
}

export default new AuthService();
