package cn.edu.dlmu.backend.model.dto;

import cn.edu.dlmu.backend.model.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 队伍查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamDTO extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 搜索关键词，同时对名称和描述进行模糊匹配
     */
    private String searchText;

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
     * 创建人id
     */
    private Long userId;

    /**
     * 0-公开，1-私有，2-加密
     */
    private Integer teamStatus;

}
