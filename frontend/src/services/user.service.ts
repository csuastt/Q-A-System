import axios from "axios";
import { UserBasicInfo, UserInfoList } from "./definations";

class UserService {
    get_users_of_type(type: string): Promise<UserInfoList> {
        return axios
            .get("/users", { params: { type } })
            .then((response) => response.data);
    }

    get_users_by_id_list(ids: Array<number>): Promise<UserInfoList> {
        return Promise.all(ids.map((id) => this.get_user_basic_info(id)));
    }

    get_user_basic_info(id: number): Promise<UserBasicInfo> {
        return axios.get(`/user/${id}/basic`).then((response) => response.data);
    }
}

export default new UserService();
