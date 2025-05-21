import {UserType} from "./user";

/**
 * 队伍类型
 */
export type TeamType = {

    /**
     * id
     */
    id: number;

    /**
     * 队伍名称
     */
    teamName: string;

    /**
     * 队伍描述
     */
    description?: string;

    /**
     * 队伍最大人数
     */
    maxNum: number;

    /**
     * 队伍已加入人数
     */
    currentNum: number;

    /**
     * 过期时间
     */
    expireTime?: Date;

    /**
     * 创建人id
     */
    userId: number;

    /**
     * 队长id
     */
    leaderId: number;

    /**
     * 队伍密码
     */
    teamPassword?: string;

    /**
     * 0-公开，1-私有，2-加密
     */
    teamStatus: number;

    /**
     * 已加入用户
     */
    userVOList: UserType[];
}