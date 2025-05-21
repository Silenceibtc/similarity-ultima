package cn.edu.dlmu.backend.model.request;

import lombok.Data;

@Data
public class FriendRequestOperateRequest {
    /**
     * 好友请求ID
     */
    private Long requestId;

    /**
     * 操作状态（1-同意，2-拒绝）
     */
    private Integer status;
}