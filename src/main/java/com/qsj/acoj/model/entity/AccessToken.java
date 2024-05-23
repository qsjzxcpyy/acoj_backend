package com.qsj.acoj.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@TableName("access_token")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class AccessToken implements Serializable {
    /**
     * create table if not exists access_token (
     *     id              bigint auto_increment comment 'id' primary key ,
     *     user_id         bigint                             not null ,
     *     userInfo        varchar(2560)                      not null ,
     *     accessToken     varchar(128)                       not null ,
     *     refreshToken    varchar(128)                       not null ,
     *     expires_time    datetime                           not null ,
     *     createTime      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
     *     updateTime      datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
     *     isDelete        tinyint  default 0                 not null comment '是否删除'
     * ) comment '访问Token';
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户信息
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String,String> userInfo;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌过期时间
     */
     private LocalDateTime expiresTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}
