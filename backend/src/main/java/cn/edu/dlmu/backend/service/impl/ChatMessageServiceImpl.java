package cn.edu.dlmu.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.dlmu.backend.model.domain.ChatMessage;
import cn.edu.dlmu.backend.service.ChatMessageService;
import cn.edu.dlmu.backend.mapper.ChatMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author Silenceibtc
* @description 针对表【chat_message(聊天记录表)】的数据库操作Service实现
* @createDate 2025-05-21 14:52:28
*/
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage>
    implements ChatMessageService{

}




