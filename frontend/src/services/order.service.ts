import axios from "axios";
import { CreationResult, OrderList } from "./definations";
import authToken from "./auth.token";

class OrderService {
    getOrdersOfUser(userId: number): Promise<OrderList> {
        return axios
            .get("/orders", {
                headers: authToken(),
            })
            .then((response) => response.data);
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
                    headers: authToken(),
                }
            )
            .then((response) => response.data);
    }
}

export default new OrderService();
