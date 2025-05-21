package cn.edu.dlmu.backend.service.impl;

import cn.edu.dlmu.backend.common.ErrorCode;
import cn.edu.dlmu.backend.constant.RedisKey;
import cn.edu.dlmu.backend.constant.UserConstant;
import cn.edu.dlmu.backend.exception.BusinessException;
import cn.edu.dlmu.backend.mapper.UserMapper;
import cn.edu.dlmu.backend.model.domain.User;
import cn.edu.dlmu.backend.service.UserService;
import cn.edu.dlmu.backend.utils.AlgorithmUtils;
import cn.edu.dlmu.backend.utils.QiniuCloudUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Silenceibtc
 * @description 针对表【user(用户表)】的数据库操作Service实现
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private QiniuCloudUtil qiniuCloudUtil;

    private static final String SALT = "Silenceibtc";

    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1.校验
        //非空
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        //账户不小于四位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号名称过短");
        }
        //账户不包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。 ，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号名称包含特殊字符");
        }
        //账户不小于四位
        if (userAccount.length() > 12) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号名称过长");
        }
        //密码不小于8位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户密码过短");
        }
        //密码不大于16位
        if (userPassword.length() > 16) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户密码过长");
        }
        //密码与校验码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //账户不能重复（对数据库操作，放在最后以优化性能）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        Long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户重复");
        }

        //2.加密密码
        String handledPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(handledPassword);
        // 通过https://robohash.org/随机生成一个机器人头像作为初始头像
        user.setAvatarUrl("https://robohash.org/" + userAccount);
        user.setUserStatus(0);
        user.setIsDelete(0);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.DB_ERROR, "数据库插入失败");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 参数校验
        // 非空
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        // 账户不小于四位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号名称过短");
        }
        // 账户不包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。 ，、？]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号名称包含特殊字符");
        }
        // 密码不小于8位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户密码过短");
        }

        // 加密密码
        String handledPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", handledPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed, userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }

        // 对用户信息进行脱敏
        User safeUser = getSafeUser(user);
        // 记录用户登录态（session）
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safeUser);

        return safeUser;
    }

    @Override
    public User getSafeUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setGender(originUser.getGender());
        safeUser.setAvatarUrl(originUser.getAvatarUrl());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setIdentity(originUser.getIdentity());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setTags(originUser.getTags());
        safeUser.setProfile(originUser.getProfile());
        return safeUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }


    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 先将所有用户查询出来
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        // 2. 判断是否有指定的标签
        Gson gson = new Gson();
        return userList.stream().filter(user -> {
            String tagStr = user.getTags();
            if (StringUtils.isBlank(tagStr))
                return false;
            Set<String> tempTagNameSet = gson.fromJson(tagStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
    }

    @Override
    public int updateUserInfo(User user, User loginUser) {
        if (!Objects.equals(user.getId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return userMapper.updateById(user);
    }

    @Override
    public int adminUpdateUserInfo(User user, User loginUser) {
        if (!isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return userMapper.updateById(user);
    }

    @Deprecated
    private List<User> searchUsersByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 拼接查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        // 用户查询并脱敏
        List<User> userList = userMapper.selectList(queryWrapper).stream().map(this::getSafeUser).collect(Collectors.toList());
        return userList;
    }

    @Override
    public User getCurrentUserInfo(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return user;
    }


    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getIdentity() == UserConstant.ADMIN;
    }

    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getIdentity() == UserConstant.ADMIN;
    }

    @Override
    public List<User> getHomePageUsers(long pageNum, long pageSize, HttpServletRequest request) {
        User currentUser = getCurrentUserInfo(request);
        String redisKey = String.format(RedisKey.USER_HOMEPAGE_REDIS_KEY, pageNum, pageSize, currentUser.getId());
        // 构建查询条件，随机查询20位用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("ORDER BY RAND() LIMIT 20");
        List<User> userList = this.list(queryWrapper);
        // 用户信息脱敏
        List<User> safeUserList = userList.stream().map(this::getSafeUser).collect(Collectors.toList());
        // 异步写缓存
        CompletableFuture.runAsync(() -> {
            try {
                redisTemplate.opsForValue().set(redisKey, safeUserList, 30000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.error("Redis缓存写入失败，键: {}，错误信息: {}", redisKey, e.getMessage());
            }
        });
        return safeUserList;
    }

    @Override
    public List<User> matchUsers(Long num, User currentUser) {
        // 查询标签不为空的用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        // 处理当前用户标签
        String tags = currentUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 使用最大堆维护TopN最小距离（堆顶为当前堆内最大距离）
        PriorityQueue<Pair<User, Integer>> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getValue() - a.getValue() // 降序排列，堆顶是最大距离
        );
        // 计算所有用户与当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 如果用户没有标签，或者是用户自己，则跳过
            if (StringUtils.isBlank(userTags) || Objects.equals(user.getId(), currentUser.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算编辑距离，编辑距离越小，相似度越高
            Integer distance = AlgorithmUtils.minDistance(tagList, userTagList);
            // 如果堆中元素个数小于num，直接加入堆中
            if (maxHeap.size() < num) {
                maxHeap.offer(new Pair<>(user, distance));
            } else if (maxHeap.peek() != null && distance < maxHeap.peek().getValue()) {
                // 如果当前距离小于堆顶距离，移除堆顶元素，加入当前元素
                // 移除堆顶最大距离
                maxHeap.poll();
                maxHeap.offer(new Pair<>(user, distance));
            }
        }
        // 按照编辑距离由小到大排序前num名用户返回
        List<Pair<User, Integer>> sortedList = new ArrayList<>(maxHeap);
        sortedList.sort(Comparator.comparingInt(Pair::getValue));

        // 提取选中用户的id列表
        List<Long> userIds = sortedList.stream()
                .map(pair -> pair.getKey().getId())
                .collect(Collectors.toList());

        // 根据id列表查询完整的用户信息
        List<User> fullUsers = this.listByIds(userIds);

        // 将完整用户列表转换为以id为键的Map便于快速查找
        Map<Long, User> userMap = fullUsers.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 替换用户对象并返回安全信息
        return sortedList.stream()
                .map(pair -> userMap.get(pair.getKey().getId())) // 替换为完整用户对象
                .filter(Objects::nonNull) // 过滤潜在的空值（防止数据不一致）
                .map(this::getSafeUser)  // 进行安全脱敏处理
                .collect(Collectors.toList());
    }

    @Override
    public String uploadAvatar(MultipartFile file, User currentUser) {
        try {
            String avatarUrl = qiniuCloudUtil.upload(file);
            currentUser.setAvatarUrl(avatarUrl);
            this.updateById(currentUser);
            return avatarUrl;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}




