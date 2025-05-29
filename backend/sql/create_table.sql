create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    username     varchar(256)                       null comment '用户名',
    gender       tinyint                            null comment '性别 0 - 女 1 - 男',
    avatarUrl    varchar(1024)                      null comment '头像url',
    userAccount  varchar(256)                       not null comment '账号',
    userPassword varchar(512)                       not null comment '用户密码',
    phone        varchar(128)                       null comment '电话号码',
    email        varchar(512)                       null comment '邮箱',
    userStatus   int      default 0                 not null comment '用户状态 0 - 正常',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除 0 - 未删除',
    identity     int      default 1                 not null comment '用户身份 0 - 管理员 1 - 普通用户',
    tags         varchar(1024)                      null comment '标签列表 json',
    profile      varchar(512)                       null comment '个人简介'
)
    comment '用户表';

create table team
(
    id           bigint auto_increment comment 'id'
        primary key,
    teamName     varchar(256)                       not null comment '队伍名称',
    description  varchar(1024)                      null comment '队伍描述',
    maxNum       int      default 1                 not null comment '队伍最大人数',
    currentNum   int                                not null comment '队伍当前人数',
    expireTime   datetime                           null comment '过期时间',
    userId       bigint                             null comment '用户id（创建人id）',
    leaderId     bigint                             null comment '队长id',
    teamPassword varchar(512)                       null comment '队伍密码',
    teamStatus   int      default 0                 not null comment '0-公开，1-私有，2-加密',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除 0 - 未删除'
)
    comment '队伍表';

create table user_team
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             null comment '用户id',
    teamId     bigint                             null comment '队伍id',
    joinTime   datetime                           null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除 0 - 未删除'
)
    comment '用户队伍关系表';

create table friend_relation
(
    id         bigint auto_increment comment '主键'
        primary key,
    userId     bigint                             not null comment '用户ID（主动添加方）',
    friendId   bigint                             not null comment '好友ID（被添加方）',
    isAgreed   tinyint  default 0                 not null comment '是否同意（0-未同意，1-已同意）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除（0-未删除，1-已删除）',
    constraint uk_user_friend
        unique (userId, friendId)
)
    comment '好友关系表';

create table friend_request
(
    id         bigint auto_increment comment '主键'
        primary key,
    senderId   bigint                             not null comment '发送请求的用户ID',
    receiverId bigint                             not null comment '接收请求的用户ID',
    status     tinyint  default 0                 not null comment '请求状态（0-待处理，1-同意，2-拒绝）',
    remark     varchar(512)                       null comment '请求备注',
    createTime datetime default CURRENT_TIMESTAMP not null comment '请求时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除（0-未删除，1-已删除）',
    index idx_receiver_status (receiverId, status)
)
    comment '好友请求表';

create table group_chat
(
    id         bigint auto_increment comment '主键'
        primary key,
    teamId     bigint                             not null comment '关联的队伍ID',
    groupName  varchar(256)                       not null comment '群名称',
    groupDesc  varchar(1024)                      null comment '群描述',
    creatorId  bigint                             not null comment '群创建者ID',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除（0-未删除，1-已删除）',
    constraint uk_team_id
        unique (teamId)
)
    comment '群聊表';

create table group_member
(
    id         bigint auto_increment comment '主键'
        primary key,
    groupId    bigint                             not null comment '群ID',
    userId     bigint                             not null comment '用户ID',
    joinTime   datetime default CURRENT_TIMESTAMP not null comment '加入时间',
    isAdmin    tinyint  default 0                 not null comment '是否是管理员（0-普通，1-管理员）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '记录创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除（0-未删除，1-已删除）',
    constraint uk_group_user
        unique (groupId, userId)
)
    comment '群成员表';

create table chat_message
(
    id          bigint auto_increment comment '主键'
        primary key,
    chatType    tinyint                            not null comment '聊天类型（0-单聊，1-群聊）',
    senderId    bigint                             not null comment '发送者ID',
    receiverId  bigint                             not null comment '接收者ID（单聊为用户ID，群聊为群ID）',
    content     text                               not null comment '消息内容',
    messageType tinyint  default 0                 not null comment '消息类型（0-文本，1-图片，2-文件）',
    sendTime    datetime default CURRENT_TIMESTAMP not null comment '发送时间',
    isDelete    tinyint  default 0                 not null comment '是否删除（0-未删除，1-已删除）',
    index idx_chat_receiver (chatType, receiverId)
)
    comment '聊天记录表';

create table match_record
(
    id          bigint auto_increment comment '主键'
        primary key,
    userid      bigint                             not null comment '发起用户id',
    matchUserId bigint                             not null comment '匹配用户id',
    matchStatus tinyint  default 0                 not null comment '匹配状态（0-成功，1-失败）',
    matchType   tinyint  default 0                 not null comment '匹配类型（0-随机匹配，1-智能匹配）',
    matchTime   datetime default CURRENT_TIMESTAMP not null comment '匹配时间',
    isDelete    tinyint  default 0                 not null comment '是否删除（0-未删除，1-已删除）'
)
    comment '匹配记录表';