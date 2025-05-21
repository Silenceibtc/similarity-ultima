package cn.edu.dlmu.backend.model.vo;

import lombok.Data;

@Data
public class FriendVO {
    private Long id;
    private String username;
    private String avatarUrl;
    // 可根据需求扩展其他字段（如最后聊天时间）
}