package cn.edu.dlmu.backend.mapper;

import cn.edu.dlmu.backend.model.domain.FriendRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;

public interface FriendRelationMapper extends BaseMapper<FriendRelation> {

    /**
     * 查询用户的好友ID列表
     * @param userId 用户ID
     * @return 好友ID列表
     */
    List<Long> selectFriendIdsByUserId(Long userId);
}