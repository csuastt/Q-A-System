export enum UserRole {
    USER = "USER",
    ANSWERER = "ANSWERER",
    ALL = "USER_AND_ANSWERER",
}

export enum UserGender {
    FEMALE = "FEMALE",
    MALE = "MALE",
    UNKNOWN = "UNKNOWN",
}

export enum UserType {
    Normal,
    Answerer,
}

export enum ManagerRole {
    ADMIN = "ADMIN",
    REVIEWER = "REVIEWER",
    SUPER_ADMIN = "SUPER_ADMIN",
}

export interface ConfigInfo {
    minPrice: number;
    maxPrice: number;
    respondExpirationSeconds: number;
    answerExpirationSeconds: number;
    fulfillExpirationSeconds: number;
    maxChatMessages: number;
    maxChatTimeSeconds: number;
    feeRate: number;
}

export interface UserBasicInfo {
    id: number;
    username: string;
    nickname: string;
    description: string;
    price: number;
    role: UserRole;
}

export type ManagerInfoList = Array<ManagerInfo>;

export interface ManagerInfo {
    id: number;
    username: string;
    password: string;
    deleted: boolean;
    role: ManagerRole;
    createTime: string;
}

export type UserInfoList = Array<UserBasicInfo>;

export interface UserInfo {
    id: number;
    username: string;
    nickname: string;
    email: string;
    phone: string;
    price: number;
    gender: UserGender;
    balance: number;
    description: string;
    role: UserRole;
}

export interface UserFullyInfo {
    id: number;
    username: string;
    password: string;
    nickname: string;
    sign_up_timestamp: number;
    email: string;
    phone: string;
    gender: UserGender;
    balance: number;
    description: string;
    price: number;
    role: UserRole;
}

export enum OrderState {
    CREATED = "CREATED",
    PAYED = "PAYED",
    PAY_TIMEOUT = "PAY_TIMEOUT",
    REVIEWED = "REVIEWED",
    REJECTED_BY_REVIEWER = "REJECTED_BY_REVIEWER",
    ACCEPTED = "ACCEPTED",
    REJECTED_BY_ANSWERER = "REJECTED_BY_ANSWERER",
    RESPOND_TIMEOUT = "RESPOND_TIMEOUT",
    ANSWERED = "ANSWERED",
    ANSWER_TIMEOUT = "ANSWER_TIMEOUT",
    CHAT_ENDED = "CHAT_ENDED",
    FULFILLED = "FULFILLED",
    CANCELLED = "CANCELLED",
}

export const OrderStateMsg: Map<OrderState, string> = new Map([
    [OrderState.CREATED, "已创建"],
    [OrderState.PAYED, "已支付"],
    [OrderState.PAY_TIMEOUT, "支付超时"],
    [OrderState.REVIEWED, "审核通过"],
    [OrderState.REJECTED_BY_REVIEWER, "审核失败"],
    [OrderState.ACCEPTED, "已接单"],
    [OrderState.REJECTED_BY_ANSWERER, "拒绝接单"],
    [OrderState.RESPOND_TIMEOUT, "接单超时"],
    [OrderState.ANSWERED, "已回答"],
    [OrderState.ANSWER_TIMEOUT, "回答超时"],
    [OrderState.CHAT_ENDED, "交流结束"],
    [OrderState.FULFILLED, "交易完成"],
]);

export enum OrderEndReason {
    UNKNOWN,
    ASKER,
    ANSWERER,
    TIME_LIMIT,
    MESSAGE_LIMIT,
    SYSTEM,
}

export interface OrderInfo {
    id: number;
    state: OrderState;
    asker: UserBasicInfo;
    answerer: UserBasicInfo;
    questionTitle: string;
    questionDescription: string;
    createTime: string;
    endReason: OrderEndReason;
    finished: boolean;
    deleted: boolean;
    answer: string;
    price: number;
}

export enum CreationResultType {
    SUCCESS,
    INVALID_INPUT,
}

export interface CreationResult {
    id: number;
    type: CreationResultType;
    state: string;
    created_id: number;
    message: string;
}

export interface PagedList<T> {
    data: Array<T>;
    pageSize: number;
    page: number;
    totalPages: number;
    totalCount: number;
}

export interface EarningsMonthly {
    month: string;
    earnings: number;
}

export interface EarningsInfo {
    total: number;
    monthly: Array<EarningsMonthly>;
}

export interface StatsInfo {
    askCount: number;
    answerCount: number;
}

export interface ManagerStatsInfo {
    //todo
    // askCount: number;
    // answerCount: number;
}

export interface ConfigInfo {
    minPrice: number;
    maxPrice: number;
    respondExpirationSeconds: number;
    answerExpirationSeconds: number;
    fulfillExpirationSeconds: number;
    maxChatMessages: number;
    maxChatTimeSeconds: number;
    feeRate: number;
}

export enum NotificationType {
    PLAIN = "PLAIN",
    NEW_MESSAGE = "NEW_MESSAGE",
    ORDER_STATE_CHANGED = "ORDER_STATE_CHANGED",
    ACCEPT_DEADLINE = "ACCEPT_DEADLINE",
    ACCEPT_TIMEOUT = "ACCEPT_TIMEOUT",
    ANSWER_DEADLINE = "ANSWER_DEADLINE",
    ANSWER_TIMEOUT = "ANSWER_TIMEOUT",
}

export interface Notification {
    notifId: number;
    createTime: string;
    type: NotificationType;
    receiverId: number;
    targetId: number;
    haveRead: boolean;
    msgSummary?: string;
    newState?: OrderState;
    deadline?: string;
}

export interface IMMessage {
    messageId: number;
    senderId: number;
    sendTime: string;
    msgBody: string;
}

export interface AttachmentInfo {
    uuid: number;
    filename: string;
    size: number;
}

export interface FileInfo {
    file: File;
    url: string;
}
