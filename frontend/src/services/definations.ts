export enum UserType {
    Normal,
    Answerer
}

export interface UserBasicInfo {
    id: number;
    avatarUrl: string;
    name: string;
    introduction: string;
    type: UserType;
}

export type UserInfoList = Array<UserBasicInfo>;

export enum QuestionState {
    WAITING_FOR_REVIEW,
    REJECTED_BY_REVIEWER,
    WAITING_TO_BE_ACCEPTED,
    REJECTED_BY_ANSWERER,
    WAITING_FOR_INITIAL_ANSWER,
    COMMUNICATING,
    CANCELLED,
    SOLVED,
    TRANSACTION_COMPLETE
}

export interface QuestionBasicInfo {
    id: number;
    state: QuestionState;
    questionerId: number;
    questionerName: string;
    answererId: number;
    answererName: string;
    stem: string;
    description: string;
    createTime: number;
}

export type QuestionInfoList = Array<QuestionBasicInfo>;

export enum CreationResultType {
    SUCCESS,
    INVALID_INPUT
}

export interface CreationResult {
    type: CreationResultType;
    created_id: number;
    err_msg?: string;
}