package cn.edu.dlmu.backend.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 解散队伍请求类
 */
@Data
public class DeleteTeamRequest implements Serializable {

    /**
     * 队伍id
     */
    private Long teamId;

}
