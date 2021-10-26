import axios from "axios";
import {ManagerInfo,ManagerInfoList} from "./definations";

class ManagerService {
    getManagerList(
        reviewer: boolean,
        page: number = 1,
        limit: number = 20
    ): Promise<Array<ManagerInfo>> {
        return axios
            .get("/admins", {
                params: {
                    reviewer: reviewer,
                    page: page,
                    limit: limit,
                },
            })
            .then((response) => response.data["admins"]);
    }

    getManagerByIdList(ids: Array<number>): Promise<ManagerInfoList> {
        return Promise.all(ids.map((id) => this.getManagerInfo(id)));
    }

    getManagerInfo(id: number): Promise<ManagerInfo> {
        return axios.get(`/admins/${id}`).then((response) => response.data);
    }





}
const managerService = new ManagerService();
export default new ManagerService();
