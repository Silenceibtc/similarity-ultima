package cn.edu.dlmu.backend.service;

import cn.edu.dlmu.backend.model.domain.ChatMessage;
import cn.edu.dlmu.backend.model.vo.ChatMessageVO;
import cn.edu.dlmu.backend.model.vo.ChatSessionVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {

    /**
     * 发送消息（单聊/群聊）
     * @param chatType 0-单聊，1-群聊
     * @param senderId 发送者ID
     * @param receiverId 接收者ID（单聊为用户ID，群聊为群ID）
     * @param content 消息内容
     * @param messageType 0-文本，1-图片，2-文件
     * @return 是否成功
     */
    boolean sendMessage(Integer chatType, Long senderId, Long receiverId, String content, Integer messageType);

    /**
     * 获取当前用户的聊天会话列表（单聊+群聊）
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ChatSessionVO> getChatSessions(Long userId);

    /**
     * 获取聊天记录
     * @param userId 当前用户ID
     * @param targetId 聊天对象ID（单聊为好友ID，群聊为群ID）
     * @param chatType 聊天类型（0-单聊，1-群聊）
     */
    List<ChatMessageVO> getChatMessages(Long userId, Long targetId, Integer chatType);
}