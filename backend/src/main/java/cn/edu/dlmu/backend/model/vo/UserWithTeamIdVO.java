package cn.edu.dlmu.backend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户封装类
 */
@Data
public class UserWithTeamIdVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 性别 0 - 女 1 - 男
     */
    private Integer gender;

    /**
     * 头像url

     */
    private String avatarUrl;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 电话号码
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户身份 0 - 管理员 1 - 普通用户
     */
    private Integer identity;

    /**
     * 标签列表
     */
    private String tags;

    /**
     * 个人简介
     */
    private String profile;

}
