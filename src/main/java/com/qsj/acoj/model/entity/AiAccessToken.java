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

@TableName("ai_access_token")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class AiAccessToken implements Serializable {
    private static final long serialVersionUID = 4129688863211999026L;
    @TableId
    private Long id;

    private String accessToken;

    private LocalDateTime expiresTime;

    private LocalDateTime createTime;
}
