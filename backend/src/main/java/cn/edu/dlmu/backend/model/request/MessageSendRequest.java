package cn.edu.dlmu.backend.model.request;

import lombok.Data;

@Data
public class MessageSendRequest {
    /**
     * 聊天类型（0-单聊，1-群聊）
     */
    private Integer chatType;

    /**
     * 接收者ID（单聊为用户ID，群聊为群ID）
     */
    private Long receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型（0-文本，1-图片，2-文件）
     */
    private Integer messageType;
}