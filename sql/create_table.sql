

-- 创建库
create database if not exists acoj;

-- 切换库
use acoj;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    unionId      varchar(256)                           null comment '微信开放平台id',
    mpOpenId     varchar(256)                           null comment '公众号openId',
    userName     varchar(256)                           null comment '用户昵称',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
) comment '用户' collate = utf8mb4_unicode_ci;

-- 题目表
create table if not exists question
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    tags        varchar(1024)                      null comment '标签列表，题目类型（json 数组）',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    submitNum   int      default 0                 not null comment '提交数',
    acceptedNum int      default 0                 not null comment '通过数',
    judgeConfig text                               null comment '时间复杂度，空间复杂度 （json 数组）',
    judgeCase   text                               null comment '输入样例输出样例 （json 数组）',
    answer      text                               not null comment '答案',
    userId      bigint                             not null comment '创建用户 id',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '题目' collate = utf8mb4_unicode_ci;

-- 判题任务表
create table if not exists question_submit
(
    id         bigint auto_increment comment 'id' primary key,
    questionId bigint                             not null comment '题目 id',
    userId     bigint                             not null comment '创建用户 id',
    language   varchar(128)                       not null comment '编程语言',
    code       text                               not null comment '提交的代码',
    status     int                                not null default 0 comment '题目测评状态，0 未测评 1 正在测评 2 测评成功 3 测评失败',
    judgeInfo  text                               null comment 'accept wonger answer TLE ....,消耗的时间和占用的空间 （json 数组）',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_questionId (questionId),
    index idx_userId (userId)
) comment '判题任务表';

# -- 帖子收藏表（硬删除）
# create table if not exists post_favour
# (
#     id         bigint auto_increment comment 'id' primary key,
#     postId     bigint                             not null comment '帖子 id',
#     userId     bigint                             not null comment '创建用户 id',
#     createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
#     updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
#     index idx_postId (postId),
#     index idx_userId (userId)
# ) comment '帖子收藏';
create table if not exists access_token (
    id              bigint auto_increment comment 'id' primary key ,
    userId         bigint                             not null ,
    userInfo        varchar(2560)                      not null ,
    accessToken     varchar(128)                       not null ,
    refreshToken    varchar(128)                       not null ,
    expiresTime    datetime                           not null ,
    createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                 not null comment '是否删除'
) comment '访问Token';
create table if not exists refresh_token (
    id bigint auto_increment comment 'id' primary key ,
    userId bigint not null ,
    refreshToken varchar(128) not null ,
    expiresTime datetime not null ,
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
) comment '刷新Token';
