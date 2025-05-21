package cn.edu.dlmu.backend.service;

import cn.edu.dlmu.backend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Silenceibtc
* @description 针对表【user(用户表)】的数据库操作Service
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser 初始用户
     * @return 脱敏后的用户
     */
    User getSafeUser(User originUser);

    /**
     * 用户注销
     *
     * @param request
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     *
     * @param tagNameList 标签列表
     * @return 符合条件的脱敏后的所有用户
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 用户更新个人信息
     *
     * @param user      要更新的用户信息
     * @param loginUser
     * @return 成功返回1，失败返回0
     */
    int updateUserInfo(User user, User loginUser);

    /**
     * 管理员更新用户信息
     *
     * @param user    要更新的用户信息
     * @param request
     * @return 成功返回1，失败返回0
     */
    int adminUpdateUserInfo(User user, User request);

    /**
     * 获取当前登录的用户信息
     * @param request 与用户建立的链接信息
     * @return 用户信息
     */
    User getCurrentUserInfo(HttpServletRequest request);

    /**
     * 判断当前用户是否为管理员
     * @param request 与用户建立的链接信息
     * @return 是管理员返回true，不是返回false
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 判断当前用户是否为管理员
     * @param user 用户信息
     * @return 是管理员返回true，不是返回false
     */
    boolean isAdmin(User user);

    /**
     * 获取主页推荐用户信息
     * @param pageNum 页号
     * @param pageSize 页大小
     * @param request 用户链接
     * @return 主页推荐用户信息
     */
    List<User> getHomePageUsers(long pageNum, long pageSize, HttpServletRequest request);

    /**
     * 匹配相似的用户
     * @param num 匹配用户数量
     * @param currentUser 当前登录用户
     * @return 匹配到的用户列表
     */
    List<User> matchUsers(Long num, User currentUser);

    /**
     * 上传头像
     * @param file 头像文件
     * @param currentUser 当前登录用户
     * @return 图像url
     */
    String uploadAvatar(MultipartFile file, User currentUser);
}
