package cn.edu.dlmu.backend.controller;

import cn.edu.dlmu.backend.common.BaseResponse;
import cn.edu.dlmu.backend.common.ErrorCode;
import cn.edu.dlmu.backend.model.request.PageRequest;
import cn.edu.dlmu.backend.model.vo.PageVO;
import cn.edu.dlmu.backend.utils.ResultUtils;
import cn.edu.dlmu.backend.constant.RedisKey;
import cn.edu.dlmu.backend.exception.BusinessException;
import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.model.request.UserLoginRequest;
import cn.edu.dlmu.backend.model.request.UserRegisterRequest;
import cn.edu.dlmu.backend.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户接口
 *
 * @author Silenceibtc
 */
@RestController
@RequestMapping("/user")
//@CrossOrigin(origins = {"http://localhost:5173"}, allowCredentials = "true")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        //鉴权
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //查询用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        //用户信息脱敏
        List<User> safeUserList = userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
        return ResultUtils.success(safeUserList);
    }

    @GetMapping("/list")
    public BaseResponse<PageVO<List<User>>> getUserList(HttpServletRequest request, PageRequest pageRequest) {
        //鉴权
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //查询用户信息
        PageVO<List<User>> userList = userService.getUserList(pageRequest);
        return ResultUtils.success(userList);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }

    @GetMapping("/homePage")
    public BaseResponse<List<User>> getHomePageUsers(long pageNum, long pageSize, HttpServletRequest request) {
        if (pageNum <= 0 || pageSize <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "分页参数错误");
        }
        // 如果缓存中有数据，直接读缓存
        User currentUser = userService.getCurrentUserInfo(request);
        String redisKey = String.format(RedisKey.USER_HOMEPAGE_REDIS_KEY, pageNum, pageSize, currentUser.getId());
        List<User> userList = (List<User>) redisTemplate.opsForValue().get(redisKey);
        if (userList != null) {
            return ResultUtils.success(userList);
        }
        userList = userService.getHomePageUsers(pageNum, pageSize, request);
        return ResultUtils.success(userList);
    }

    @PostMapping("/delete/{id}")
    public BaseResponse<Boolean> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        // 从session中取出用户信息
        User user = userService.getCurrentUserInfo(request);
        // 判断是否有该用户
        Long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 获取最新的用户信息
        User currentUser = userService.getById(userId);
        User safeUser = userService.getSafeUser(currentUser);
        return ResultUtils.success(safeUser);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUserInfo(@RequestBody User user, HttpServletRequest request) {
        // 校验参数
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户信息为空");
        }
        Long userId = user.getId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        User userToUpdate = userService.getById(userId);
        if (userToUpdate == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 仅本人和管理员可修改
        User loginUser = userService.getCurrentUserInfo(request);
        // 用户
        if (!userService.isAdmin(loginUser)) {
            return ResultUtils.success(userService.updateUserInfo(user, loginUser));
        }
        // 管理员
        return ResultUtils.success(userService.adminUpdateUserInfo(user, loginUser));
    }

    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(Long num, HttpServletRequest request) {
        if (num == null || num <= 0 || num > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUserInfo(request);
        List<User> matchedUserList = userService.matchUsers(num, currentUser);
        return ResultUtils.success(matchedUserList);
    }

    @PostMapping("/upload")
    public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件为空");
        }
        String contentType = file.getContentType();
        if (!(contentType != null && contentType.startsWith("image/"))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只支持图片格式");
        }
        User currentUser = userService.getCurrentUserInfo(request);
        String imgUrl = userService.uploadAvatar(file, currentUser);
        return ResultUtils.success(imgUrl);
    }

}
