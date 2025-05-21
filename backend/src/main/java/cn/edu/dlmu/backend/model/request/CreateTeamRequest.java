package cn.edu.dlmu.backend.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建队伍请求类
 */
@Data
public class CreateTeamRequest implements Serializable {

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
     * 过期时间
     */
    private Date expireTime;

    /**
     * 队伍密码
     */
    private String teamPassword;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

}
