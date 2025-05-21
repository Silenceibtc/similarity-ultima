package cn.edu.dlmu.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.dlmu.backend.model.domain.GroupChat;
import cn.edu.dlmu.backend.service.GroupChatService;
import cn.edu.dlmu.backend.mapper.GroupChatMapper;
import org.springframework.stereotype.Service;

/**
* @author Silenceibtc
* @description 针对表【group_chat(群聊表)】的数据库操作Service实现
* @createDate 2025-05-21 14:52:28
*/
@Service
public class GroupChatServiceImpl extends ServiceImpl<GroupChatMapper, GroupChat>
    implements GroupChatService{

}




