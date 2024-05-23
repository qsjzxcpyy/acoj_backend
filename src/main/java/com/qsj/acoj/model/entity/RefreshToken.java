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
import java.util.Map;

@TableName("refresh_token")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class RefreshToken  implements Serializable {
    /**
     * create table if not exists refresh_token (
     *     id bigint auto_increment comment 'id' primary key ,
     *     user_id bigint not null ,
     *     refresh_token varchar(128) not null ,
     *     expires_time datetime not null ,
     *     create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
     *     update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
     *     is_delete   tinyint  default 0                 not null comment '是否删除'
     * ) comment '刷新Token';
     */

    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

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
