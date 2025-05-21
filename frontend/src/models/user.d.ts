/**
 * 用户类型
 */
export type UserType = {
    id?: number;
    username: string;
    gender: number;
    avatarUrl: string;
    userAccount: string;
    phone?: string;
    email?: string;
    userStatus?: number;
    identity?: number;
    tags: string;
    profile: string;
    createTime: Date;
}