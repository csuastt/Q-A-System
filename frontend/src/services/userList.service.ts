import axios from "axios";
import {UserListItem} from "./userDefination";

const API_URL = "http://localhost:8080/api/users";

class UserListService {
    get(type: string): Promise<Array<UserListItem>> {
        return axios.get(API_URL, {
            params: {
                type
            }
        }).then(response => response.data)
    }
}

export default new UserListService();