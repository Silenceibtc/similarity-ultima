package cn.edu.dlmu.backend.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友请求表
 * @TableName friend_request
 */
@TableName(value ="friend_request")
@Data
public class FriendRequest implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 发送请求的用户ID
     */
    private Long senderId;

    /**
     * 接收请求的用户ID
     */
    private Long receiverId;

    /**
     * 请求状态（0-待处理，1-同意，2-拒绝）
     */
    private Integer status;

    /**
     * 请求备注
     */
    private String remark;

    /**
     * 请求时间
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