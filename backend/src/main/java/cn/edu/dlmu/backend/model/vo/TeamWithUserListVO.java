package cn.edu.dlmu.backend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 关联用户信息的队伍列表封装类
 */
@Data
public class TeamWithUserListVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String teamName;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 队伍最大人数
     */
    private Integer maxNum;

    /**
     * 队伍当前人数
     */
    private Integer currentNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队长id
     */
    private Long leaderId;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 关联的用户信息
     */
    List<UserWithTeamIdVO> userVOList;

}
