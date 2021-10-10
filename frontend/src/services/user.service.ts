import axios from "axios";
import { UserInfo, UserInfoList } from "./definations";
import authToken from "./auth-token";

class UserService {
    getAnswerers(): Promise<UserInfoList> {
        return axios
            .get("/users", { params: { answerer: true }, headers: authToken() })
            .then((response) => response.data.users);
    }

    getUsersByIdList(ids: Array<number>): Promise<UserInfoList> {
        return Promise.all(ids.map((id) => this.getUserInfo(id)));
    }

    getUserInfo(id: number): Promise<UserInfo> {
        return axios
            .get(`/users/${id}`, { headers: authToken() })
            .then((response) => response.data);
    }

    modifyUserInfo(info: UserInfo) {
        return axios.put(
            `/users/${info.id}`,
            {
                username: info.username,
                email: info.email,
                gender: info.gender,
                birthday: info.birthday,
            },
            {
                headers: authToken(),
            }
        );
    }
}

export default new UserService();
