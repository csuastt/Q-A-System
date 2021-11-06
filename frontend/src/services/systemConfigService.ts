import axios from "axios";
import { ConfigInfo } from "./definations";

class SystemConfigService {
    getSystemConfig(): Promise<ConfigInfo> {
        return axios.get("/config").then((response) => response.data);
    }
}

const systemConfigService = new SystemConfigService();
export default systemConfigService;
