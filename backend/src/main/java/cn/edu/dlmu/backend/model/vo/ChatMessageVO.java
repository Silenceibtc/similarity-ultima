package cn.edu.dlmu.backend.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ChatMessageVO {
    private Long id;
    private Integer chatType;         // 聊天类型（0-单聊，1-群聊）
    private Long senderId;           // 发送者ID
    private String senderAvatar;      // 发送者头像（需从用户表或群表获取）
    private String content;          // 消息内容
    private Date sendTime;           // 发送时间
    // 可根据需要添加更多字段（如是否已读等）
}