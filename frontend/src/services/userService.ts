import axios from "axios";
import {
    PagedList,
    UserBasicInfo,
    UserFullyInfo,
    UserInfo,
    UserInfoList,
} from "./definations";

class UserService {
    getUserList(
        answerer: boolean,
        page?: number,
        prePage?: number
    ): Promise<PagedList<UserBasicInfo>> {
        return axios
            .get("/users", {
                params: {
                    role: answerer ? "ANSWERER" : "USER",
                    page: page,
                    pageSize: prePage,
                },
            })
            .then((response) => response.data);
    }

    getUsersByIdList(ids: Array<number>): Promise<UserInfoList> {
        return Promise.all(ids.map((id) => this.getUserInfo(id)));
    }

    applyAnswerer(id: number, description: string, price: number) {
        return axios.post(`/users/${id}/apply`, {
            description: description,
            price: price,
        });
    }

    moneyRecharge(id: number, value: number) {
        return axios.post(`/users/${id}/recharge`, {
            value: value,
        });
    }

    modifyPrice(id: number, price: number) {
        return axios.put(`/users/${id}`, {
            price: price,
        });
    }

    getUserInfo(id: number): Promise<UserInfo> {
        return axios.get(`/users/${id}`).then((response) => response.data);
    }
    getUserFullyInfo(id: number): Promise<UserFullyInfo> {
        return axios.get(`/users/${id}`).then((response) => response.data);
    }
    getUserBasicInfo(id: number): Promise<UserBasicInfo> {
        return axios
            .get(`/users/${id}`)
            .then((response) => response.data);
    }

    modifyUserInfo(info: UserInfo) {
        return axios.put(`/users/${info.id}`, {
            nickname: info.nickname,
            gender: info.gender,
            phone: info.phone,
            description: info.description,
        });
    }
    deleteUser(id: number) {
        return axios.delete(`/users/${id}`, {});
    }
    modifyUserInfoByAdmin(info: UserInfo) {
        return axios.put(`/users/${info.id}`, {
            nickname: info.nickname,
            gender: info.gender,
            phone: info.phone,
            description: info.description,
            price: info.price,
            email: info.email,
            role: info.role,
            balance: info.balance,
        });
    }
}

const userService = new UserService();
export default userService;
