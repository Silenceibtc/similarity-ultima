package cn.edu.dlmu.backend.service.impl;

import cn.edu.dlmu.backend.mapper.FriendRelationMapper;
import cn.edu.dlmu.backend.mapper.UserMapper;
import cn.edu.dlmu.backend.model.domain.FriendRelation;
import cn.edu.dlmu.backend.model.vo.FriendVO;
import cn.edu.dlmu.backend.service.FriendRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FriendRelationServiceImpl extends ServiceImpl<FriendRelationMapper, FriendRelation> implements FriendRelationService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<FriendVO> getFriendList(Long userId) {
        // 查询当前用户的所有好友ID
        List<Long> friendIds = baseMapper.selectFriendIdsByUserId(userId);
        // 关联查询用户信息
        return friendIds.stream()
                .map(userMapper::selectById)
                .map(user -> {
                    FriendVO vo = new FriendVO();
                    vo.setId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setAvatarUrl(user.getAvatarUrl());
                    return vo;
                })
                .collect(Collectors.toList());
    }
}