package cn.edu.dlmu.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.dlmu.backend.model.domain.GroupMember;
import cn.edu.dlmu.backend.service.GroupMemberService;
import cn.edu.dlmu.backend.mapper.GroupMemberMapper;
import org.springframework.stereotype.Service;

/**
* @author Silenceibtc
* @description 针对表【group_member(群成员表)】的数据库操作Service实现
* @createDate 2025-05-21 14:52:28
*/
@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember>
    implements GroupMemberService{

}




