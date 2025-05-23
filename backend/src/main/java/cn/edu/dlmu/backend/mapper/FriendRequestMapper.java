package cn.edu.dlmu.backend.mapper;

import cn.edu.dlmu.backend.model.domain.FriendRequest;
import cn.edu.dlmu.backend.model.vo.FriendRequestVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
 * @author Silenceibtc
 * @description 针对表【friend_request(好友请求表)】的数据库操作Mapper
 * @createDate 2025-05-21 14:52:28
 * @Entity cn.edu.dlmu.backend.model.domain.FriendRequest
 */
public interface FriendRequestMapper extends BaseMapper<FriendRequest> {

    List<FriendRequestVO> selectPendingRequestsWithUserInfo(@Param("receiverId") Long receiverId);
}




