import axios from "axios";
import { CreationResult, OrderList } from "./definations";
import AuthService from "../services/auth.service";
import ManagerService from "../services/manager.service";

class OrderService {
    getOrdersOfUser(userId: number): Promise<OrderList> {
        return axios
            .get("/orders", {
                headers: AuthService.authToken(),
            })
            .then((response) => response.data);
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
