package cn.edu.dlmu.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.edu.dlmu.backend.model.domain.UserTeam;
import cn.edu.dlmu.backend.service.UserTeamService;
import cn.edu.dlmu.backend.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author Silenceibtc
* @description 针对表【user_team(用户队伍关系表)】的数据库操作Service实现
* @createDate 2025-04-11 10:58:41
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




