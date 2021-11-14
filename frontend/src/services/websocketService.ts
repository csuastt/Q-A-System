import { Client, StompSubscription } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { IMMessage, Notification } from "./definations";
import notificationService from "./notificationService";

const websocketEndpoint = process.env.REACT_APP_API_BASE + "/ws";

class WebsocketService {
    stompClient: Client;
    sessionUserId: number;
    notifSubscription: StompSubscription | null;
    imSubscription: StompSubscription | null;
    orderToBeSubscribed: number;

    onConnected: () => void = () => null;
    onDisconnected: () => void = () => null;
    onNewNotification: (notif: Notification) => boolean = () => false;
    onNewMessage: (newMsg: IMMessage) => void = () => null;

    constructor() {
        this.stompClient = new Client();
        this.sessionUserId = -1;
        this.notifSubscription = null;
        this.imSubscription = null;
        this.orderToBeSubscribed = -1;

        // @ts-ignore
        this.stompClient.webSocketFactory = function () {
            return new SockJS(websocketEndpoint);
        };
        this.stompClient.onConnect = () => {
            this.onConnected();
            // Auto subscribe to notification
            this.subscribeNotification();
            // Auto subscribe to delayed im order
            if (this.orderToBeSubscribed >= 0) {
                this.subscribeIM(this.orderToBeSubscribed);
            }
        };
        this.stompClient.onDisconnect = () => {
            this.onDisconnected();
        };
        this.stompClient.onStompError = (receipt) => {
            const errBody = receipt.body;
            console.log("WebSocket disconnected due to error: " + errBody);
            if (errBody.search("No auth token") !== -1) {
                console.log(
                    "WebSocket authenticated invalid. Do not try to reconnect."
                );
                this.deactivate();
            }
        };
    }

    tryActivate() {
        let ok = false;
        const storedToken: string | null = localStorage.getItem("token");
        if (storedToken) {
            const authToken = "Bearer " + storedToken;
            const userId = JSON.parse(atob(storedToken.split(".")[1]))["sub"];
            if (userId) {
                this.sessionUserId = userId;
                this.stompClient.connectHeaders = {
                    Authorization: authToken,
                };
                this.stompClient.activate();
                ok = true;
            }
        }
        console.log(
            ok
                ? "Activating WebSocket"
                : "Cannot find valid auth token. Will not activate WebSocket."
        );
    }

    deactivate() {
        this.notifSubscription?.unsubscribe();
        this.imSubscription?.unsubscribe();
        this.notifSubscription = null;
        this.imSubscription = null;
        this.stompClient.deactivate();
        this.sessionUserId = -1;
    }

    subscribeNotification(): boolean {
        if (!this.stompClient.connected) {
            return false;
        }
        this.notifSubscription?.unsubscribe();
        this.notifSubscription = this.stompClient.subscribe(
            `/notif/${this.sessionUserId}`,
            (frame) => {
                const notif: Notification = JSON.parse(frame.body);
                if (this.onNewNotification(notif)) {
                    // Auto read
                    notificationService
                        .readOne(this.sessionUserId, notif.notifId)
                        .catch((reason) =>
                            console.log(
                                `Cannot auto read notification[id=${notif.notifId}]: ${reason}`
                            )
                        );
                }
            }
        );
        return true;
    }

    unsubscribeNotification() {
        this.notifSubscription?.unsubscribe();
        this.notifSubscription = null;
    }

    subscribeIM(orderId: number) {
        if (!this.stompClient.connected) {
            this.orderToBeSubscribed = orderId;
            return;
        }
        this.imSubscription?.unsubscribe();
        this.imSubscription = this.stompClient.subscribe(
            `/im/receive/${orderId}`,
            (frame) => {
                const msg: IMMessage = JSON.parse(frame.body);
                this.onNewMessage(msg);
            }
        );
    }

    unsubscribeIM() {
        this.imSubscription?.unsubscribe();
        this.imSubscription = null;
        this.orderToBeSubscribed = -1;
    }

    enableStompDebug() {
        this.stompClient.debug = (str) => console.log(str);
    }
}

const websocketService = new WebsocketService();
export default websocketService;
