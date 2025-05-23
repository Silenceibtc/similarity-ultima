// src/main/java/cn/edu/dlmu/backend/model/request/FriendRequestSendRequest.java
package cn.edu.dlmu.backend.model.request;

import lombok.Data;

@Data
public class FriendRequestSendRequest {
    private Long receiverId;  // 必传：接收方ID
    private String remark;    // 可选：好友备注
}