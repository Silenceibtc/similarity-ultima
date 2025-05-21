package cn.edu.dlmu.backend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 群聊表
 * @TableName group_chat
 */
@TableName(value ="group_chat")
@Data
public class GroupChat implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的队伍ID
     */
    private Long teamId;

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 群描述
     */
    private String groupDesc;

    /**
     * 群创建者ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}