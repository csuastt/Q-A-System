export enum UserType {
    Normal,
    Answerer
}

export interface UserListItem {
    id: number;
    avatarUrl: string;
    name: string;
    introduction: string;
    type: UserType
}
