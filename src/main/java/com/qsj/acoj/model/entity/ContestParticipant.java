package com.qsj.acoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value = "content_participant")
@Data
public class ContestParticipant implements Serializable {
    /**
     * 自增id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 所属比赛ID
     */
    private Long contestId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 报名状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 