import axios from "axios";
import {CreationResult, OrderInfo, OrderList, UserInfo} from "./definations";
import AuthService from "../services/auth.service";
import ManagerService from "./adminAuthService";

class OrderService {
    getOrdersOfUser(userId: number): Promise<OrderList> {
        return axios
            .get("/orders", {
                headers: AuthService.authToken(),
            })
            .then((response) => response.data);
    }
    modifyOrderInfo(info: OrderInfo) {
        return axios.put(
            `/orders/${info.id}`,
            {
                asker:info.asker,
                answerer:info.answerer,
                state: info.state,
                endReason: info.endReason,
                price: info.price,
                question: info.question,
            },
            {
                headers: ManagerService.managerToken(),
            }
        );
    }
    reviewOrder(orderId: number,accept:boolean){
        return axios.post(
            `/orders/${orderId}/review`,
            {
                description: accept
            },
            {
                headers: ManagerService.managerToken(),
            }
        );
    }

    create_question(
        asker: number,
        answerer: number,
        question: string
    ): Promise<CreationResult> {
        return axios
            .post(
                "/orders",
                {
                    asker: asker,
                    answerer: answerer,
                    question: question,
                },
                {
                    headers: AuthService.authToken(),
                }
            )
            .then((response) => response.data);
    }
}

export default new OrderService();
