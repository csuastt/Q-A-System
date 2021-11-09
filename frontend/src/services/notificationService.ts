import { Notification, PagedList } from "./definations";
import axios from "axios";

class NotificationService {
    getNotificationList(
        userId: number,
        hasRead?: boolean,
        page?: number,
        pageSize?: number
    ): Promise<PagedList<Notification>> {
        return axios
            .get(`/users/${userId}/notif`, {
                params: {
                    hasRead,
                    page,
                    pageSize,
                },
            })
            .then((response) => response.data);
    }

    readOne(userId: number, notifId: number): Promise<any> {
        return axios.post(`/users/${userId}/notif/${notifId}/read`);
    }

    readAll(userId: number): Promise<any> {
        return axios.post(`/users/${userId}/notif/readAll`);
    }

    deleteRead(userId: number): Promise<any> {
        return axios.post(`/users/${userId}/notif/deleteRead`);
    }
}

const notificationService = new NotificationService();
export default notificationService;
