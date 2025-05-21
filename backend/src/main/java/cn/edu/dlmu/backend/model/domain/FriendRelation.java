package cn.edu.dlmu.backend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友关系表
 * @TableName friend_relation
 */
@TableName(value ="friend_relation")
@Data
public class FriendRelation implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（主动添加方）
     */
    private Long userId;

    /**
     * 好友ID（被添加方）
     */
    private Long friendId;

    /**
     * 是否同意（0-未同意，1-已同意）
     */
    private Integer isAgreed;

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