import axios from "axios";
import {
    AttachmentInfo,
    CreationResult,
    OrderInfo,
    OrderState,
    PagedList,
    SortDirection,
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
        page: number,
        pageSize: number
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

    getAllOrderListByAdmin(
        page: number,
        pageSize: number,
        sortDirection?: SortDirection,
        state?: OrderState
    ): Promise<PagedList<OrderInfo>> {
        return axios
            .get("/orders", {
                params: {
                    page: page,
                    pageSize: pageSize,
                    sortDirection: sortDirection,
                    state: state,
                },
            })
            .then((response) => response.data);
    }

    createQuestion(
        asker: number,
        answerer: number,
        questionTitle: string,
        questionDescription: string,
        showPublic: boolean
    ): Promise<CreationResult> {
        return axios
            .post("/orders", {
                asker: asker,
                answerer: answerer,
                title: questionTitle,
                description: questionDescription,
                showPublic: showPublic
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

    endOrder(orderId: number): Promise<any> {
        return axios.post(`/orders/${orderId}/end`);
    }

    uploadAttachment(orderId: number, file: File): Promise<any> {
        const formData = new FormData();
        formData.append("file", file);
        return axios.post(`/orders/${orderId}/attachments`, formData, {
            headers: {
                "content-type": "multipart/form-data",
            },
        });
    }

    getAttachments(orderId: number): Promise<Array<AttachmentInfo>> {
        return axios
            .get(`/orders/${orderId}/attachments`)
            .then((response) => response.data);
    }

    getAttachmentUrl(orderId: number, uuid: number) {
        return (
            axios.defaults.baseURL + `/orders/${orderId}/attachments/${uuid}`
        );
    }
}

const orderService = new OrderService();
export default orderService;
