package cn.edu.dlmu.backend.service;

import cn.edu.dlmu.backend.model.domain.FriendRequest;
import cn.edu.dlmu.backend.model.request.FriendRequestOperateRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface FriendRequestService extends IService<FriendRequest> {

    /**
     * 发送好友请求
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param remark 备注
     * @return 是否成功
     */
    boolean sendFriendRequest(Long senderId, Long receiverId, String remark);

    /**
     * 获取当前用户的好友请求列表（待处理）
     * @param userId 当前用户ID
     * @return 好友请求列表
     */
    List<FriendRequest> getPendingRequests(Long userId);

    /**
     * 处理好友请求（同意/拒绝）
     * @param request 操作请求（包含请求ID、操作类型）
     * @return 是否成功
     */
    boolean handleFriendRequest(FriendRequestOperateRequest request);
}