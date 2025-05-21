package cn.edu.dlmu.backend.service;

import cn.edu.dlmu.backend.model.domain.FriendRelation;
import cn.edu.dlmu.backend.model.vo.FriendVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface FriendRelationService extends IService<FriendRelation> {

    /**
     * 获取当前用户的好友列表
     * @param userId 用户ID
     * @return 好友VO列表（包含用户信息）
     */
    List<FriendVO> getFriendList(Long userId);
}