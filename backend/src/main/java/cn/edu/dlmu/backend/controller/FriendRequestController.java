package cn.edu.dlmu.backend.controller;

import cn.edu.dlmu.backend.common.BaseResponse;
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
    public BaseResponse<Boolean> sendFriendRequest(
            @RequestParam Long receiverId,
            @RequestParam(required = false) String remark,
            HttpServletRequest request) {
        Long senderId = userService.getCurrentUserInfo(request).getId();
        boolean result = friendRequestService.sendFriendRequest(senderId, receiverId, remark);
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
            @RequestBody FriendRequestOperateRequest request,
            HttpServletRequest request1) {
        boolean result = friendRequestService.handleFriendRequest(request);
        return ResultUtils.success(result);
    }
}