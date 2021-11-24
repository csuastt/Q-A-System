import axios from "axios";
import { AdminStatsInfo, ConfigInfo, EarningsInfo } from "./definations";

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
            askerFeeRate: info.askerFeeRate,
        });
    }

    getSystemEarnings(): Promise<EarningsInfo> {
        return axios.get(`/config/earnings`).then((response) => response.data);
    }

    getAdminStats(): Promise<AdminStatsInfo> {
        return axios.get("/config/stats").then((response) => response.data);
    }
}

const configService = new ConfigService();
export default configService;
