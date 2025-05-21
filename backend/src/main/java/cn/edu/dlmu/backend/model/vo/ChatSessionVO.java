package cn.edu.dlmu.backend.model.vo;

import lombok.Data;
import java.util.Date;

@Data
public class ChatSessionVO {
    /**
     * 会话ID（单聊为好友ID，群聊为群ID）
     */
    private Long sessionId;

    /**
     * 会话类型（0-单聊，1-群聊）
     */
    private Integer sessionType;

    /**
     * 会话名称（好友昵称/群名称）
     */
    private String name;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 最后一条消息内容
     */
    private String lastMessage;

    /**
     * 最后一条消息时间
     */
    private Date lastMessageTime;
}