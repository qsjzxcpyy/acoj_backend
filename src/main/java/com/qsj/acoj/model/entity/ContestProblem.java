package com.qsj.acoj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@TableName(value = "contest_problem")
@Data
public class ContestProblem implements Serializable {
    /**
     * 自增id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 比赛ID
     */
    private Long contestId;

    /**
     * 题目ID
     */
    private Long problemId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 题目顺序
     */
    private Integer problemOrder;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
} 