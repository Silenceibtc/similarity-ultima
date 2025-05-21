package cn.edu.dlmu.backend.service.impl;

import cn.edu.dlmu.backend.common.ErrorCode;
import cn.edu.dlmu.backend.constant.UserConstant;
import cn.edu.dlmu.backend.exception.BusinessException;
import cn.edu.dlmu.backend.mapper.TeamMapper;
import cn.edu.dlmu.backend.mapper.UserMapper;
import cn.edu.dlmu.backend.model.domain.Team;
import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.model.domain.UserTeam;
import cn.edu.dlmu.backend.model.dto.TeamDTO;
import cn.edu.dlmu.backend.model.enums.TeamStatusEnum;
import cn.edu.dlmu.backend.model.request.JoinTeamRequest;
import cn.edu.dlmu.backend.model.request.QuitTeamRequest;
import cn.edu.dlmu.backend.model.request.UpdateTeamRequest;
import cn.edu.dlmu.backend.model.vo.TeamWithUserListVO;
import cn.edu.dlmu.backend.model.vo.UserWithTeamIdVO;
import cn.edu.dlmu.backend.service.TeamService;
import cn.edu.dlmu.backend.service.UserTeamService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserMapper userMapper;

    @Override
    @Transactional
    public Long createTeam(Team team, User currentUser) {

        // 校验人数
        // 1. 队伍人数>1且<=8
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 1 || maxNum > 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        // 2. 队伍标题>=2且<=10
        String teamName = team.getTeamName();
        if (StringUtils.isBlank(teamName) || teamName.length() < 2 || teamName.length() > 10) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名不符合要求");
        }
        // 3. 描述不为空并且描述长度<=50
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // 4. teamStatus是否公开，不传默认为0-公开
        int teamStatus = Optional.ofNullable(team.getTeamStatus()).orElse(0);
        TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
        if (teamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不符合要求");
        }
        // 5. 如果teamStatus为加密状态，一定要有密码，且密码<=16
        String teamPassword = team.getTeamPassword();
        if (TeamStatusEnum.ENCRYPTION.equals(teamStatusEnum) && (StringUtils.isBlank(teamPassword) || teamPassword.length() > 16)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码未设置或设置不符合要求");
        }
        // 6. 超时时间>当前时间
        Date expireTime = team.getExpireTime();
        Date currentTime = new Date();
        if (expireTime != null && currentTime.after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不符合要求");
        }
        // 7. 校验用户最多创建5个队伍
        Long userId = currentUser.getId();
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = this.count();
        if (count >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍过多");
        }
        // 8. 插入队伍信息
        team.setUserId(userId);
        team.setLeaderId(userId);
        boolean save = this.save(team);
        if (!save) {
            throw new BusinessException(ErrorCode.DB_ERROR, "创建队伍失败");
        }
        // 9. 插入用户-队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        Long teamId = team.getId();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());
        save = userTeamService.save(userTeam);
        if (!save) {
            throw new BusinessException(ErrorCode.DB_ERROR, "创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamWithUserListVO> getTeamList(TeamDTO teamDTO, boolean isAdmin) {
        // 1. 组合查询条件
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        if (teamDTO != null) {
            Long id = teamDTO.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }

            String searchText = teamDTO.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("teamName", searchText).or().like("description", searchText));
            }

            String teamName = teamDTO.getTeamName();
            if (StringUtils.isNotBlank(teamName)) {
                queryWrapper.like("teamName", teamName);
            }

            String description = teamDTO.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }

            // 管理员-查看所有房间；普通用户-查看加密和公开的房间
            Integer teamStatus = teamDTO.getTeamStatus();
            TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
            // 1. 优先处理权限异常
            if (!isAdmin && teamStatusEnum == TeamStatusEnum.PRIVATE) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            // 2. 管理员自由查询逻辑
            if (isAdmin && teamStatusEnum != null) {
                queryWrapper.eq("teamStatus", teamStatusEnum.getValue());
            }
            // 3. 普通用户动态过滤逻辑
            if (!isAdmin) {
                if (teamStatusEnum != null) {
                    queryWrapper.eq("teamStatus", teamStatusEnum.getValue());
                } else {
                    // 默认展示公开和加密房间
                    queryWrapper.in("teamStatus",
                            TeamStatusEnum.PUBLIC.getValue(),
                            TeamStatusEnum.ENCRYPTION.getValue());
                }
            }

            Integer maxNum = teamDTO.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }

            Long userId = teamDTO.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
        }
        // 过期时间大于当前时间
        queryWrapper.and(qw -> qw.isNull("expireTime").or().gt("expireTime", new Date()));
        // 2. 查询队伍列表
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        // 3. 关联查询已加入队伍的用户信息
        return getTeamWithUserListVOS(teamList);
    }

    private List<TeamWithUserListVO> getTeamWithUserListVOS(List<Team> teamList) {
        if (teamList == null || teamList.isEmpty()) {
            return null;
        }
        // 3.1 获取所有队伍id
        List<Long> teamIds = teamList.stream().map(Team::getId).collect(Collectors.toList());
        // 3.2 批量查询所有相关用户并按队伍id分组
        Map<Long, List<UserWithTeamIdVO>> usersWithTeamIdMap = userMapper.selectByTeamIds(teamIds).stream()
                .collect(Collectors.groupingBy(UserWithTeamIdVO::getTeamId));

        // 3.3 组装VO
        List<TeamWithUserListVO> teamWithUserListVOList = new ArrayList<>();
        for (Team team : teamList) {
            TeamWithUserListVO teamWithUserListVO = new TeamWithUserListVO();
            BeanUtils.copyProperties(team, teamWithUserListVO);
            List<UserWithTeamIdVO> userWithTeamIdVOList = usersWithTeamIdMap.getOrDefault(team.getId(), Collections.emptyList());
            teamWithUserListVO.setUserVOList(userWithTeamIdVOList);
            teamWithUserListVOList.add(teamWithUserListVO);
        }
        return teamWithUserListVOList;
    }

    @Override
    public boolean updateTeam(UpdateTeamRequest updateTeamRequest, User currentUser) {
        // 1. 判断队伍是否存在
        Long teamId = updateTeamRequest.getId();
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        // 2. 只有管理员或队长可以修改
        hasTeamAuth(currentUser, team);
        // 3. 判断要更新的内容是否符合要求
        // 3.1. 队伍人数>=当前队伍已有人数并且<=8
        Integer maxNum = updateTeamRequest.getMaxNum();
        if (maxNum != null) {
            Integer currentNum = team.getCurrentNum();
            if (!(maxNum >= currentNum && maxNum <= 8)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
            }
            team.setMaxNum(maxNum);
        }
        // 3.2. 队伍标题>=2且<=10
        String teamName = updateTeamRequest.getTeamName();
        if (StringUtils.isNotBlank(teamName)) {
            if (teamName.length() < 2 || teamName.length() > 10) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名不符合要求");
            }
            team.setTeamName(teamName);
        }
        // 3.3. 描述<=50
        String description = updateTeamRequest.getDescription();
        if (StringUtils.isNotBlank(description)) {
            if (description.length() > 50) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
            }
            team.setDescription(description);
        }
        // 3.4. 加密状态必须输入密码，其他则设置密码为null
        TeamStatusEnum newTeamStatusEnum = TeamStatusEnum.getEnumByValue(updateTeamRequest.getTeamStatus());
        if (newTeamStatusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定队伍权限");
        }
        team.setTeamStatus(newTeamStatusEnum.getValue());
        String teamPassword = updateTeamRequest.getTeamPassword();
        if (newTeamStatusEnum == TeamStatusEnum.ENCRYPTION) {
            if (StringUtils.isBlank(teamPassword)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密队伍必须设置密码");
            }
            if (teamPassword.length() > 16) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置过长");
            }
            team.setTeamPassword(teamPassword);
        } else {
            team.setTeamPassword(null);
        }
        // 3.5. 超时时间>当前时间
        Date expireTime = updateTeamRequest.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不符合要求");
        }
        team.setExpireTime(expireTime);
        // 4. 执行更新
        return this.updateById(team);
    }

    private void hasTeamAuth(User currentUser, Team team) {
        if (!Objects.equals(currentUser.getId(), team.getLeaderId()) && currentUser.getIdentity() != UserConstant.ADMIN) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
    }

    @Override
    @Transactional
    public boolean joinTeam(JoinTeamRequest joinTeamRequest, User currentUser) {
        // 1. 队伍存在
        Long teamId = joinTeamRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        // 2. 不能重复加入已加入的队伍
        Long userId = currentUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("userId", userId);
        long count = userTeamService.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入已加入的队伍");
        }
        synchronized (this) {
            // 3. 用户最多加入5个队伍
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            long joinedTeamNum = userTeamService.count(queryWrapper);
            if (joinedTeamNum >= 5) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入队伍数量过多");
            }
            // 4. 只能加入未过期的队伍
            Date expireTime = team.getExpireTime();
            if (expireTime != null && expireTime.before(new Date())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
            }
            // 5. 禁止加入私有的队伍
            Integer teamStatus = team.getTeamStatus();
            TeamStatusEnum teamStatusEnum = TeamStatusEnum.getEnumByValue(teamStatus);
            if (teamStatusEnum == TeamStatusEnum.PRIVATE) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私密队伍");
            }
            // 6. 如果加入的队伍已加密，必须密码匹配
            // 用户输入密码
            String password = joinTeamRequest.getTeamPassword();
            // 队伍密码
            String teamPassword = team.getTeamPassword();
            // 如果队伍为加密，输入密码为空或者密码不匹配，则不允许加入
            if (teamStatusEnum == TeamStatusEnum.ENCRYPTION && (StringUtils.isBlank(password) || !password.equals(teamPassword))) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不匹配");
            }
            // 7. 只能加入未满的队伍
            Integer currentNum = team.getCurrentNum();
            if (Objects.equals(currentNum, team.getMaxNum())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
            }
            // 8. 队伍已有人数加一
            team.setCurrentNum(currentNum + 1);
            boolean update = this.updateById(team);
            if (!update) {
                throw new BusinessException(ErrorCode.DB_ERROR, "更新队伍当前人数失败");
            }
            // 9. 新增用户-队伍关联信息
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(userId);
            userTeam.setTeamId(teamId);
            userTeam.setJoinTime(new Date());
            boolean save = userTeamService.save(userTeam);
            if (!save) {
                throw new BusinessException(ErrorCode.DB_ERROR, "新增用户-队伍关联信息失败");
            }
            return true;
        }
    }

    @Override
    @Transactional
    public boolean quitTeam(QuitTeamRequest quitTeamRequest, User currentUser) {
        // 1. 判断是否已加入该队伍
        Long teamId = quitTeamRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        Long userId = currentUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        queryWrapper.eq("userId", userId);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入该队伍");
        }
        // 2. 用户退出队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        long teamMemberNum = userTeamService.count(queryWrapper);
        // 2.1 只剩一人，队伍解散
        if (teamMemberNum == 1) {
            // 如果队伍只剩一个人，则解散队伍
            boolean removeById = this.removeById(teamId);
            if (!removeById) {
                throw new BusinessException(ErrorCode.DB_ERROR);
            }
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", teamId);
            boolean remove = userTeamService.remove(queryWrapper);
            if (!remove) {
                throw new BusinessException(ErrorCode.DB_ERROR);
            }
            return true;
        }
        // 2.2 不只有一人
        // 2.2.1. 队长退出，权限转给第二早加入的人
        if (Objects.equals(team.getLeaderId(), userId)) {
            // 队长退出
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            queryWrapper.eq("teamId", teamId);
            boolean remove = userTeamService.remove(queryWrapper);
            if (!remove) {
                throw new BusinessException(ErrorCode.DB_ERROR);
            }
            // 权限转给第二早加入的人，现队伍里用户-队伍关系表id最小那条记录的用户id
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("teamId", teamId);
            queryWrapper.last("order by id asc limit 1");
            List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
            if (userTeamList == null || userTeamList.isEmpty()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误");
            }
            UserTeam newLeader = userTeamList.get(0);
            team.setLeaderId(newLeader.getUserId());
            boolean save = this.save(team);
            if (!save) {
                throw new BusinessException(ErrorCode.DB_ERROR);
            }
        } else {
            // 2.2.2 非队长，直接退出，不做其他操作
            queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userId", userId);
            queryWrapper.eq("teamId", teamId);
            boolean remove = userTeamService.remove(queryWrapper);
            if (!remove) {
                throw new BusinessException(ErrorCode.DB_ERROR);
            }
        }
        // 3. 队伍已有人数减一
        team.setCurrentNum(team.getCurrentNum() - 1);
        boolean update = this.updateById(team);
        if (!update) {
            throw new BusinessException(ErrorCode.DB_ERROR, "更新队伍当前人数失败");
        }
        return true;
    }

    @Override
    @Transactional
    public boolean deleteTeam(Long teamId, User currentUser) {
        // 判断是否是队长或管理员
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        hasTeamAuth(currentUser, team);
        // 删除队伍
        boolean removeById = this.removeById(teamId);
        if (!removeById) {
            throw new BusinessException(ErrorCode.DB_ERROR, "删除队伍失败");
        }
        // 删除所有用户-队伍关系
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.DB_ERROR, "删除用户-队伍关系失败");
        }
        return true;
    }

    @Override
    public List<TeamWithUserListVO> getMyTeamList(Long userId) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("leaderId", userId);
        List<Team> teamList = this.list(queryWrapper);
        // 关联查询已加入队伍的用户信息
        return getTeamWithUserListVOS(teamList);
    }

    @Override
    public List<TeamWithUserListVO> getJoinedTeamList(Long userId) {
        // 查询已加入队伍id
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        List<Long> teamIds = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toList());
        // 根据队伍id查询队伍信息
        List<Team> teamList = this.listByIds(teamIds);
        // 关联查询已加入队伍的用户信息
        return getTeamWithUserListVOS(teamList);
    }
}




