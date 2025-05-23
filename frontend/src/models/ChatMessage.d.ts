export type ChatMessage = {
    /**
     * 主键
     */
    id: number,

    /**
     * 聊天类型（0-单聊，1-群聊）
     */
    chatType: number,

    /**
     * 发送者ID
     */
    senderId: number,

    /**
     * 接收者ID（单聊为用户ID，群聊为群ID）
     */
    receiverId: number,

    /**
     * 消息内容
     */
    content: string,

    /**
     * 消息类型（0-文本，1-图片，2-文件）
     */
    messageType: number,

    /**
     * 发送时间
     */
    sendTime: Date,
}