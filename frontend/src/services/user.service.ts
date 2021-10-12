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
                nickname: info.nickname,
                gender: info.gender,
                phone: info.phone,
                description: info.description,
            },
            {
                headers: authToken(),
            }
        );
    }
}

export default new UserService();
