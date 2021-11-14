import { IMMessage } from "./definations";
import axios from "axios";
import websocketService from "./websocketService";

class IMService {
    getOrderIMHistory(orderId: number): Promise<Array<IMMessage>> {
        return axios
            .get(`/im/history/${orderId}`)
            .then((response) => response.data);
    }

    sendMessage(orderId: number, msg: Omit<IMMessage, "messageId">) {
        websocketService.stompClient.publish({
            destination: `/im/send/${orderId}`,
            body: JSON.stringify(msg),
        });
    }
}

const imService = new IMService();
export default imService;
