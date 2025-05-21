package cn.edu.dlmu.backend.service;

import cn.edu.dlmu.backend.model.domain.Team;
import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.model.dto.TeamDTO;
import cn.edu.dlmu.backend.model.request.JoinTeamRequest;
import cn.edu.dlmu.backend.model.request.QuitTeamRequest;
import cn.edu.dlmu.backend.model.request.UpdateTeamRequest;
import cn.edu.dlmu.backend.model.vo.TeamWithUserListVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     *
     * @param team            队伍信息
     * @param currentUserInfo 当前用户信息
     * @return 队伍id
     */
    Long createTeam(Team team, User currentUserInfo);

    /**
     * 获取队伍列表
     *
     * @param team        查询条件
     * @param isAdmin 是否为管理员
     * @return 关联用户信息的队伍列表
     */
    List<TeamWithUserListVO> getTeamList(TeamDTO team, boolean isAdmin);

    /**
     * 更新队伍信息
     *
     * @param updateTeamRequest 要更新的队伍信息
     * @param currentUser
     * @return 是否更新成功
     */
    boolean updateTeam(UpdateTeamRequest updateTeamRequest, User currentUser);

    /**
     * 加入队伍
     * @param joinTeamRequest 加入队伍请求类
     * @param currentUser 当前登录用户
     * @return 是否加入成功
     */
    boolean joinTeam(JoinTeamRequest joinTeamRequest, User currentUser);

    /**
     * 退出队伍
     * @param quitTeamRequest 退出队伍请求类
     * @param currentUser 当前登录用户
     * @return 是否退出成功
     */
    boolean quitTeam(QuitTeamRequest quitTeamRequest, User currentUser);

    /**
     * 删除队伍
     *
     * @param teamId  队伍id
     * @param currentUser 当前登录用户
     * @return 是否删除成功
     */
    boolean deleteTeam(Long teamId, User currentUser);

    /**
     * 获取我的队伍列表及其关联用户
     * @param userId 创建人id
     * @return 队伍列表及其关联用户
     */
    List<TeamWithUserListVO> getMyTeamList(Long userId);

    /**
     * 获取已加入的队伍以及关联用户
     * @param userId  用户id
     * @return 队伍列表及其关联用户
     */
    List<TeamWithUserListVO> getJoinedTeamList(Long userId);
}
