import axios from "axios";
import { ConfigInfo, EarningsInfo, UserInfo } from "./definations";

class ConfigService {
    getSystemConfig(): Promise<ConfigInfo> {
        return axios.get("/config").then((response) => response.data);
    }
    modifyConfigInfo(info: ConfigInfo) {
        return axios.put(`/config`, {
            minPrice: info.minPrice,
            maxPrice: info.maxPrice,
            respondExpirationSeconds: info.respondExpirationSeconds,
            answerExpirationSeconds: info.answerExpirationSeconds,
            fulfillExpirationSeconds: info.fulfillExpirationSeconds,
            maxChatMessages: info.maxChatMessages,
            maxChatTimeSeconds: info.maxChatTimeSeconds,
            feeRate: info.feeRate,
        });
    }
    getSystemEarnings(): Promise<EarningsInfo> {
        return axios.get(`/config/earnings`).then((response) => response.data);
    }
}

const configService = new ConfigService();
export default configService;
