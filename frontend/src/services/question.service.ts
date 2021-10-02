import axios from "axios";
import { CreationResult, QuestionInfoList } from "./definations";
import authHeader from "./auth-header";

class QuestionService {
    get_questions_for_user(userId: number): Promise<QuestionInfoList> {
        return axios
            .get("/questions", {
                params: { user: userId },
                headers: authHeader(),
            })
            .then((response) => response.data);
    }

    create_question(
        answererId: number,
        question: string,
        description: string
    ): Promise<CreationResult> {
        return axios
            .put(
                "/question",
                {
                    answerer: answererId,
                    question: question,
                    description: description,
                },
                {
                    headers: authHeader(),
                }
            )
            .then((response) => response.data);
    }
}

export default new QuestionService();
