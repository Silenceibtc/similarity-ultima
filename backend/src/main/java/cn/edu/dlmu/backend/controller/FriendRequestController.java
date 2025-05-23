package cn.edu.dlmu.backend.controller;

import cn.edu.dlmu.backend.common.BaseResponse;
import cn.edu.dlmu.backend.model.request.FriendRequestSendRequest;
import cn.edu.dlmu.backend.utils.ResultUtils;
import cn.edu.dlmu.backend.model.domain.FriendRequest;
import cn.edu.dlmu.backend.model.request.FriendRequestOperateRequest;
import cn.edu.dlmu.backend.service.FriendRequestService;
import cn.edu.dlmu.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/friend/request")
public class FriendRequestController {

    @Resource
    private FriendRequestService friendRequestService;

    @Resource
    private UserService userService;

    @PostMapping("/send")
    public BaseResponse<Boolean> sendFriendRequest(@RequestBody FriendRequestSendRequest request, HttpServletRequest httpRequest) {
        Long senderId = userService.getCurrentUserInfo(httpRequest).getId();
        boolean result = friendRequestService.sendFriendRequest(senderId, request.getReceiverId(), request.getRemark());
        return ResultUtils.success(result);
    }

    @GetMapping("/pending")
    public BaseResponse<List<FriendRequest>> getPendingRequests(HttpServletRequest request) {
        Long userId = userService.getCurrentUserInfo(request).getId();
        List<FriendRequest> requests = friendRequestService.getPendingRequests(userId);
        return ResultUtils.success(requests);
    }

    @PostMapping("/handle")
    public BaseResponse<Boolean> handleFriendRequest(
            @RequestBody FriendRequestOperateRequest friendRequestOperateRequest,
            HttpServletRequest request) {
        boolean result = friendRequestService.handleFriendRequest(friendRequestOperateRequest);
        return ResultUtils.success(result);
    }
}