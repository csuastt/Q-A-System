export enum UserType {
    Normal,
    Answerer,
}
export enum ManagerType {
    Admin,
    Auditor,
    Observer,
}

export interface UserBasicInfo {
    id: number;
    username: string;
    nickname: string;
    ava_url: string;
    description: string;
    type: UserType;
}
export type ManagerInfoList = Array<ManagerInfo>;

export interface ManagerInfo {
    id: number;
    manager_name: string;
    sign_up_timestamp: number;
    email: string;
    phone: string;
    permission: string;
    password: string;
    type: ManagerType;
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
    WAITING_FOR_REVIEW,
    REJECTED_BY_REVIEWER,
    WAITING_TO_BE_ACCEPTED,
    REJECTED_BY_ANSWERER,
    WAITING_FOR_INITIAL_ANSWER,
    COMMUNICATING,
    CANCELLED,
    SOLVED,
    TRANSACTION_COMPLETE,
}

export interface OrderInfo {
    id: number;
    state: OrderState;
    asker: UserBasicInfo;
    answerer: UserBasicInfo;
    question: string;
    createTime: string;
    endReason: string;
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
