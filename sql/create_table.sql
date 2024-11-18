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
    userMailbox  varchar(256)                           not null comment '用户邮箱',
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
create table if not exists access_token
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null,
    userInfo     varchar(2560)                      not null,
    accessToken  varchar(128)                       not null,
    refreshToken varchar(128)                       not null,
    expiresTime  datetime                           not null,
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
) comment '访问Token';
create table if not exists refresh_token
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null,
    refreshToken varchar(128)                       not null,
    expiresTime  datetime                           not null,
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
) comment '刷新Token';

create table if not exists ai_access_token
(
    id          bigint auto_increment comment 'id' primary key,
    accessToken varchar(128)                       not null,
    expiresTime datetime                           not null,
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间'
) comment 'ai_accessToken';

create table if not exists ai_question_chat
(
    id           bigint auto_increment comment 'id' primary key,
    userId       bigint                             not null,
    userName     varchar(128)                       not null,
    userRequest text,
    aiResponse  text                               not null,
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
   index idx_userId (userId)
) comment '对话记录';

CREATE TABLE IF NOT EXISTS contest
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '比赛ID',
    name         VARCHAR(255) NOT NULL COMMENT '比赛名称',
    description  TEXT COMMENT '比赛描述',
    startTime    DATETIME NOT NULL COMMENT '比赛开始时间',
    endTime      DATETIME NOT NULL COMMENT '比赛结束时间',
    status       ENUM('pending', 'ongoing', 'completed') DEFAULT 'pending' NOT NULL COMMENT '比赛状态',
    userId       BIGINT NOT NULL COMMENT '创建者id',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete     TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除'
) COMMENT = '比赛表';

CREATE TABLE IF NOT EXISTS contest_problem
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增id',
    contestId   BIGINT NOT NULL COMMENT '比赛ID',
    problemId   BIGINT NOT NULL COMMENT '题目ID',
    problemOrder       INT NOT NULL COMMENT '题目顺序',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'

) COMMENT = '比赛与题目关联表';

CREATE TABLE IF NOT EXISTS contest_submission
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增id',
    submissionId  BIGINT NOT NULL COMMENT '提交ID',
    contestId      BIGINT NOT NULL COMMENT '比赛ID',    -- 比赛ID
    problemId      BIGINT NOT NULL COMMENT '题目ID'  ,  -- 题目ID
    userId       BIGINT NOT NULL
) COMMENT = '比赛题目提交记录表';

CREATE TABLE IF NOT EXISTS content_participant
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增id',
    contestId   BIGINT NOT NULL COMMENT '所属比赛ID',
    userId      BIGINT NOT NULL COMMENT '用户ID',
    status          ENUM('REGISTERED', 'COMPLETED', 'CANCELLED') DEFAULT 'REGISTERED' COMMENT '报名状态',
    createTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT = '参与者表';
CREATE TABLE IF NOT EXISTS contest_rank
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增id',
    contestId      BIGINT NOT NULL COMMENT '比赛ID',    -- 外键：比赛ID
    participantId  BIGINT NOT NULL COMMENT '参与者ID',  -- 外键：选手ID
    totalScore     INT DEFAULT 0 COMMENT '总得分',      -- 总得分
    totalTime      INT DEFAULT 0 COMMENT '总时间（分钟）',  -- 完成比赛所用的总时间（单位：分钟）
    penaltyTime    INT DEFAULT 0 COMMENT '总罚时（分钟）',  -- 总罚时（单位：分钟）
    solvedProblems INT DEFAULT 0 COMMENT '解决的题目数',  -- 解决的题目数量
    contestRank            INT COMMENT '比赛排名',              -- 比赛排名
    createTime      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    updateTime      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    isDelete        TINYINT DEFAULT 0 NOT NULL COMMENT '是否删除'  -- 删除标记：0 为有效，1 为删除
    ) COMMENT = '比赛排名表';






