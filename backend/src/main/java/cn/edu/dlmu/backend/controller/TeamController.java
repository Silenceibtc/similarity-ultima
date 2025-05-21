package cn.edu.dlmu.backend.controller;

import cn.edu.dlmu.backend.common.BaseResponse;
import cn.edu.dlmu.backend.common.ErrorCode;
import cn.edu.dlmu.backend.utils.ResultUtils;
import cn.edu.dlmu.backend.exception.BusinessException;
import cn.edu.dlmu.backend.model.domain.Team;
import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.model.dto.TeamDTO;
import cn.edu.dlmu.backend.model.request.*;
import cn.edu.dlmu.backend.model.vo.TeamWithUserListVO;
import cn.edu.dlmu.backend.service.TeamService;
import cn.edu.dlmu.backend.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 队伍接口
 *
 * @author Silenceibtc
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    @PostMapping("/create")
    public BaseResponse<Long> createTeam(@RequestBody CreateTeamRequest createTeamRequest, HttpServletRequest request) {
        // 1. 请求参数是否为空
        if (createTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许操作
        User currentUserInfo = userService.getCurrentUserInfo(request);
        Team team = new Team();
        BeanUtils.copyProperties(createTeamRequest, team);
        Long teamId = teamService.createTeam(team, currentUserInfo);
        if (teamId <= 0) {
            throw new BusinessException(ErrorCode.DB_ERROR, "创建队伍失败");
        }
        return ResultUtils.success(teamId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteTeamRequest deleteTeamRequest, HttpServletRequest request) {
        if (deleteTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = deleteTeamRequest.getTeamId();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUserInfo(request);
        boolean res = teamService.deleteTeam(teamId, currentUser);
        return ResultUtils.success(res);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody UpdateTeamRequest updateTeamRequest, HttpServletRequest request) {
        if (updateTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUserInfo(request);
        boolean res = teamService.updateTeam(updateTeamRequest, currentUser);
        if (!res) {
            throw new BusinessException(ErrorCode.DB_ERROR, "更新队伍失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeam(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "获取队伍失败");
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamWithUserListVO>> getTeamList(TeamDTO teamDTO, HttpServletRequest request) {
        boolean isAdmin = userService.isAdmin(request);
        List<TeamWithUserListVO> teamList = teamService.getTeamList(teamDTO, isAdmin);
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/myTeam")
    public BaseResponse<List<TeamWithUserListVO>> getMyTeamList(HttpServletRequest request) {
        User currentUser = userService.getCurrentUserInfo(request);
        List<TeamWithUserListVO> teamList = teamService.getMyTeamList(currentUser.getId());
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/joinedTeam")
    public BaseResponse<List<TeamWithUserListVO>> getJoinedTeamList(HttpServletRequest request) {
        User currentUser = userService.getCurrentUserInfo(request);
        List<TeamWithUserListVO> teamList = teamService.getJoinedTeamList(currentUser.getId());
        return ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamListPage(@RequestBody TeamDTO teamDTO) {
        if (teamDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamDTO, team);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> pageResult = teamService
                .page(new Page<>(teamDTO.getPageNum(), teamDTO.getPageSize()), queryWrapper);
        return ResultUtils.success(pageResult);
    }

    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        if (joinTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUserInfo(request);
        boolean res = teamService.joinTeam(joinTeamRequest, currentUser);
        return ResultUtils.success(res);
    }

    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody QuitTeamRequest quitTeamRequest, HttpServletRequest request) {
        if (quitTeamRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUserInfo(request);
        boolean res = teamService.quitTeam(quitTeamRequest, currentUser);
        return ResultUtils.success(res);
    }

}
