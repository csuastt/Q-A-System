export enum UserType {
    Normal,
    Answerer,
}

export interface UserBasicInfo {
    id: number;
    username: string;
    nickname: string;
    ava_url: string;
    description: string;
    type: UserType;
}

export type UserInfoList = Array<UserBasicInfo>;

export interface UserInfo {
    id: number;
    username: string;
    nickname: string;
    ava_url: string;
    sign_up_timestamp: number;
    email: string;
    phone: string;
    gender: string;
    permission: string;
    money: number;
    description: string;
    type: UserType;
}

export interface UserFullyInfo {
    id: number;
    username: string;
    password: string;
    nickname: string;
    ava_url: string;
    sign_up_timestamp: number;
    email: string;
    phone: string;
    gender: string;
    permission: string;
    money: number;
    description: string;
    price: number;
    type: UserType;
}

export enum OrderState {
    CREATED,
    PAYED,
    PAY_TIMEOUT,
    REVIEWED,
    REJECTED_BY_REVIEWER,
    ACCEPTED,
    REJECTED_BY_ANSWERER,
    RESPOND_TIMEOUT,
    ANSWERED,
    ANSWER_TIMEOUT,
    CHAT_ENDED,
    FULFILLED,
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

export type OrderList = Array<OrderInfo>;

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
