package cn.edu.dlmu.backend.model.vo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class FriendRequestVO implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 请求备注
     */
    private String remark;

    /**
     * 发送人用户名
     */
    private String senderUsername;

    /**
     * 发送人头像URL
     */
    private String senderAvatarUrl;
}