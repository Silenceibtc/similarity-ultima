package cn.edu.dlmu.backend.service.impl;

import cn.edu.dlmu.backend.common.ErrorCode;
import cn.edu.dlmu.backend.exception.BusinessException;
import cn.edu.dlmu.backend.mapper.FriendRequestMapper;
import cn.edu.dlmu.backend.mapper.FriendRelationMapper;
import cn.edu.dlmu.backend.model.domain.FriendRequest;
import cn.edu.dlmu.backend.model.domain.FriendRelation;
import cn.edu.dlmu.backend.model.request.FriendRequestOperateRequest;
import cn.edu.dlmu.backend.model.vo.FriendRequestVO;
import cn.edu.dlmu.backend.service.FriendRequestService;
import cn.edu.dlmu.backend.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    @Resource
    private UserService userService;

    @Resource
    private FriendRelationMapper friendRelationMapper;

    @Resource
    private FriendRequestMapper friendRequestMapper;

    @Override
    @Transactional
    public boolean sendFriendRequest(Long senderId, Long receiverId, String remark) {
        // 校验用户是否存在
        if (userService.getById(senderId) == null || userService.getById(receiverId) == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        // 不能给自己发请求
        if (senderId.equals(receiverId)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能添加自己为好友");
        }
        // 检查是否已存在未处理的请求
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("senderId", senderId)
                .eq("receiverId", receiverId)
                .eq("status", 0); // 0-待处理
        if (count(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已存在未处理的好友请求");
        }
        // 插入请求记录
        FriendRequest request = new FriendRequest();
        request.setSenderId(senderId);
        request.setReceiverId(receiverId);
        request.setRemark(remark);
        request.setCreateTime(new Date());
        request.setStatus(0); // 初始状态：待处理
        return save(request);
    }

    @Override
    public List<FriendRequestVO> getPendingRequests(Long userId) {
        return friendRequestMapper.selectPendingRequestsWithUserInfo(userId);
    }

    @Override
    @Transactional
    public boolean handleFriendRequest(FriendRequestOperateRequest friendRequestOperateRequest, Long receivedId) {
        Long requestId = friendRequestOperateRequest.getRequestId();
        Integer status = friendRequestOperateRequest.getStatus(); // 1-同意，2-拒绝
        FriendRequest friendRequest = getById(requestId);
        if (friendRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "请求不存在");
        }
        // 校验当前用户是否是接收者
        if (!friendRequest.getReceiverId().equals(receivedId)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限处理该请求");
        }
        if (friendRequest.getStatus() != 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求已处理");
        }
        // 更新请求状态
        friendRequest.setStatus(status);
        updateById(friendRequest);
        // 同意则插入好友关系表
        if (status == 1) {
            FriendRelation relation = new FriendRelation();
            relation.setUserId(friendRequest.getSenderId());
            relation.setFriendId(friendRequest.getReceiverId());
            relation.setIsAgreed(1);
            friendRelationMapper.insert(relation);
            // 反向插入（用户A添加用户B，同时用户B的好友列表也需要用户A）
            relation = new FriendRelation();
            relation.setUserId(friendRequest.getReceiverId());
            relation.setFriendId(friendRequest.getSenderId());
            relation.setIsAgreed(1);
            friendRelationMapper.insert(relation);
        }
        return true;
    }
}