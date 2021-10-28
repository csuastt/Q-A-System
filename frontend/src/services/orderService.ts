import axios from "axios";
import {
    CreationResult,
    OrderInfo,
    OrderState,
    PagedList,
} from "./definations";

class OrderService {
    getOrdersOfUser(
        asker?: number,
        answerer?: number,
        page?: number,
        prePage?: number
    ): Promise<PagedList<OrderInfo>> {
        return axios
            .get("/orders", {
                params: {
                    asker: asker,
                    answerer: answerer,
                    page: page,
                    pageSize: prePage,
                },
            })
            .then((response) => response.data);
    }
    getOrderListByUser(
        asker: boolean,
        answerer: boolean,
        page: number = 1,
        limit: number = 20
    ): Promise<Array<OrderInfo>> {
        return axios
            .get("/orders", {
                params: {
                    asker: asker,
                    answerer: answerer,
                    page: page,
                    limit: limit,
                },
            })
            .then((response) => response.data["orders"]);
    }
    getOrderListByAdmin(
        state: OrderState,
        page: number = 1,
        limit: number = 20
    ): Promise<Array<OrderInfo>> {
        return axios
            .get("/users", {
                params: {
                    state: state,
                    page: page,
                    limit: limit,
                },
            })
            .then((response) => response.data["users"]);
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
