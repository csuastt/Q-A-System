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
        prePage?: number,
        finished?: boolean
    ): Promise<PagedList<OrderInfo>> {
        return axios
            .get("/orders", {
                params: {
                    asker: asker,
                    answerer: answerer,
                    page: page,
                    pageSize: prePage,
                    finished: finished,
                },
            })
            .then((response) => response.data);
    }
    getOrderListByAdmin(
        state: OrderState,
        page: number = 1,
        pageSize: number = 20
    ): Promise<PagedList<OrderInfo>> {
        return axios
            .get("/orders", {
                params: {
                    state: state,
                    page: page,
                    pageSize: pageSize,
                },
            })
            .then((response) => response.data);
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
