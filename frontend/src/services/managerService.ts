import axios from "axios";
import {
    ManagerInfo,
    ManagerInfoList,
    ManagerRole,
    ManagerStatsInfo,
    PagedList,
} from "./definations";

class ManagerService {
    getManagerList(
        reviewer: boolean,
        page?: number,
        prePage?: number
    ): Promise<PagedList<ManagerInfo>> {
        return axios
            .get("/admins", {
                params: {
                    reviewer: reviewer,
                    page: page,
                    pageSize: prePage,
                },
            })
            .then((response) => response.data);
    }
    getAllManagerList(
        page?: number,
        prePage?: number
    ): Promise<PagedList<ManagerInfo>> {
        return axios
            .get("/admins", {
                params: {
                    page: page,
                    pageSize: prePage,
                },
            })
            .then((response) => response.data);
    }
    modifyManagerRole(id: number, role: ManagerRole) {
        return axios.put(`/admins/${id}`, {
            role: role,
        });
    }
    getManagerByIdList(ids: Array<number>): Promise<ManagerInfoList> {
        return Promise.all(ids.map((id) => this.getManagerInfo(id)));
    }

    getManagerInfo(id: number): Promise<ManagerInfo> {
        return axios.get(`/admins/${id}`).then((response) => response.data);
    }

    deleteManager(id: number) {
        return axios.delete(`/admins/${id}`, {});
    }
    getManagerStats(id: number): Promise<ManagerStatsInfo> {
        return axios
            .get(`/admins/${id}/stats`)
            .then((response) => response.data);
    }
}
const managerService = new ManagerService();
export default managerService;
