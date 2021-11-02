export enum UserRole {
    USER = "USER",
    ANSWERER = "ANSWERER",
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

export interface UserBasicInfo {
    id: number;
    username: string;
    nickname: string;
    avatar: string;
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
    avatar: string;
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
    avatar: string;
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
    CREATED="CREATED",
    PAYED="PAYED",
    PAY_TIMEOUT="PAY_TIMEOUT",
    REVIEWED="REVIEWED",
    REJECTED_BY_REVIEWER="REJECTED_BY_REVIEWER",
    ACCEPTED="ACCEPTED",
    REJECTED_BY_ANSWERER="REJECTED_BY_ANSWERER",
    RESPOND_TIMEOUT="RESPOND_TIMEOUT",
    ANSWERED="ANSWERED",
    ANSWER_TIMEOUT="ANSWER_TIMEOUT",
    CHAT_ENDED="CHAT_ENDED",
    FULFILLED="FULFILLED",
}

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
    question: string;
    createTime: string;
    endReason: OrderEndReason;
    finished: boolean;
    deleted: boolean;
    answerSummary: boolean;
    price: number;
}

export enum CreationResultType {
    SUCCESS,
    INVALID_INPUT,
}

export interface CreationResult {
    type: CreationResultType;
    state: string;
    created_id: number;
    err_msg?: string;
}

export interface PagedList<T> {
    data: Array<T>;
    pageSize: number;
    page: number;
    totalPages: number;
    totalCount: number;
}
