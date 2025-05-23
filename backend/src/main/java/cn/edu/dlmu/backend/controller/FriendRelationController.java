package cn.edu.dlmu.backend.controller;

import cn.edu.dlmu.backend.common.BaseResponse;
import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.model.vo.FriendVO;
import cn.edu.dlmu.backend.service.FriendRelationService;
import cn.edu.dlmu.backend.service.UserService;
import cn.edu.dlmu.backend.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendRelationController {

    @Resource
    private FriendRelationService friendRelationService;

    @Resource
    private UserService userService;

    @GetMapping("/list")
    public BaseResponse<List<FriendVO>> getFriendList(HttpServletRequest request) {
        User currentUser = userService.getCurrentUserInfo(request);
        List<FriendVO> friendList = friendRelationService.getFriendList(currentUser.getId());
        return ResultUtils.success(friendList);
    }
}
