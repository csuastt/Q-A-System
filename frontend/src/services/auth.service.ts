import axios from "axios";
import {UserInfo} from "../components/profileComponent";

// temporary local server
const API_URL = "http://localhost:8080/api/user/";

class AuthService {
    // Here are some request maker
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

    logout(username: string) {
        localStorage.removeItem("user");
        return axios.post(API_URL + "logout", {
            username: username,
        });
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

    modifyUserInfo(info: UserInfo) {
        return axios.put(API_URL + info.username + "/modify/info", {
            nickname: info.nickname,
            gender: info.gender,
            phone: info.phone,
            description: info.description
        });
    }

    modifyPassword(username: string, old_password: string, password: string) {
        return axios.put(API_URL + username + "/modify/password", {
            origin: old_password,
            password: password
        });
    }
}

export default new AuthService();
