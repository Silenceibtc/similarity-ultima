package cn.edu.dlmu.backend.controller;

import cn.edu.dlmu.backend.common.BaseResponse;
import cn.edu.dlmu.backend.model.vo.ChatMessageVO;
import cn.edu.dlmu.backend.utils.ResultUtils;
import cn.edu.dlmu.backend.model.request.MessageSendRequest;
import cn.edu.dlmu.backend.model.vo.ChatSessionVO;
import cn.edu.dlmu.backend.service.ChatMessageService;
import cn.edu.dlmu.backend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatMessageController {

    @Resource
    private ChatMessageService chatMessageService;

    @Resource
    private UserService userService;

    @PostMapping("/send")
    public BaseResponse<Boolean> sendMessage(@RequestBody MessageSendRequest request, HttpServletRequest httpRequest) {
        Long senderId = userService.getCurrentUserInfo(httpRequest).getId();
        boolean result = chatMessageService.sendMessage(
                request.getChatType(),
                senderId,
                request.getReceiverId(),
                request.getContent(),
                request.getMessageType()
        );
        return ResultUtils.success(result);
    }

    @GetMapping("/sessions")
    public BaseResponse<List<ChatSessionVO>> getChatSessions(HttpServletRequest request) {
        Long userId = userService.getCurrentUserInfo(request).getId();
        List<ChatSessionVO> sessions = chatMessageService.getChatSessions(userId);
        return ResultUtils.success(sessions);
    }

    @GetMapping("/message/list")
    public BaseResponse<List<ChatMessageVO>> getChatMessages(
            @RequestParam("targetId") Long targetId,
            @RequestParam("chatType") Integer chatType,
            HttpServletRequest request
    ) {
        Long userId = userService.getCurrentUserInfo(request).getId();
        List<ChatMessageVO> messages = chatMessageService.getChatMessages(userId, targetId, chatType);
        return ResultUtils.success(messages);
    }

}