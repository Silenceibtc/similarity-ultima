package cn.edu.dlmu.backend.mapper;

import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.model.vo.UserWithTeamIdVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author Silenceibtc
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2024-03-25 21:54:56
* @Entity cn.edu.dlmu.back.model.domain.User
*/
public interface UserMapper extends BaseMapper<User> {

    List<UserWithTeamIdVO> selectByTeamIds(List<Long> teamIds);
}




