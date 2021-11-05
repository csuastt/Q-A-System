import axios from "axios";
import { ManagerInfo, ManagerInfoList, PagedList } from "./definations";

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

    getManagerByIdList(ids: Array<number>): Promise<ManagerInfoList> {
        return Promise.all(ids.map((id) => this.getManagerInfo(id)));
    }

    getManagerInfo(id: number): Promise<ManagerInfo> {
        return axios.get(`/admins/${id}`).then((response) => response.data);
    }

    modifyManagerInfo(info: ManagerInfo) {
        return axios.put(`/admins/${info.id}`, {
            password: info.password,
            role: info.role,
        });
    }

    deleteManager(id: number) {
        return axios.delete(`/admins/${id}`, {});
    }
}
const managerService = new ManagerService();
export default managerService;
