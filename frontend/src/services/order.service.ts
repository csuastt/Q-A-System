import axios from "axios";
import { CreationResult, OrderInfo, OrderList } from "./definations";

class OrderService {
    getOrdersOfUser(userId: number): Promise<OrderList> {
        return axios.get("/orders").then((response) => response.data);
    }

    create_question(
        asker: number,
        answerer: number,
        question: string
    ): Promise<CreationResult> {
        return axios
            .post("/orders", {
                asker: asker,
                answerer: answerer,
                question: question,
            })
            .then((response) => response.data);
    }

    getOrderInfo(orderId: number): Promise<OrderInfo> {
        return axios
            .get(`/orders/${orderId}`)
            .then((response) => response.data);
    }

    modifyOrderInfo(orderId: number, newInfo: OrderInfo): Promise<any> {
        return axios.put(`/orders/${orderId}`, {
            ...newInfo,
            asker: newInfo.asker.id,
            answerer: newInfo.answerer.id,
        });
    }

    reviewOrder(orderId: number, accept: boolean): Promise<any> {
        return axios.post(`/orders/${orderId}/review`, { accept: accept });
    }

    respondOrder(orderId: number, accept: boolean): Promise<any> {
        return axios.post(`/orders/${orderId}/respond`, { accept: accept });
    }

    answerOrder(orderId: number, answer: string): Promise<any> {
        return axios.post(`/orders/${orderId}/answer`, { answer: answer });
    }
}

const orderService = new OrderService();
export default orderService;
