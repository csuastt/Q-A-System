import axios from "axios";
import { ConfigInfo } from "./definations";

class ConfigService {
    getSystemConfig(): Promise<ConfigInfo> {
        return axios.get("/config").then((response) => response.data);
    }
}

const configService = new ConfigService();
export default configService;
