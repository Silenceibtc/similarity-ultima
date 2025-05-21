package cn.edu.dlmu.backend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 聊天记录表
 * @TableName chat_message
 */
@TableName(value ="chat_message")
@Data
public class ChatMessage implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 聊天类型（0-单聊，1-群聊）
     */
    private Integer chatType;

    /**
     * 发送者ID
     */
    private Long senderId;

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

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}