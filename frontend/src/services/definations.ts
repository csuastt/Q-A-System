import {ZonedDateTime} from 'js-joda'
export enum UserType {
    Normal,
    Answerer,
}
export enum ManagerRole {
    ADMIN="ADMIN",
    REVIEWER="REVIEWER",
    SUPER_ADMIN="SUPER_ADMIN",
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
    username: string;
    password: string;
    deleted: boolean;
    role: ManagerRole;
    createTime: ZonedDateTime;
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
    finished:boolean;
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
