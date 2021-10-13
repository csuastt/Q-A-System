import axios from "axios";
import { UserBasicInfo, UserInfo, UserInfoList } from "./definations";
import authToken from "./auth-token";

class UserService {
    getUserList(
        answerer: boolean,
        page: number = 1,
        limit: number = 20
    ): Promise<Array<UserBasicInfo>> {
        return axios
            .get("/users", {
                params: {
                    answerer: answerer,
                    page: page,
                    limit: limit,
                },
                headers: authToken(),
            })
            .then((response) => response.data['users']);
    }

    getUsersByIdList(ids: Array<number>): Promise<UserInfoList> {
        return Promise.all(ids.map((id) => this.getUserInfo(id)));
    }

    getUserInfo(id: number): Promise<UserInfo> {
        return axios
            .get(`/users/${id}`, { headers: authToken() })
            .then((response) => response.data);
    }

    getUserBasicInfo(id: number): Promise<UserBasicInfo> {
        return axios
            .get(`/users/${id}/basic`)
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
