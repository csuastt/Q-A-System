import axios from "axios";
import {QuestionInfoList} from "./definations";

class QuestionService {

    get_questions_for_user(userId: number): Promise<QuestionInfoList> {
        return axios.get("/questions", {params: {'user': userId}}).then(response => response.data)
    }

}

export default new QuestionService();