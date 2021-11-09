import { IMMessage } from "./definations";
import axios from "axios";

class ImHistoryService {
    getOrderIMHistory(orderId: number): Promise<Array<IMMessage>> {
        return axios
            .post(`/im/history/${orderId}`)
            .then((response) => response.data);
    }
}

const imHistoryService = new ImHistoryService();
export default imHistoryService;
