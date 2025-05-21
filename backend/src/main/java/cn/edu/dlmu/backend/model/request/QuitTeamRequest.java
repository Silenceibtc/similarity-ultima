package cn.edu.dlmu.backend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 加入队伍请求类
 */
@Data
public class QuitTeamRequest implements Serializable {

    /**
     * 队伍id
     */
    private Long teamId;

}
