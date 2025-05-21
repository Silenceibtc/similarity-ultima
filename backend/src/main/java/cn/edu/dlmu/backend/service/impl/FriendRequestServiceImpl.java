package cn.edu.dlmu.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.dlmu.backend.model.domain.FriendRequest;
import cn.edu.dlmu.backend.service.FriendRequestService;
import cn.edu.dlmu.backend.mapper.FriendRequestMapper;
import org.springframework.stereotype.Service;

/**
* @author Silenceibtc
* @description 针对表【friend_request(好友请求表)】的数据库操作Service实现
* @createDate 2025-05-21 14:52:28
*/
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest>
    implements FriendRequestService{

}




