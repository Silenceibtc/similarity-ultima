package cn.edu.dlmu.backend.model.request;

import lombok.Data;

/**
 * 加入队伍请求类
 */
@Data
public class JoinTeamRequest {

    /**
     * id
     */
    private Long teamId;

    /**
     * 队伍密码
     */
    private String teamPassword;

}
