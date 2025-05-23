export type chatSessionVO = {
    /**
     * 会话ID（单聊为好友ID，群聊为群ID）
     */
    sessionId : number,

    /**
     * 会话类型（0-单聊，1-群聊）
     */
    sessionType: number,

    /**
     * 会话名称（好友昵称/群名称）
     */
    name: string,

    /**
     * 头像URL
     */
    avatarUrl: string,

    /**
     * 最后一条消息内容
     */
    lastMessage: string,

    /**
     * 最后一条消息时间
     */
    lastMessageTime: Date;
}