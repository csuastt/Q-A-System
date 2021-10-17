import axios from "axios";
import { UserBasicInfo, UserInfo, UserInfoList } from "./definations";
import AuthService from "../services/auth.service";

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
                headers: AuthService.authToken(),
            })
            .then((response) => response.data["users"]);
    }

    getUsersByIdList(ids: Array<number>): Promise<UserInfoList> {
        return Promise.all(ids.map((id) => this.getUserInfo(id)));
    }

    applyAnswerer(id: number, description: string, price: number) {
        return axios.post(
            `/users/${id}/apply`,
            {
                description: description,
                price: price
            },
            {
                headers: AuthService.authToken(),
            }
        );
    }

    modifyPrice(id: number, price: number) {
        return axios.put(
            `/users/${id}`,
            {
                price: price
            },
            {
                headers: AuthService.authToken(),
            }
        );
    }

    getUserInfo(id: number): Promise<UserInfo> {
        return axios
            .get(`/users/${id}`, { headers: AuthService.authToken() })
            .then((response) => response.data);
    }

    getUserBasicInfo(id: number): Promise<UserBasicInfo> {
        return axios
            .get(`/users/${id}/basic`, { headers: AuthService.authToken() })
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
                headers: AuthService.authToken(),
            }
        );
    }
}

export default new UserService();
