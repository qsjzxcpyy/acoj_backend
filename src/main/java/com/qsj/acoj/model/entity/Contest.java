package com.qsj.acoj.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value = "contest")
@Data
public class Contest implements Serializable {
    /**
     * 比赛ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 比赛名称
     */
    private String name;

    /**
     * 比赛描述
     */
    private String description;

    /**
     * 比赛开始时间
     */
    private LocalDateTime startTime;

    /**
     * 比赛结束时间
     */
    private LocalDateTime endTime;

    /**
     * 比赛状态
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

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 创建者id
     */
    private Long userId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 