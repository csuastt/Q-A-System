export enum UserType {
    Normal,
    Answerer,
}

// TODO: backend doesn't support it yet
// export interface UserBasicInfo {
//     id: number;
//     avatarUrl: string;
//     name: string;
//     introduction: string;
//     type: UserType;
// }

export type UserInfoList = Array<UserInfo>;

export interface UserInfo {
    id: number;
    username: string;
    nickname: string;
    ava_url: string;
    sign_up_timestamp: number;
    mail: string;
    gender: string;
    phone: string;
    birthday: string;
    permission: string;
    money: number;
    description: string;
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
    deleted: boolean;
    state: OrderState;
    asker: number;
    answerer: number;
    stem: string;
    question: string;
    createTime: string;
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
